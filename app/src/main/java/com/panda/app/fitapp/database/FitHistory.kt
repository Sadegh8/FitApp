package com.panda.app.fitapp.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "fit_history_table")
data class FitHistory(
    @PrimaryKey(autoGenerate = true)
    var fitID: Long = 0L,

    @ColumnInfo(name = "push_count")
    var pushCount: Int = 0,

    @ColumnInfo(name = "push_count_total")
    var pushCountTotal: Int = 0,

    @ColumnInfo(name = "push_count_practice")
    var pushCountPractice: Int = 0,

    @ColumnInfo(name = "push_count_done")
    var pushCountDone: Boolean = false,

    @ColumnInfo(name = "sit_count")
    var sitCount: Int = 0,

    @ColumnInfo(name = "sit_count__total")
    var sitCountTotal: Int = 0,

    @ColumnInfo(name = "sit_count_practice")
    var sitCountPractice: Int = 0,

    @ColumnInfo(name = "sit_count_done")
    var sitCountDone: Boolean = false,

    @ColumnInfo(name = "squat_count")
    var squatCount: Int = 0,

    @ColumnInfo(name = "squat_count_total")
    var squatCountTotal: Int = 0,

    @ColumnInfo(name = "squat_count_practice")
    var squatCountPractice: Int = 0,

    @ColumnInfo(name = "squat_count_done")
    var squatCountDone: Boolean = false,

    @ColumnInfo(name = "calorie")
    var calorie: Double = 0.0,

    @ColumnInfo(name = "date_milli")
    var date: Long = System.currentTimeMillis(),

    @ColumnInfo(name = "timer_second")
    var timer: Long = 0L,

    @ColumnInfo(name = "gender")
    var gender: Int = 0,

    @ColumnInfo(name = "weight")
    var weight: Int = 75,

    @ColumnInfo(name = "height")
    var height: Int = 178,

    @ColumnInfo(name = "age")
    var age: Int = 25

)