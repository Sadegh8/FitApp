package com.panda.app.fitapp.pushup

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.panda.app.fitapp.database.FitDatabaseDao
import com.panda.app.fitapp.history.HistoryViewModel

class HistoryViewModelFactory(
    private val database: FitDatabaseDao,
    private val application: Application) : ViewModelProvider.Factory {
    @Suppress("unchecked_cast")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HistoryViewModel::class.java)) {
            return HistoryViewModel(database, application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}