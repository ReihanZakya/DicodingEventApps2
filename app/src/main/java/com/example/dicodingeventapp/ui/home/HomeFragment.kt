package com.example.dicodingeventapp.ui.home

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.example.dicodingeventapp.databinding.FragmentHomeBinding
import com.example.dicodingeventapp.ui.Adapter
import com.example.dicodingeventapp.ui.DetailActivity
import com.example.dicodingeventapp.ui.setting.SettingPreferences
import com.example.dicodingeventapp.ui.setting.SettingViewModel
import com.example.dicodingeventapp.ui.setting.SettingViewModelFactory
import com.example.dicodingeventapp.ui.setting.dataStore

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private lateinit var homeViewModel: HomeViewModel
    private lateinit var settingViewModel: SettingViewModel
    private lateinit var upcomingAdapter: Adapter
    private lateinit var finishedAdapter: Adapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Inisialisasi SettingViewModel untuk memantau tema
        val pref = SettingPreferences.getInstance(requireContext().dataStore)
        settingViewModel = ViewModelProvider(this, SettingViewModelFactory(pref)).get(
            SettingViewModel::class.java
        )

        // Observe perubahan tema dan terapkan tema sesuai data
        settingViewModel.getThemeSettings().observe(viewLifecycleOwner) { isDarkModeActive ->
            if (isDarkModeActive) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            }
        }

        homeViewModel = ViewModelProvider(requireActivity(), ViewModelProvider.NewInstanceFactory())[HomeViewModel::class.java]

        setupRecyclerViews()
        setupObservers()
    }

    private fun setupRecyclerViews() {
        upcomingAdapter = Adapter { selectedEvent ->
            val intent = Intent(requireActivity(), DetailActivity::class.java)
            intent.putExtra("EVENT_ID_UP", selectedEvent.id.toString())
            startActivity(intent)
        }
        binding.rvHorizontalHome.layoutManager = LinearLayoutManager(requireActivity(), LinearLayoutManager.HORIZONTAL, false)
        binding.rvHorizontalHome.adapter = upcomingAdapter

        finishedAdapter = Adapter { selectedEvent ->
            val intent = Intent(requireActivity(), DetailActivity::class.java)
            intent.putExtra("EVENT_ID", selectedEvent.id.toString())
            startActivity(intent)
        }
        binding.rvVerticalHome.layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
        binding.rvVerticalHome.adapter = finishedAdapter
    }

    private fun setupObservers() {
        homeViewModel.upcomingEvents.observe(viewLifecycleOwner) { events ->
            val limitedEvents = events.take(5)
            upcomingAdapter.submitList(limitedEvents)
        }

        homeViewModel.finishedEvents.observe(viewLifecycleOwner) { events ->
            finishedAdapter.submitList(events)
        }

        homeViewModel.errorMessage.observe(viewLifecycleOwner) { errorMessage ->
            if (!errorMessage.isNullOrEmpty()) {
                Toast.makeText(requireActivity(), errorMessage, Toast.LENGTH_SHORT).show()
            }
        }

        homeViewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            showLoading(isLoading)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }


}