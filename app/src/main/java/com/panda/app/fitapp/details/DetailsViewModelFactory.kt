package com.panda.app.fitapp.details

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.panda.app.fitapp.database.FitDatabaseDao

class DetailsViewModelFactory(
    private val fitKey: Long,
    private val database: FitDatabaseDao) : ViewModelProvider.Factory {
    @Suppress("unchecked_cast")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(DetailsViewModel::class.java)) {
            return DetailsViewModel(fitKey, database) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}