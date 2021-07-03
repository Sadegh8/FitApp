package com.panda.app.fitapp

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

class MainViewModel(application: Application) : AndroidViewModel(application) {
    private val _navigateToPushUps = MutableLiveData<Boolean?>()

    /**
     * When true immediately navigate back to the [PushUpsFragment]
     */
    val navigateToPushUps: LiveData<Boolean?>
        get() = _navigateToPushUps


    private val _navigateToSquats = MutableLiveData<Boolean?>()

    /**
     * When true immediately navigate back to the [PushUpsFragment]
     */
    val navigateToSquats: LiveData<Boolean?>
        get() = _navigateToSquats



    private val _navigateToSit = MutableLiveData<Boolean?>()

    /**
     * When true immediately navigate back to the [PushUpsFragment]
     */
    val navigateToSit: LiveData<Boolean?>
        get() = _navigateToSit



    private val _navigateToWorkout = MutableLiveData<Boolean?>()

    /**
     * When true immediately navigate back to the [PushUpsFragment]
     */
    val navigateToWorkout: LiveData<Boolean?>
        get() = _navigateToWorkout


    private val _navigateToLevelTest = MutableLiveData<Boolean?>()

    /**
     * When true immediately navigate back to the [PushUpsFragment]
     */
    val navigateToLevelTest: LiveData<Boolean?>
        get() = _navigateToLevelTest


    private val _navigateToHistory = MutableLiveData<Boolean?>()

    /**
     * When true immediately navigate back to the [PushUpsFragment]
     */
    val navigateToHistory: LiveData<Boolean?>
        get() = _navigateToHistory



    /**
     * Call this immediately after navigating to [PushUpsFragment]
     */
    fun doneNavigating() {
        _navigateToPushUps.value = null
    }

    fun onPushUps() {
        _navigateToPushUps.value = true
    }
    // For Squats
    fun doneNavigatingSquats() {
        _navigateToSquats.value = null
    }

    fun onSquats() {
        _navigateToSquats.value = true
    }
    // For Sit ups
    fun doneNavigatingSit() {
        _navigateToSit.value = null
    }

    fun onSitUps() {
        _navigateToSit.value = true
    }


    // For workout
    fun doneNavigatingWorkout() {
        _navigateToWorkout.value = null
    }

    fun onWorkout() {
        _navigateToWorkout.value = true
    }


    // For LevelTest
    fun doneNavigatingLevelTest() {
        _navigateToLevelTest.value = null
    }

    fun onLevelTest() {
        _navigateToLevelTest.value = true
    }

    // For LevelTest
    fun doneNavigatingHistory() {
        _navigateToHistory.value = null
    }

    fun onHistory() {
        _navigateToHistory.value = true
    }
}