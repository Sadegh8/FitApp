package com.panda.app.fitapp.details

import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.ViewModel
import com.panda.app.fitapp.database.FitDatabaseDao
import com.panda.app.fitapp.database.FitHistory
import kotlinx.coroutines.Job


class DetailsViewModel(
    fitKey: Long = 0L,
    dataSource: FitDatabaseDao): ViewModel() {

    val database = dataSource

    private val viewModelJob = Job()

    private val fit = MediatorLiveData<FitHistory>()

    fun getFit() = fit

    init {
        fit.addSource(database.getFitWithId(fitKey), fit::setValue)
    }

    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }

}
