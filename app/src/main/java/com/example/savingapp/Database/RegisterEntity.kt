package com.example.savingapp.Database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "register-table")
data class RegisterEntity(
        @PrimaryKey
        val date: String,
        val price: Float,
        val category: String
    )
