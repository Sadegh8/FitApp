package com.panda.app.fitapp.pushup

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.panda.app.fitapp.database.FitDatabaseDao

class
PushUpsViewModelFactory(
    private val database: FitDatabaseDao,
    private val application: Application,
    private val levelKey: String, private val levelCount: Int,
    private val sitCount: Int,
    private val squatCount: Int,
    private val gender: Int,
    private val weight:Int,
    private val height:Int,
    private val age:Int,
    private val totalLevel:Int): ViewModelProvider.Factory {
    @Suppress("unchecked_cast")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PushUpsViewModel::class.java)) {
            return PushUpsViewModel(
                database, application, levelKey,
                levelCount, gender, weight
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}