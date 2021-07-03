package com.panda.app.fitapp.testLevel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData


class LevelTestViewModel(application: Application) : AndroidViewModel(application){
    private val _navigateToPushUps = MutableLiveData<Boolean?>()

    /**
     * When true immediately navigate back to the [PushUpsFragment]
     */
    val navigateToPushUps: LiveData<Boolean?>
        get() = _navigateToPushUps

    /**
     * Call this immediately after navigating to [PushUpsFragment]
     */
    fun doneNavigating() {
        _navigateToPushUps.value = null
    }

    fun onPushUps() {
        _navigateToPushUps.value = true
    }
}