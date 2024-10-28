package com.example.dicodingeventapp.ui.favorite

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dicodingeventapp.data.FavoriteEventRepository
import com.example.dicodingeventapp.data.local.entity.FavoriteEvent
import kotlinx.coroutines.launch

class FavoriteViewModel(private val repository: FavoriteEventRepository) : ViewModel() {

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> get() = _isLoading

    fun insertFavorite(favoriteEvent: FavoriteEvent) {
        viewModelScope.launch {
            repository.insert(favoriteEvent)
        }
    }

    fun getFavoriteEvents(): LiveData<List<FavoriteEvent>> {
        _isLoading.value = true
        val favorites = repository.getAllFavorites()
        favorites.observeForever {
            _isLoading.value = false
        }
        return favorites
    }

    suspend fun deleteFavoriteEvent(favoriteEvent: FavoriteEvent) {
        repository.deleteFavorite(favoriteEvent)
    }
}