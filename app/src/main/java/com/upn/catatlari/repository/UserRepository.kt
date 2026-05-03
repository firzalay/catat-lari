package com.upn.catatlari.repository

import com.upn.catatlari.data.local.UserDao
import com.upn.catatlari.model.User
import com.upn.catatlari.utils.PasswordHelper

class UserRepository(private val userDao: UserDao) {

    suspend fun register(name: String, email: String, password: String): Result<Unit> {
        return try {
            val existingUser = userDao.getUserByEmail(email)
            if (existingUser != null) {
                Result.failure(Exception("Email sudah terdaftar"))
            } else {
                val hashedPassword = PasswordHelper.hash(password)
                userDao.insertUser(User(name = name, email = email, password = hashedPassword))
                Result.success(Unit)
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun login(email: String, password: String): Result<User> {
        return try {
            val user = userDao.getUserByEmail(email)
            when {
                user == null -> Result.failure(Exception("Email tidak terdaftar"))
                !PasswordHelper.verify(password, user.password) -> Result.failure(Exception("Password salah"))
                else -> Result.success(user)
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updateUser(id: Int, name: String, email: String, password: String): Result<User> {
        return try {
            userDao.updateUser(id, name, email, password)
            val updatedUser = userDao.getUserById(id)
            if (updatedUser != null) {
                Result.success(updatedUser)
            } else {
                Result.failure(Exception("Gagal mengupdate user"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updatePhotoPath(id: Int, photoPath: String?): Result<Unit> {
        return try {
            userDao.updatePhotoPath(id, photoPath)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

}