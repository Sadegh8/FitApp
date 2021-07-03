package com.panda.app.fitapp.Util

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.work.*
import com.panda.app.fitapp.MainActivity
import com.panda.app.fitapp.R
import java.util.*
import java.util.concurrent.TimeUnit

class MyWorker(context: Context, workerParams: WorkerParameters) : Worker(context, workerParams) {

    var mContext: Context = context
    lateinit var notificationManager: NotificationManager
    lateinit var notificationChannel: NotificationChannel
    lateinit var builder: Notification.Builder
    private val channelId ="com.panda.app.fitapp"
    var TAG = "periodic_work_fit"

    var  hour = 12
    var  min = 0
    override fun doWork(): Result {

        notificationManager = mContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val intent = Intent(mContext, MainActivity::class.java)

        val pendingIntent = PendingIntent.getActivity(mContext,0,intent,PendingIntent.FLAG_UPDATE_CURRENT)


        val  sharedPref = mContext.getSharedPreferences("g",Context.MODE_PRIVATE)!!

        hour = sharedPref.getInt("hour", 12)

        min = sharedPref.getInt("min", 0)



        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notificationChannel = NotificationChannel(channelId,"Remind",NotificationManager.IMPORTANCE_HIGH)
            notificationChannel.enableLights(true)
            notificationChannel.lightColor = Color.GREEN
            notificationChannel.enableVibration(true)
            notificationManager.createNotificationChannel(notificationChannel)

            builder = Notification.Builder(mContext,channelId)
                .setContentTitle("Get fit Reminder")
                .setContentText("Don't forget to do your daily exercise")
                .setSmallIcon(R.drawable.ic_stat_noti_icon)
                .setContentIntent(pendingIntent)

        }else{
            builder = Notification.Builder(mContext)
                .setContentTitle("Reminder")
                .setContentText("Don't forget to do your daily exercise")
                .setSmallIcon(R.drawable.ic_stat_noti_icon)
                .setContentIntent(pendingIntent)

        }
        notificationManager.notify(1258,builder.build())



        val currentDate = Calendar.getInstance()
        val dueDate = Calendar.getInstance()
        // Set Execution around 05:00:00 AM
        dueDate.set(Calendar.HOUR_OF_DAY, hour)
        dueDate.set(Calendar.MINUTE, min)
        dueDate.set(Calendar.SECOND, 0)
        if (dueDate.before(currentDate)) {
            dueDate.add(Calendar.HOUR_OF_DAY, 24)
        }
        val timeDiff = dueDate.timeInMillis - currentDate.timeInMillis
        val dailyWorkRequest = OneTimeWorkRequest.Builder(MyWorker::class.java)
            .setInitialDelay(timeDiff, TimeUnit.MILLISECONDS)
            .addTag(TAG)
            .build()
        WorkManager.getInstance(applicationContext)
            .enqueue(dailyWorkRequest)


        return try {
            Result.success()
        } catch (e: Exception) {
            Result.failure()
        }

    }

}

