package com.panda.app.fitapp.history

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import com.panda.app.fitapp.Util.formatFits
import com.panda.app.fitapp.database.FitDatabaseDao
import com.panda.app.fitapp.database.FitHistory
import kotlinx.coroutines.*

class HistoryViewModel(val database: FitDatabaseDao, application: Application) : AndroidViewModel(application) {
    /**
     * Database
     */
    private var viewModelJob = Job()

    private val uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)

    var fits = database.getAllFitHistory()

    var fitsSort = database.getAllFitHistorySort()


    private var currentFit = MutableLiveData<FitHistory?>()

    private var _showSnackBarEvent = MutableLiveData<Boolean>()

    val showSnackBarEvent : LiveData<Boolean>
        get() = _showSnackBarEvent

    fun donShowingSnackBar(){
        _showSnackBarEvent.value = true
    }

    private var sort: Boolean = false

    val fitStrings = Transformations.map(fits){fits ->
        formatFits(fits, application.resources)
    }

    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }

    fun onClear(){
        uiScope.launch {
            clear()
            currentFit.value = null

        }
    }

    fun onClearOneItem(id:Long){
        uiScope.launch {
            clearItem(id)
        }
    }


    private suspend fun clearItem(id:Long){
        withContext(Dispatchers.IO){
            database.clearItem(id)
        }
    }

    private suspend fun clear(){
        withContext(Dispatchers.IO){
            database.clear()
        }
    }

    fun change(){
        if (sort){
            _showSnackBarEvent.value = false

            sort = false
        }else{
            _showSnackBarEvent.value = true
            sort = true
        }

    }

    private var _navigateToDetail = MutableLiveData<Long>()
        val navigateToDetail
        get() = _navigateToDetail

    fun onFitClicked(id: Long){
        _navigateToDetail.value = id
    }

    fun onDetailedNavigated(){
        _navigateToDetail.value = null
    }

    private var _deleteItem = MutableLiveData<Long>()
    val deleteItem
        get() = _deleteItem

    fun onFitClickedLong(id: Long){
        _deleteItem.value = id
    }

    fun onDetailedNavigatedLong(){
        _deleteItem.value = null
    }


}