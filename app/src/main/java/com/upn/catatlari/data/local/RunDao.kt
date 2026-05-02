package com.upn.catatlari.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.upn.catatlari.model.Run
import androidx.room.Update

@Dao
interface RunDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRun(run: Run)

    @Query("SELECT * FROM runs ORDER BY id DESC")
    suspend fun getAllRuns(): List<Run>

    @Update (onConflict = OnConflictStrategy.REPLACE)
    suspend fun updateRun(run: Run)

}