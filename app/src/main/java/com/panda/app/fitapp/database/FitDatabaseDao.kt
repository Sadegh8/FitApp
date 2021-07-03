package com.panda.app.fitapp.database

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface FitDatabaseDao {

    @Insert
    fun insert(fitHistory: FitHistory)

    @Update
    fun update(fitHistory: FitHistory)

    @Query("SELECT * FROM fit_history_table WHERE fitID = :key")
    fun get(key: Long): FitHistory

    @Query("DELETE FROM fit_history_table")
    fun clear()

    @Query("DELETE FROM fit_history_table WHERE fitID = :key")
    fun clearItem(key: Long)

    @Query("SELECT * FROM fit_history_table ORDER BY fitID DESC")
    fun getAllFitHistory(): LiveData<List<FitHistory>>

    @Query("SELECT * FROM fit_history_table ORDER BY fitID ASC")
    fun getAllFitHistorySort(): LiveData<List<FitHistory>>

    @Query("SELECT * FROM fit_history_table ORDER BY fitID DESC LIMIT 1")
    fun getCurrentFit(): FitHistory?

    @Query("SELECT * FROM fit_history_table ORDER BY push_count_practice DESC LIMIT 1")
    fun getPushPracticeMax(): FitHistory?

    @Query("SELECT * FROM fit_history_table ORDER BY push_count DESC LIMIT 1")
    fun getPushProgramMax(): FitHistory?

    @Query("SELECT * from fit_history_table WHERE fitID = :key")
    fun getFitWithId(key: Long): LiveData<FitHistory>

    @Query("SELECT SUM(push_count) AS value FROM fit_history_table")
    fun getPushTotal(): LiveData<Long>

    @Query("SELECT SUM(push_count_practice) AS value FROM fit_history_table")
    fun getPushTotalFree(): LiveData<Long>

    @Query("SELECT SUM(sit_count) AS value FROM fit_history_table")
    fun getSitTotal(): LiveData<Long>

    @Query("SELECT SUM(sit_count_practice) AS value FROM fit_history_table")
    fun getSitTotalFree(): LiveData<Long>

    @Query("SELECT SUM(squat_count) AS value FROM fit_history_table")
    fun getSquatTotal(): LiveData<Long>

    @Query("SELECT SUM(squat_count_practice) AS value FROM fit_history_table")
    fun getSquatTotalFree(): LiveData<Long>


}