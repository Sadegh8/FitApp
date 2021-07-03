package com.panda.app.fitapp.pushup

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.panda.app.fitapp.database.FitDatabaseDao
import com.panda.app.fitapp.sitUps.SitUpsViewModel

class SitUpViewModelFactory(
    private val database: FitDatabaseDao,
    private val application: Application,
    private val levelKey: String,
    private val levelCount: Int,
    private val gender: Int,
    private val weight:Int
  ) : ViewModelProvider.Factory {
    @Suppress("unchecked_cast")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SitUpsViewModel::class.java)) {
            return SitUpsViewModel(database, application,levelKey,levelCount,gender,weight) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}