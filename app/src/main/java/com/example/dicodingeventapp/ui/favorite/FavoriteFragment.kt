package com.example.dicodingeventapp.ui.favorite

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.dicoding.applicationdicodingevent.data.response.ListEventsItem
import com.example.dicodingeventapp.data.FavoriteEventRepository
import com.example.dicodingeventapp.data.local.room.AppDatabase
import com.example.dicodingeventapp.databinding.FragmentFavoriteBinding
import com.example.dicodingeventapp.ui.Adapter
import com.example.dicodingeventapp.ui.DetailActivity


class FavoriteFragment : Fragment() {
    private lateinit var binding: FragmentFavoriteBinding
    private lateinit var viewModel: FavoriteViewModel
    private lateinit var adapter: Adapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentFavoriteBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = Adapter { event ->
            val intent = Intent(requireContext(), DetailActivity::class.java)
            intent.putExtra("EVENT_ID", event.id)
            startActivity(intent)
        }
        binding.rvFavoriteEvent.adapter = adapter
        binding.rvFavoriteEvent.layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)

        val repository = FavoriteEventRepository(AppDatabase.getDatabase(requireContext()).favoriteEventDao())
        val factory = ViewModelFactory(repository)
        viewModel = ViewModelProvider(this, factory).get(FavoriteViewModel::class.java)

        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }

        viewModel.getFavoriteEvents().observe(viewLifecycleOwner) { favoriteEvents ->
            adapter.submitList(favoriteEvents.map { event ->
                ListEventsItem(
                    id = event.id,
                    name = event.name,
                    mediaCover = event.mediaCover
                )
            })
        }
    }
}