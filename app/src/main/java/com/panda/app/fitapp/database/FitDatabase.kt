package com.panda.app.fitapp.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [FitHistory::class], version =4, exportSchema = false)
abstract class FitDatabase : RoomDatabase(){

    abstract val fitDatabaseDao: FitDatabaseDao

    companion object{
        @Volatile
        private var INSTANCE: FitDatabase? = null

        fun getInstance(context: Context):FitDatabase{
            synchronized(this){
                var instance = INSTANCE

                if (instance == null){
                    instance = Room.databaseBuilder(
                        context.applicationContext,
                        FitDatabase::class.java,
                        "fit_history_database"
                    )
                        .fallbackToDestructiveMigration()
                        .build()
                    INSTANCE = instance
                }

                return instance

            }
        }
    }

}