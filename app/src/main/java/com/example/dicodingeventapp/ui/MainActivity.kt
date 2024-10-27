package com.example.dicodingeventapp.ui;

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.dicoding.applicationdicodingevent.data.response.DetailEventResponse
import com.dicoding.applicationdicodingevent.data.response.EventResponse
import com.dicoding.applicationdicodingevent.data.response.ListEventsItem
import com.dicoding.applicationdicodingevent.data.retrofit.ApiConfig
import com.example.dicodingeventapp.databinding.ActivityMainBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    companion object {
        private const val TAG = "MainActivity"
        private const val ACTIVE_ID = 0
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.hide()

        val layoutManager = LinearLayoutManager(this)
        binding.rvEvents.layoutManager = layoutManager
        val itemDecoration = DividerItemDecoration(this, layoutManager.orientation)
        binding.rvEvents.addItemDecoration(itemDecoration)

    }


}