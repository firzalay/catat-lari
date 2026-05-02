package com.upn.catatlari.repository

import com.upn.catatlari.data.local.RunDao
import com.upn.catatlari.model.Run

class RunRepository(private val runDao: RunDao) {

    suspend fun addRun(run: Run): Result<Unit> {
        return try {
            runDao.insertRun(run)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getAllRuns(): Result<List<Run>> {
        return try {
            val runs = runDao.getAllRuns()
            Result.success(runs)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updateRun(run: Run) {
        runDao.updateRun(run)
    }

    suspend fun deleteRun(run: Run): Result<Unit> {
        return try {
            runDao.deleteRun(run)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}