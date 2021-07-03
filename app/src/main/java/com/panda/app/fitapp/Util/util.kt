package com.panda.app.fitapp.Util

import android.annotation.SuppressLint
import android.content.res.Resources
import android.os.Build
import android.text.Html
import android.text.Spanned
import android.widget.TextView
import androidx.core.text.HtmlCompat
import androidx.recyclerview.widget.RecyclerView
import com.panda.app.fitapp.R
import com.panda.app.fitapp.database.FitHistory
import java.text.DecimalFormat
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.roundToInt


@SuppressLint("SimpleDateFormat")
fun convertLongToDateString(systemTime: Long): String {
    return SimpleDateFormat("LLL dd, yyyy")
        .format(systemTime).toString()

//    MM-dd-yyyy
}

fun getTotalForProgress(item: FitHistory): Int {
    val totalPush = item.pushCountTotal

    val totalSit = item.sitCountTotal

    val totalSquat = item.squatCountTotal

    return totalPush + totalSit + totalSquat
}

fun getTotalUserForProgress(item: FitHistory): Int {
    val totalPush = item.pushCountTotal

    val totalSit = item.sitCountTotal

    val totalSquat = item.squatCountTotal


    var totalUserPush = item.pushCount

    var totalUserSit = item.sitCount

    var totalUserSquat = item.squatCount


    if (item.pushCountDone) {
        totalUserPush = totalPush
    }

    if (item.sitCountDone) {
        totalUserSit = totalSit
    }

    if (item.squatCountDone) {
        totalUserSquat = totalSquat
    }

    return totalUserPush + totalUserSit + totalUserSquat
}


fun getVisibilityProgress(item: FitHistory): Boolean {

    val totalPush = item.pushCountTotal

    val totalSit = item.sitCountTotal

    val totalSquat = item.squatCountTotal


    var totalUserPush = item.pushCount

    var totalUserSit = item.sitCount

    var totalUserSquat = item.squatCount


    if (item.pushCountDone) {
        totalUserPush = totalPush
    }

    if (item.sitCountDone) {
        totalUserSit = totalSit
    }

    if (item.squatCountDone) {
        totalUserSquat = totalSquat
    }

    val totalUser = totalUserPush + totalUserSit + totalUserSquat

    val total = totalPush + totalSit + totalSquat

    return !(total == totalUser && total != 0)

}



fun getPushPractice(item: FitHistory): String{
   return item.pushCount.toString()

}

fun getPushFree(item: FitHistory): String{
    return item.pushCountPractice.toString()

}

fun getPercents(item: FitHistory): String{
    val totalPush = item.pushCountTotal

    val totalSit = item.sitCountTotal

    val totalSquat = item.squatCountTotal

    var totalUserPush = item.pushCount

    var totalUserSit = item.sitCount

    var totalUserSquat = item.squatCount


    if (item.pushCountDone) {
        totalUserPush = totalPush
    }

    if (item.sitCountDone) {
        totalUserSit = totalSit
    }

    if (item.squatCountDone) {
        totalUserSquat = totalSquat
    }



    val totalDone = totalUserPush + totalUserSit + totalUserSquat + 0.0
    val whole = totalPush + totalSquat + totalSit + 0.0

    var percent = 0

    if (whole > 0){
        percent = ((totalDone/whole)*100).roundToInt()
    }else{
        percent = 0

    }

    return percent.toString() + "%"
}


fun getSitPractice(item: FitHistory): String{
    return item.sitCount.toString()

}


fun getSitFree(item: FitHistory): String{
    return item.sitCountPractice.toString()

}

fun getSquatPractice(item: FitHistory): String{
    return item.squatCount.toString()

}

fun getSquatFree(item: FitHistory): String{
    return item.squatCountPractice.toString()

}


fun getCalorie(item: FitHistory): String{
//    val formatter: NumberFormat = DecimalFormat("#0.00")

    val nf: NumberFormat = NumberFormat.getInstance(Locale.ENGLISH)
    val formatter = nf as DecimalFormat
    formatter.applyPattern("#0.00")


    return formatter.format(item.calorie).toString()

}



fun millisecondsToTime(milliseconds: Long): String? {
    var milliseconds1 = milliseconds
    var hr = 0
    var min = 0
    var sec = 0
    var day = 0
    while (milliseconds1 >= 1000) {
        milliseconds1 = (milliseconds1 - 1000)
        sec += 1
        if (sec >= 60) min += 1
        if (sec == 60) sec = 0
        if (min >= 60) hr += 1
        if (min == 60) min = 0
        if (hr >= 24) {
            hr = (hr - 24)
            day += 1
        }
    }
    val secondsStr = sec.toString()
    val secs: String
    secs = if (secondsStr.length >= 2) {
        secondsStr.substring(0, 2)
    } else {
        "0$secondsStr"
    }

    val minStr = min.toString()
    val mins: String
    mins = if (minStr.length >= 2) {
        minStr.substring(0, 2)
    } else {
        "0$minStr"
    }

    val hourStr = hr.toString()
    val hours: String
    hours = if (hourStr.length >= 2) {
        hourStr.substring(0, 2)
    } else {
        "0$hourStr"
    }
    return if (hours.equals("00")){
        "$mins:$secs"
    }else{
        "$hours:$mins:$secs"
    }



}

@SuppressLint("SimpleDateFormat")
 fun convertLongToDateStringCompareDay(systemTime: Long): Long {
    return SimpleDateFormat("yyyyMMdd")
        .format(systemTime).toLong()
}

fun formatFits(fits: List<FitHistory>, resources: Resources): Spanned {
    val sb = StringBuilder()
    sb.apply {
        append(resources.getString(R.string.title))

        fits.forEach {
            append("<br>")
            append(resources.getString(R.string.start_time))
            append("\t${convertLongToDateString(it.date)}<br>")
            append("Push count program : ${it.pushCount}<br>")
            append("Push count free : ${it.pushCountPractice}<br>")
            append("Sit count program : ${it.sitCount}<br>")
            append("sit count free : ${it.sitCountPractice}<br>")
            append("squat count program : ${it.squatCount}<br>")
            append("squat count free : ${it.squatCountPractice}<br>")
            append("Calorie : ${it.calorie}<br>")
            append("Duration : ${millisecondsToTime(it.timer)}")


        }
    }
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
        Html.fromHtml(sb.toString(), Html.FROM_HTML_MODE_LEGACY)
    } else {
        HtmlCompat.fromHtml(sb.toString(), HtmlCompat.FROM_HTML_MODE_LEGACY)
    }

}

 fun determineLevelPush(count: Int): Int {
    val level: Int
    when (count) {
        in 0..5 -> {
            level = 0
        }
        in 5..10 -> {
            level = 1
        }
        in 10..20 -> {
            level = 2
        }
        in 20..25 -> {
            level = 3
        }
        in 25..35 -> {
            level = 4
        }
        in 35..50 -> {
            level = 5
        }
        else -> {
            level = 6
        }
    }
    return level
}

 fun determineLevelSit(count: Int):Int{
    var level = 0
    when (count) {
        in 0..5 -> {
            level = 0
        }
        in 5..10 -> {
            level = 1
        }
        in 10..20 -> {
            level = 2
        }
        in 20..25 -> {
            level = 3
        }
        in 25..35 -> {
            level = 4
        }
        in 35..50 -> {
            level = 5
        }
        in 35..50 -> {
            level = 6
        }
        in 50..60 -> {
            level = 7
        }
        in 60..70 -> {
            level = 8
        }
        in 70..80 -> {
            level = 9
        }
        in 80..90 -> {
            level = 10
        }
        in 90..100 -> {
            level = 11
        }else ->{
        level = 12
    }
    }
    return level
}

 fun determineLevelSquat(count: Int): Int {
    var level = 0
    when (count) {
        in 0..5 -> {
            level = 0
        }
        in 5..10 -> {
            level = 1
        }
        in 10..20 -> {
            level = 2
        }
        in 20..25 -> {
            level = 3
        }
        in 25..35 -> {
            level = 4
        }
        in 35..50 -> {
            level = 5
        }
        in 35..50 -> {
            level = 6
        }
        in 50..60 -> {
            level = 7
        }
        else -> {
            level = 8
        }
    }
    return level
}




class TextItemViewHolder(val textView: TextView) : RecyclerView.ViewHolder(textView)