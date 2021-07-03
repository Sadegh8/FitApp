package com.panda.app.fitapp.mainworkout

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

class WorkoutViewModel(application: Application) : AndroidViewModel(application) {

    private val _navigateToPushUp = MutableLiveData<Boolean?>()

    /**
     * When true immediately navigate back to the [PushUpsFragment]
     */
    val navigateToPushUp: LiveData<Boolean?>
        get() = _navigateToPushUp


    /**
     * Call this immediately after navigating to [PushUpsFragment]
     */
    fun doneNavigatingPush() {
        _navigateToPushUp.value = null
    }

    fun onPushUp() {
        _navigateToPushUp.value = true
    }


}