package com.panda.app.fitapp.squats

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
import com.panda.app.fitapp.Util.SQuatData
import com.panda.app.fitapp.Util.determineLevelSquat
import com.panda.app.fitapp.database.FitDatabaseDao
import com.panda.app.fitapp.database.FitHistory
import com.panda.app.fitapp.pushup.PushUpsFragment
import kotlinx.coroutines.*
import java.text.SimpleDateFormat
import kotlin.math.sqrt


class SquatsViewModel(
    val database: FitDatabaseDao,
    application: Application, private val level: String, levelCount: Int,private val gender: Int,
    private val weight:Int) :
    AndroidViewModel(application),
    SensorEventListener {

    private lateinit var sensorManager: SensorManager
    private lateinit var accelerometer: Sensor
    val sensorDataLiveData = MutableLiveData<String>()

    private val _increaseSquats = MutableLiveData<Int?>()

    val increaseSquats: LiveData<Int?>
        get() = _increaseSquats

    private val _playChronometer = MutableLiveData<Boolean?>()

    val playChronometer: LiveData<Boolean?>
        get() = _playChronometer


    private val _navigateToLevel = MutableLiveData<Boolean?>()

    /**
     * When true immediately navigate back to the [PushUpsFragment]
     */
    val navigateToLevel: LiveData<Boolean?>
        get() = _navigateToLevel


    private val _navigateToCongratulation = MutableLiveData<Boolean?>()

    /**
     * When true immediately navigate back to the [PushUpsFragment]
     */
    val navigateToCongratulation: LiveData<Boolean?>
        get() = _navigateToCongratulation

    private var squatsCount = 0

    private var count: Int = 0

    private var d: Long = 0L

    private var e: Int = 0

    private var first: Boolean = false

    private var time: Long = 0L

    private var h: Boolean = false

    private var started: Boolean = true


    private var x = 1

    private val _setMaxProgress = MutableLiveData<Int?>()

    val setMaxProgress: LiveData<Int?>
        get() = _setMaxProgress


    private var currentLevel = SQuatData
    private lateinit var levelData: SQuatData.squatLevel


    /**
     * Database
     */
    private var fullSquatCount = 0


    private var viewModelJob = Job()

    private val uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)

    private var currentFit = MutableLiveData<FitHistory?>()

    private var chronometerText : Long = 0L

    var levelSquat = 1

    // register Sensors we need
    init {
        first = true
        _playChronometer.value = false

        if (level == "Easy") {

            levelSquat = determineLevelSquat(levelCount)

            levelData = currentLevel.squatLevels[levelSquat]
            squatsCount = levelData.rep[0]
            _increaseSquats.value = squatsCount
            _setMaxProgress.value = squatsCount

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

    private suspend fun insert(fit: FitHistory) {
        withContext(Dispatchers.IO) {
            database.insert(fit)
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


    private fun saveSquatByProgram() {
        uiScope.launch {
            val oldFit = currentFit.value ?: return@launch

            oldFit.squatCount += fullSquatCount
//            oldFit.squatCountTotal = total

            val oldTimer = oldFit.timer
            chronometerText += oldTimer

            oldFit.timer = chronometerText
            oldFit.squatCountDone = true

            val burn = burnCount()


            val cal = (burn * fullSquatCount)

            oldFit.calorie += cal


            update(oldFit)

        }
    }


    private fun saveSquatByPractice() {
        uiScope.launch {
            val oldFit = currentFit.value ?: return@launch

            oldFit.squatCountPractice += squatsCount

            val oldTimer = oldFit.timer

            chronometerText += oldTimer

            oldFit.timer = chronometerText

            val burn = burnCount()


            val cal = (burn * squatsCount)

            oldFit.calorie += cal



            update(oldFit)

        }
    }

    private suspend fun update(fit: FitHistory) {
        withContext(Dispatchers.IO) {
            database.update(fit)
        }
    }
    // button for increase push count
    fun pauseChrono() {
        _playChronometer.value = false
    }

    private fun onStartTracking() {
        uiScope.launch {
            val newFit = FitHistory()
            newFit.squatCount = fullSquatCount
            newFit.squatCountPractice = squatsCount
            newFit.timer = chronometerText

            val burn = burnCount()

            var cal = (burn * fullSquatCount)

            cal += (burn * squatsCount)

            newFit.calorie += cal


//            newFit.squatCountTotal = total

            if (level == "Easy") {
                newFit.squatCountDone = true
            }
            insert(newFit)

        }
    }



    fun addSquats() {
        add()
    }

    // button for increase push count
    fun playChrono() {
        _playChronometer.value = true
    }

    fun registerSensors() {
        sensorManager =
            getApplication<Application>().getSystemService(Context.SENSOR_SERVICE) as SensorManager
        sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)?.let {
            this.accelerometer = it


        }
        if (sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER) != null) {

            h = sensorManager.registerListener(
                this,
                this.accelerometer,
                SensorManager.SENSOR_DELAY_GAME
            )
        }
    }

    //unregister sensors OnPause
    fun unregisterSensors() {
        sensorManager.unregisterListener(this)
        h = false
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {

    }

    override fun onSensorChanged(sensorEvent: SensorEvent?) {
        val currentTime = System.currentTimeMillis()

        var timeBetween: Long = currentTime - time

        if (timeBetween > 100L) {
            timeBetween = 0L

        }
        time = currentTime

        val data =
            sqrt((sensorEvent!!.values[0] * sensorEvent.values[0] + sensorEvent.values[1] * sensorEvent.values[1]).toDouble())
        val data2 =
            sqrt((data * data) + (sensorEvent.values[2] * sensorEvent.values[2]).toDouble()) - 9.706650161743164

        if (d == 0L) {
            if (data2 < -0.6) {
                d = timeBetween
                e = 0
            }
        } else if (data2 < -0.2) {
            d += timeBetween + d
            e = 0

        } else if (d > 0L) {
            d -= timeBetween
        }
        if (data2 > 0.0) {
            if (d > 225L) {

                e = (e.toLong() + timeBetween).toInt()
                d += timeBetween
                if (data2 > 3.0) {
                    e = (e.toLong() + timeBetween).toInt()

                }
                if (!first) {
                    return
                }
                if (e.toLong() <= 450) {
                    return
                }
                first = false
                count++
                d = 0L
                val sb = StringBuilder()
                sb.append("count: ")
                sb.append(count)
                add()
                return


            }
            d = 0L
            e = 0
            first = true
            return
        }
        if (e <= 0) {
            return
        }
        d
        timeBetween * 2L
        d = 14 - 15
        e = (e - 15)
        first = true


    }
    //decrease squat  count via sensor or button
    private fun decrease() {
        if (squatsCount > 0) {
            squatsCount = squatsCount.dec()
            fullSquatCount += 1
            _increaseSquats.value = squatsCount
        }

        if (squatsCount == 0) {
            while (x < levelData.rep.size && squatsCount == 0) {
                squatsCount = levelData.rep[x]
                _setMaxProgress.value = squatsCount
                _increaseSquats.value = squatsCount
                x++
            }
        }
        if (squatsCount == 0){
            if (currentFit.value == null) {
                onStartTracking()
            } else {
                saveSquatByProgram()

            }

            _playChronometer.value = false
            _navigateToCongratulation.value = true
        }

    }

    //increase squat  count via sensor or button
    private fun add() {
        if (!level.contains("push", ignoreCase = true)){
            if (_playChronometer.value == true) {
                if (level == "Easy") {
                    decrease()
                } else {
                    squatsCount = squatsCount.inc()
                    _increaseSquats.value = squatsCount

                }
                if (started) {
                    started = false

                }
            }
        }else{
            if (level == "Easy") {
                decrease()
            } else {
                squatsCount = squatsCount.inc()
                _increaseSquats.value = squatsCount

            }
            if (started) {
                started = false

            }
        }
    }


    /**
     * Call this immediately after navigating to [PushUpsFragment]
     */
    fun doneNavigatingLevel() {
        _navigateToLevel.value = null
    }


    fun doneNavigatingCongrat() {
        _navigateToCongratulation.value = null
    }

    fun onLevel() {
        if (!level.contains("push", ignoreCase = true)){
            if (currentFit.value == null) {
                onStartTracking()
            } else {
                saveSquatByPractice()

            }
        }

        _navigateToLevel.value = true
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
                if (levelSquat > 3) {
                    burn = 0.26
                }
            }
            weight > 85 -> {
                burn = 0.37
                if (levelSquat > 3) {
                    burn = 0.39
                }
            }
            weight > 100 -> {
                burn = 0.48
                if (levelSquat > 4) {
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