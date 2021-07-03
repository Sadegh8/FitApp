package com.panda.app.fitapp.profile

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.panda.app.fitapp.database.FitDatabaseDao

class ProfileViewModelFactory(
    private val database: FitDatabaseDao,
    private val application: Application) : ViewModelProvider.Factory {
    @Suppress("unchecked_cast")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ProfileViewModel::class.java)) {
            return ProfileViewModel(database, application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}