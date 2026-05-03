package com.upn.catatlari.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.upn.catatlari.model.User

@Dao
interface UserDao {

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertUser(user: User)

    @Query("SELECT * FROM users WHERE email = :email LIMIT 1")
    suspend fun getUserByEmail(email: String): User?

    @Query("SELECT * FROM users WHERE id = :id LIMIT 1")
    suspend fun getUserById(id: Int): User?

    @Query("UPDATE users SET name = :name, email = :email, password = :password WHERE id = :id")
    suspend fun updateUser(id: Int, name: String, email: String, password: String)

    @Query("UPDATE users SET photoPath = :photoPath WHERE id = :id")
    suspend fun updatePhotoPath(id: Int, photoPath: String?)

}