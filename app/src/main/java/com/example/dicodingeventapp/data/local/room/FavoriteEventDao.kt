package com.example.dicodingeventapp.data.local.room

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.dicodingeventapp.data.local.entity.FavoriteEvent

@Dao
interface FavoriteEventDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFavorite(event: FavoriteEvent)

    @Delete
    suspend fun deleteFavorite(event: FavoriteEvent)

    @Query("SELECT * FROM favorite_event WHERE id = :id")
    fun getFavoriteEventById(id: String): LiveData<FavoriteEvent>

    @Query("SELECT * FROM favorite_event")
    fun getAllFavoritesLiveData(): LiveData<List<FavoriteEvent>>

    @Query("DELETE FROM favorite_event WHERE id = :id")
    suspend fun deleteFavoriteById(id: String)
}