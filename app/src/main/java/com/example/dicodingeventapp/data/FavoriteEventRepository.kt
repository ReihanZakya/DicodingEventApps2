package com.example.dicodingeventapp.data

import androidx.lifecycle.LiveData
import com.example.dicodingeventapp.data.local.entity.FavoriteEvent
import com.example.dicodingeventapp.data.local.room.FavoriteEventDao

class FavoriteEventRepository(private val favoriteEventDao: FavoriteEventDao) {

    suspend fun insert(favoriteEvent: FavoriteEvent) {
        favoriteEventDao.insertFavorite(favoriteEvent)
    }

    fun getAllFavorites(): LiveData<List<FavoriteEvent>> {
        return favoriteEventDao.getAllFavoritesLiveData()
    }

    suspend fun insertFavorite(event: FavoriteEvent) {
        favoriteEventDao.insertFavorite(event)
    }

    suspend fun deleteFavorite(event: FavoriteEvent) {
        favoriteEventDao.deleteFavorite(event)
    }

    suspend fun deleteFavoriteById(id: String) {
        favoriteEventDao.deleteFavoriteById(id)
    }
}