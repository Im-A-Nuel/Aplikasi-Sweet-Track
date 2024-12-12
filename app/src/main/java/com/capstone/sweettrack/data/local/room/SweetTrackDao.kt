package com.capstone.sweettrack.data.local.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.capstone.sweettrack.data.local.entity.FavoriteFood
import com.capstone.sweettrack.data.local.entity.HistoryScan

@Dao
interface SweetTrackDao {

    // **HistoryScan Table**

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHistory(history: HistoryScan)

    @Query("SELECT * FROM history_scan WHERE user_id = :userId ORDER BY timestamp DESC")
    suspend fun getAllHistories(userId: Int): List<HistoryScan>

    @Query("DELETE FROM history_scan WHERE id = :id AND user_id = :userId")
    suspend fun deleteHistoryById(id: Int, userId: Int)

    @Query("DELETE FROM history_scan")
    suspend fun clearHistory()

    // **FavoriteFood Table**

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFavorite(food: List<FavoriteFood>)

    @Query("SELECT * FROM favorite_food WHERE user_id = :userId ORDER BY name ASC")
    suspend fun getAllFavorites(userId: Int): List<FavoriteFood>

    @Query("DELETE FROM favorite_food WHERE id = :id AND user_id = :userId")
    suspend fun deleteFavoriteById(id: Int, userId: Int)

    @Query("DELETE FROM favorite_food")
    suspend fun clearFavorites()


    @Query("SELECT * FROM history_scan WHERE name LIKE '%' || :name || '%'")
    suspend fun searchHistoryByName(name: String): List<HistoryScan>

    @Query("SELECT COUNT(*) FROM favorite_food WHERE name = :name")
    suspend fun isFoodFavorite(name: String): Int
}