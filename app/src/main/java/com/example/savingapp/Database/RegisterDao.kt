package com.example.savingapp.Database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface RegisterDao {
    @Insert
    suspend fun insert(registerEntity: RegisterEntity)

    @Query("SELECT * FROM `register-table`")
    fun fetchAllDates(): Flow<List<RegisterEntity>>

    @Delete
    suspend fun deleteEntry(registerEntity: RegisterEntity)
}
