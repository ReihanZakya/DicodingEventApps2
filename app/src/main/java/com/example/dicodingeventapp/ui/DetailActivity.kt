package com.example.dicodingeventapp.ui;

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.text.HtmlCompat
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.dicoding.applicationdicodingevent.data.response.DetailEventResponse
import com.dicoding.applicationdicodingevent.data.response.Event
import com.dicoding.applicationdicodingevent.data.retrofit.ApiConfig
import com.example.dicodingeventapp.R
import com.example.dicodingeventapp.data.local.entity.FavoriteEvent
import com.example.dicodingeventapp.data.local.room.AppDatabase
import com.example.dicodingeventapp.databinding.ActivityDetailEventBinding
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DetailActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDetailEventBinding
    private lateinit var database: AppDatabase
    private var mediaCover: String? = null
    private var isFavorite = false

    companion object {
        private const val TAG = "DetailEventActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailEventBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.hide()

        findEventDetail()

        // Inisialisasi database
        database = AppDatabase.getDatabase(this)


        // Dapatkan event ID dan mulai observasi status favorite
        val eventId = intent.getStringExtra("EVENT_ID")
        observeFavoriteStatus(eventId)

        // Set aksi pada tombol favorite
        binding.btnFavorite.setOnClickListener {
            if (isFavorite) {
                removeFavorite(eventId)
            } else {
                insertFavorite()
            }
        }
    }

    private fun observeFavoriteStatus(eventId: String?) {
        if (eventId != null) {
            database.favoriteEventDao().getFavoriteEventById(eventId).observe(this) { favoriteEvent ->
                if (favoriteEvent != null) {
                    // Jika event sudah ditambahkan ke favorites
                    binding.btnFavorite.setImageResource(R.drawable.ic_favorite_24)
                    isFavorite = true
                } else {
                    // Jika event belum ada di favorites
                    binding.btnFavorite.setImageResource(R.drawable.ic_favorite_border_24)
                    isFavorite = false
                }
            }
        }
    }



    private fun findEventDetail() {
        val eventId = intent.getStringExtra("EVENT_ID")
        showLoading(true)
        if(eventId != null) {
            val client = ApiConfig.getApiService().getDetailEvent(eventId)
            client.enqueue(object : Callback<DetailEventResponse> {
                override fun onResponse(call: Call<DetailEventResponse>, response: Response<DetailEventResponse>) {
                    showLoading(false)
                    if (response.isSuccessful) {
                        val responseBody = response.body()
                        if(responseBody != null) {
                            setEventData(responseBody.event)
                        }
                    } else {
                        Log.e(TAG, "onFailure: ${response.message()}")
                    }
                }


                override fun onFailure(call: Call<DetailEventResponse>, t: Throwable) {
                    showLoading(false)
                    Log.e(TAG, "onFailure: ${t.message}")
                }
            })
        }

    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    private fun setEventData(event: Event?) {
        if (event != null) {
            mediaCover = event.mediaCover
            Glide.with(binding.imgDetail.context)
                .load(event.mediaCover)
                .into(binding.imgDetail)
            binding.tvDetailName.text = event.name
            binding.tvDetailSummary.text = event.summary
            binding.tvDetailOwnerName.text = event.ownerName
            val remainingQuota = (event.quota ?: 0) - (event.registrants ?: 0)
            binding.tvDetailQuota.text = remainingQuota.toString()
            binding.tvDetailBeginTime.text = event.beginTime
            binding.tvDetailDescription.text =
                HtmlCompat.fromHtml(
                    event.description.toString(),
                    HtmlCompat.FROM_HTML_MODE_LEGACY
                )

            binding.btnDetail.setOnClickListener {
                val url = event.link
                if (!url.isNullOrEmpty()) {
                    val intent = Intent(Intent.ACTION_VIEW)
                    intent.data = Uri.parse(url)
                    startActivity(intent)
                } else {
                    Log.e(TAG, "Link is null or empty")
                }
            }
        } else {
            Log.e(TAG, "Event data is null")
        }
    }

    // Fungsi untuk menambahkan event ke database
    private fun insertFavorite() {
        val eventId = intent.getStringExtra("EVENT_ID") ?: return
        val eventName = binding.tvDetailName.text.toString()

        val favoriteEvent = FavoriteEvent(id = eventId, name = eventName, mediaCover = mediaCover)

        // Menjalankan proses insert di dalam coroutine
        lifecycleScope.launch {
            try {
                database.favoriteEventDao().insertFavorite(favoriteEvent)
                Log.d(TAG, "Event added to favorites: $eventName")
            } catch (e: Exception) {
                Log.e(TAG, "Failed to add favorite: ${e.message}")
            }
        }
    }

    private fun removeFavorite(eventId: String?) {
        if (eventId != null) {
            lifecycleScope.launch {
                try {
                    database.favoriteEventDao().deleteFavoriteById(eventId)
                    Log.d(TAG, "Event removed from favorites")
                } catch (e: Exception) {
                    Log.e(TAG, "Failed to remove favorite: ${e.message}")
                }
            }
        }
    }

}