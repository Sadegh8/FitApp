package com.panda.app.fitapp.sitUps

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.panda.app.fitapp.Util.SitData
import com.panda.app.fitapp.Util.determineLevelSit
import com.panda.app.fitapp.database.FitDatabaseDao
import com.panda.app.fitapp.database.FitHistory
import com.panda.app.fitapp.pushup.PushUpsFragment
import kotlinx.coroutines.*
import java.text.SimpleDateFormat
import kotlin.math.abs

class SitUpsViewModel(
    val database: FitDatabaseDao,
    application: Application, private val level: String, levelCount: Int,private val gender: Int,
    private val weight:Int) : AndroidViewModel(application),
    SensorEventListener {
    private lateinit var sensorManager: SensorManager
    private lateinit var accelerometer: Sensor

    val sensorDataLiveData = MutableLiveData<String>()

    private val _increaseSitUps = MutableLiveData<Int?>()

    val increaseSitUps: LiveData<Int?>
        get() = _increaseSitUps

    private val _playChronometer = MutableLiveData<Boolean?>()

    val playChronometer: LiveData<Boolean?>
        get() = _playChronometer



    private val _navigateToSquat = MutableLiveData<Boolean?>()

    /**
     * When true immediately navigate back to the [PushUpsFragment]
     */
    val navigateToSquat: LiveData<Boolean?>
        get() = _navigateToSquat


    private var sitCount = 0

    private var started: Boolean = true


    var count: Int = 0

    var d: Int = 0

    var e: Boolean = true

    var sensor: Boolean = false

    private var x = 1

    private val _setMaxProgress = MutableLiveData<Int?>()

    val setMaxProgress: LiveData<Int?>
        get() = _setMaxProgress


    private val _navigateToSquatFrom = MutableLiveData<Boolean?>()

    /**
     * When true immediately navigate back to the [PushUpsFragment]
     */
    val navigateToSquatFrom: LiveData<Boolean?>
        get() = _navigateToSquatFrom



    private var currentLevel = SitData
    private lateinit var levelData: SitData.sitLevel

    /**
     * Database
     */
    private var fullSitCount = 0


    private var viewModelJob = Job()

    private val uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)

    private var currentFit = MutableLiveData<FitHistory?>()

    private var chronometerText : Long = 0L

    var levelSit = 1

    init {
        e = true
        count = 0
        _playChronometer.value = false
        if (level == "Easy") {

            val levelSit =  determineLevelSit(levelCount)

            levelData = currentLevel.sitLevels[levelSit]
            sitCount = levelData.rep[0]
            _increaseSitUps.value = sitCount
            _setMaxProgress.value = sitCount
        }


        initializeFit()

    }

    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }

    private fun initializeFit() {
        uiScope.launch {
            currentFit.value = getCurrentFromDatabase()
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
                if (convertLongToDateString(fit.date) != convertLongToDateString(System.currentTimeMillis())) {
                    fit = null
                }
            } else {
                fit = null
            }
            fit
        }

    }


    private fun saveSitByProgram() {
        uiScope.launch {
            val oldFit = currentFit.value ?: return@launch

            oldFit.sitCount += fullSitCount

//            oldFit.sitCountTotal = total

            val oldTimer = oldFit.timer
            chronometerText += oldTimer

            oldFit.timer = chronometerText

            oldFit.sitCountDone = true

            val burn = burnCount()

            val cal = (burn * fullSitCount)

            oldFit.calorie += cal


            update(oldFit)

        }
    }


    private fun saveSitByPractice() {
        uiScope.launch {
            val oldFit = currentFit.value ?: return@launch

            oldFit.sitCountPractice += sitCount

            val oldTimer = oldFit.timer

            chronometerText += oldTimer

            oldFit.timer = chronometerText

            val burn = burnCount()

            val cal = (burn * sitCount)

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
            newFit.sitCount = fullSitCount
            newFit.sitCountPractice = sitCount
            newFit.timer = chronometerText
//            newFit.sitCountTotal = total

            val burn = burnCount()

            var cal = (burn * fullSitCount)

            cal += (burn * sitCount)

            newFit.calorie += cal

            if (level == "Easy") {
                newFit.sitCountDone = true
            }
            insert(newFit)

        }
    }


    fun addSitUps() {
        add()
    }

    // button for increase push count
    fun playChrono() {
        _playChronometer.value = true
    }

    // register Sensors we need
    fun registerSensors() {
        sensorManager =
            getApplication<Application>().getSystemService(Context.SENSOR_SERVICE) as SensorManager
        sensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION)?.let {
            this.accelerometer = it

        }
        if (sensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION) != null) {

            sensor = sensorManager.registerListener(
                this,
                this.accelerometer,
                SensorManager.SENSOR_DELAY_GAME
            )
        }
    }

    //unregister sensors OnPause
    fun unregisterSensors() {
        sensorManager.unregisterListener(this)
        sensor = false
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {

    }

    override fun onSensorChanged(event: SensorEvent?) {
        val var2 = event!!.values[2].toDouble()
        val var4 = event.values[1].toDouble()
        if (e) {
            if (abs(var2) < 50.0 && abs(var4) < 170.0 && d == 0) {
                d = 1
            }
            if (var2 > 60.0 || abs(var4) < 30.0) {
                if (d == 1) {
                    e = false
                    ++count
                    d = 0
                    add()
                }
            }
        } else {
            if (var2 > 60.0) {
                d = 1
            }
            if (var2 <= 60.0 && abs(var4) < 30.0) {
                d = 1
            }
            if (abs(var2) < 50.0) {
                if (abs(var4) > 170.0) {
                    if (d == 1) {
                        e = !e
                    }
                }
            }
        }

    }
    // button for increase push count
    fun pauseChrono() {
        _playChronometer.value = false
    }


    //decrease sit up count via sensor or button
    private fun decrease() {
        if (sitCount > 0) {
            sitCount = sitCount.dec()
            fullSitCount += 1

            _increaseSitUps.value = sitCount
        }

        if (sitCount == 0) {
            while (x < levelData.rep.size && sitCount == 0) {
                sitCount = levelData.rep[x]
                _setMaxProgress.value = sitCount
                _increaseSitUps.value = sitCount
                x++
            }
        }
        if (sitCount == 0){
            if (currentFit.value == null) {
                onStartTracking()
            } else {
                saveSitByProgram()

            }

            _playChronometer.value = false
            _navigateToSquatFrom.value = true

        }

    }

    //increase sit up count via sensor or button
    private fun add() {
        if (!level.contains("push", ignoreCase = true)){
            if (_playChronometer.value == true) {
                if (level == "Easy") {
                    decrease()
                } else {
                    sitCount = sitCount.inc()
                    _increaseSitUps.value = sitCount

                }
                if (started) {
                    started = false
                }
            }
        }else{
            if (level == "Easy") {
                decrease()
            } else {
                sitCount = sitCount.inc()
                _increaseSitUps.value = sitCount

            }
            if (started) {
                started = false

            }
        }
    }


    /**
     * Call this immediately after navigating to [PushUpsFragment]
     */
    fun doneNavigatingSquat() {
        _navigateToSquat.value = null
    }

    fun doneNavigatingSquatFrom() {
        _navigateToSquatFrom.value = null
    }


    fun onSquat() {
            if (!level.contains("push", ignoreCase = true)){
            if (currentFit.value == null) {
                onStartTracking()
            } else {
                saveSitByPractice()

            }
        }

        _navigateToSquat.value = true
    }


    @SuppressLint("SimpleDateFormat")
    private fun convertLongToDateString(systemTime: Long): Long {
        return SimpleDateFormat("yyyyMMdd")
            .format(systemTime).toLong()
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

    private fun burnCount(): Double {
        var burn = 0.18
        when {
            weight > 75 -> {
                burn = 0.21
                if (levelSit > 3) {
                    burn = 0.26
                }
            }
            weight > 85 -> {
                burn = 0.37
                if (levelSit > 3) {
                    burn = 0.39
                }
            }
            weight > 100 -> {
                burn = 0.48
                if (levelSit > 4) {
                    burn = 0.51
                }
            }
        }
        if (gender == 1){
            burn -= 0.04
        }

        return burn
    }
}

