package com.example.savingapp.Database

import android.app.Application

class DatabaseApp: Application() {
    val db by lazy{
        RegisterDatabase.getInstance(this)
    }
}