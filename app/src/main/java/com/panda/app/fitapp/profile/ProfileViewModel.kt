package com.panda.app.fitapp.profile

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.panda.app.fitapp.Util.convertLongToDateStringCompareDay
import com.panda.app.fitapp.database.FitDatabaseDao
import com.panda.app.fitapp.database.FitHistory
import kotlinx.coroutines.*


class ProfileViewModel(val database: FitDatabaseDao,
                       application: Application): AndroidViewModel(application) {

    /**
     * Database
     */
    private var viewModelJob = Job()

    private val uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)

    private var currentFit = MutableLiveData<FitHistory?>()

    var totalPush = database.getPushTotal()

    var totalPushFree = database.getPushTotalFree()


    var totalSit = database.getSitTotal()

    var totalSitFree = database.getSitTotalFree()


    var totalSquat = database.getSquatTotal()

    var totalSquatFree = database.getSquatTotalFree()


    init {
        initializeFit()
    }

    private fun initializeFit() {
        uiScope.launch {
            currentFit.value = getCurrentFromDatabase()
        }
    }

    private suspend fun getCurrentFromDatabase(): FitHistory? {
        return withContext(Dispatchers.IO) {
            var fit = database.getCurrentFit()
            if (fit != null) {
                if (convertLongToDateStringCompareDay(fit.date) != convertLongToDateStringCompareDay(System.currentTimeMillis())) {
                    fit = null
                }
            } else {
                fit = null
            }
            fit
        }
    }

     fun updateGender(gender :Int) {
        uiScope.launch {
            val oldFit = currentFit.value ?: return@launch

            oldFit.gender = gender

            update(oldFit)
        }
    }

    fun updateWeight(weight: Int) {
        uiScope.launch {
            val oldFit = currentFit.value ?: return@launch
            oldFit.weight = weight

            update(oldFit)
        }
    }

    fun updateHeight(height : Int) {
        uiScope.launch {
            val oldFit = currentFit.value ?: return@launch

            oldFit.height = height

            update(oldFit)
        }
    }

    fun updateAge(age : Int) {
        uiScope.launch {
            val oldFit = currentFit.value ?: return@launch

            oldFit.age = age

            update(oldFit)
        }
    }



    private suspend fun update(fit: FitHistory) {
        withContext(Dispatchers.IO) {
            database.update(fit)
        }
    }

}