package com.upn.catatlari.model

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize
import java.util.Date

@Parcelize
@Entity(tableName = "runs")
data class Run(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val runTitle: String,
    val runLocation: String,
    val runDate: String,
    val runDistance: Int,
    val runDuration: Int
) : Parcelable