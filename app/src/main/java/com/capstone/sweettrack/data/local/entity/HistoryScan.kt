package com.capstone.sweettrack.data.local.entity

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity(tableName = "history_scan")
data class HistoryScan(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id") val id: Int = 0,
    @ColumnInfo(name = "user_id") val userId: Int = 0,
    @ColumnInfo(name = "image_uri") val imageUri: String,
    @ColumnInfo(name = "name") val name: String,
    @ColumnInfo(name = "kalori") val kalori: Double,
    @ColumnInfo(name = "gula") val gula: Double,
    @ColumnInfo(name = "lemak") val lemak: Double,
    @ColumnInfo(name = "protein") val protein: Double,
    @ColumnInfo(name = "timestamp") val timestamp: Long
): Parcelable