package com.panda.app.fitapp.pushup

import android.app.Application
import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.panda.app.fitapp.Util.*
import com.panda.app.fitapp.database.FitDatabaseDao
import com.panda.app.fitapp.database.FitHistory
import kotlinx.coroutines.*


class PushUpsViewModel(
    val database: FitDatabaseDao,
    application: Application,
    private val level: String,
    levelCount: Int,
    private val gender: Int,
    private val weight: Int,
) :
    AndroidViewModel(application),
    SensorEventListener {

    private lateinit var sensorManager: SensorManager
    private lateinit var proximity: Sensor
    val sensorDataLiveData = MutableLiveData<String>()

    private val _incressPushUps = MutableLiveData<Int?>()

    val incressPushUps: LiveData<Int?>
        get() = _incressPushUps


    private val _setMaxProgress = MutableLiveData<Int?>()

    val setMaxProgress: LiveData<Int?>
        get() = _setMaxProgress


    private val _playChronometer = MutableLiveData<Boolean?>()

    val playChronometer: LiveData<Boolean?>
        get() = _playChronometer


    private val _navigateToSitUps = MutableLiveData<Boolean?>()

    /**
     * When true immediately navigate back to the [PushUpsFragment]
     */
    val navigateToSitUps: LiveData<Boolean?>
        get() = _navigateToSitUps

    private val _navigateToSitUpsFrom = MutableLiveData<Boolean?>()

    /**
     * When true immediately navigate back to the [PushUpsFragment]
     */
    val navigateToSitUpsFrom: LiveData<Boolean?>
        get() = _navigateToSitUpsFrom


    private var pushCount = 0

    private var fullPushCount = 0


    private var x = 1

    private var started: Boolean = true

    private var currentLevel = LevelData
    private var currentLevelSit = SitData
    private var currentLevelSquat = SQuatData



    private lateinit var levelData: LevelData.psuhLevel
    private lateinit var sitData: SitData.sitLevel
    private lateinit var squatData: SQuatData.squatLevel


    private var total = 0

    private var totalSit = 0

    private var totalSquat = 0


    /**
     * Database
     */
    private var viewModelJob = Job()

    private val uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)

    private var currentFit = MutableLiveData<FitHistory?>()

    private var chronometerText : Long = 0L

    var levelPush = 1



    init {
        _playChronometer.value = false
        if (level == "Easy") {

            val levelPush = determineLevelPush(levelCount)

            levelData = currentLevel.pushLevels[levelPush]


            pushCount = levelData.rep[0]
            _incressPushUps.value = pushCount
            _setMaxProgress.value = pushCount

            var v = 0
            while (v < levelData.rep.size) {
                total += levelData.rep[v]
                v++
            }

            val levelSIt =  determineLevelSit(levelCount)

            sitData = currentLevelSit.sitLevels[levelSIt]

            var d = 0
            while (d < sitData.rep.size) {
                totalSit += sitData.rep[d]
                d++
            }

            val levelSquat =  determineLevelSquat(levelCount)

            squatData = currentLevelSquat.squatLevels[levelSquat]


            var t = 0
            while (t < squatData.rep.size) {
                totalSquat += squatData.rep[t]
                t++
            }
        }



        initializeFit()

    }

    private fun initializeFit() {
        uiScope.launch {
            currentFit.value = getCurrentFromDatabase()
        }
    }

    // button for increase push count
    fun addPushUps() {
        add()
    }


    // button for increase push count
    fun playChrono() {
        _playChronometer.value = true
    }

    // button for increase push count
    fun pauseChrono() {
        _playChronometer.value = false
    }


    // register Sensors we need
    fun registerSensors() {
        sensorManager =
            getApplication<Application>().getSystemService(Context.SENSOR_SERVICE) as SensorManager
        sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY)?.let {
            this.proximity = it

        }
        if (sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY) != null) {
            sensorManager.registerListener(this, this.proximity, SensorManager.SENSOR_DELAY_NORMAL)
        }
    }

    //unregister sensors OnPause
    fun unregisterSensors() {
        sensorManager.unregisterListener(this)

    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {

    }

    override fun onSensorChanged(event: SensorEvent?) {
        if (0f == event!!.values[0]) {
            add()
        }
    }

    //decrease push up count via sensor or button
    private fun decrease() {
        if (pushCount > 0) {
            pushCount = pushCount.dec()
            fullPushCount += 1
            _incressPushUps.value = pushCount
        }

        if (pushCount == 0) {
            while (x < levelData.rep.size && pushCount == 0) {
                pushCount = levelData.rep[x]
                _setMaxProgress.value = pushCount
                _incressPushUps.value = pushCount
                x++
            }
        }
        if (pushCount == 0) {

            if (currentFit.value == null) {
                onStartTracking()
            } else {
                savePushByProgram()

            }
            _playChronometer.value = false
            _navigateToSitUpsFrom.value = true

        }

    }


    //increase push up count via sensor or button
    private fun add() {
        if (level != "level") {
            if (_playChronometer.value == true) {
                if (level == "Easy") {
                    decrease()
                } else {
                    pushCount = pushCount.inc()
                    _incressPushUps.value = pushCount

                }
                if (started) {
                    started = false
                }
            }
        }else{
            if (level.equals("Easy")) {
                decrease()
            } else {
                pushCount = pushCount.inc()
                _incressPushUps.value = pushCount

            }
            if (started) {
//                _playChronometer.value = true
                started = false

            }
        }
    }


    /**
     * Call this immediately after navigating to [PushUpsFragment]
     */
    fun doneNavigatingSit() {
        _navigateToSitUps.value = null
    }

    fun doneNavigatingSitFrom() {
        _navigateToSitUpsFrom.value = null
    }

    fun onSitUps() {
        if (level != "level") {
            if (currentFit.value == null) {
                onStartTracking()
            } else {
                savePushByPractice()

            }
        }
        _navigateToSitUps.value = true
    }



    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }

    private fun savePushByProgram() {
        uiScope.launch {
            val oldFit = currentFit.value ?: return@launch

            oldFit.pushCount += fullPushCount
            oldFit.pushCountTotal = total
            oldFit.sitCountTotal = totalSit
            oldFit.squatCountTotal = totalSquat
            oldFit.pushCountDone = true


            val oldTimer = oldFit.timer
            chronometerText += oldTimer

            val burn = burnCount()

            val cal = (burn * fullPushCount)

            oldFit.calorie += cal


            oldFit.timer = chronometerText

            update(oldFit)
        }
    }

    private fun burnCount(): Double {
        var burn = 0.18
        when {
            weight > 75 -> {
                burn = 0.21
                if (levelPush > 3) {
                    burn = 0.26
                }
            }
            weight > 85 -> {
                burn = 0.37
                if (levelPush > 3) {
                    burn = 0.39
                }
            }
            weight > 100 -> {
                burn = 0.48
                if (levelPush > 4) {
                    burn = 0.51
                }
            }
        }
        if (gender == 1){
            burn -= 0.04
        }

        return burn
    }


    private fun savePushByPractice() {
        uiScope.launch {
            val oldFit = currentFit.value ?: return@launch

            oldFit.pushCountPractice += pushCount

            val oldTimer = oldFit.timer

            chronometerText += oldTimer

            oldFit.timer = chronometerText

            val burn = burnCount()

            val cal = (burn * pushCount)

            oldFit.calorie += cal

            update(oldFit)

        }
    }

    private suspend fun update(fit: FitHistory) {
        withContext(Dispatchers.IO) {
            database.update(fit)
        }
    }

    private fun onStartTracking() {
        uiScope.launch {
            val newFit = FitHistory()
            newFit.pushCount = fullPushCount
            newFit.pushCountPractice = pushCount
            newFit.timer = chronometerText
            newFit.pushCountTotal = total
            newFit.sitCountTotal = totalSit
            newFit.squatCountTotal = totalSquat


            val burn = burnCount()

            var cal = (burn * fullPushCount)

            cal += (burn * pushCount)

            newFit.calorie += cal


            if (level.equals("Easy")) {
                newFit.pushCountDone = true
            }
                insert(newFit)
        }
    }

    private suspend fun insert(night: FitHistory) {
        withContext(Dispatchers.IO) {
            database.insert(night)
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



     fun getChronometer(text : String){
         chronometerText = getIntValueFromString(text)
     }

    private fun getIntValueFromString(value : String): Long {
        var returnValue = ""
        value.forEach {
            val item = it.toString().toIntOrNull()
            if(item is Int){
                returnValue += item.toString()
            }

        }
        return returnValue.toLong()

    }


}
