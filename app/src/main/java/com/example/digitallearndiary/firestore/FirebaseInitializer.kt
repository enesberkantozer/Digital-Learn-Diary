package com.example.digitallearndiary.firestore

import android.app.Application

class FirebaseInitializer : Application() {
    lateinit var container: FirebaseContainer

    override fun onCreate() {
        super.onCreate()
        container = FirebaseContainer(this)
    }
}