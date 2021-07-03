package com.panda.app.fitapp.history

import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.panda.app.fitapp.Util.*
import com.panda.app.fitapp.database.FitHistory



@BindingAdapter("fitDate")
fun TextView.setDate(item: FitHistory?){
    item?.let {
        text = convertLongToDateString(item.date)
    }
}

@BindingAdapter("fitProgress")
fun ProgressBar.fitProgress(item: FitHistory?){
    item?.let {
        max = getTotalForProgress(item)
        progress = getTotalUserForProgress(item)
        visibility = if (!getVisibilityProgress(item)) View.INVISIBLE else{
            View.VISIBLE
        }

    }
}

@BindingAdapter("fitImage")
fun ImageView.visibiltyImage(item: FitHistory?){
    item?.let {
        visibility = if (!getVisibilityProgress(item)) View.VISIBLE else {
            View.GONE
        }
    }
}

@BindingAdapter("fitDuration")
fun TextView.setDuration(item: FitHistory?){
    item?.let {
        text =  millisecondsToTime(item.timer)
    }

}

@BindingAdapter("fitPushCount")
fun TextView.setPush(item: FitHistory?){
    item?.let {
        text = getPushPractice(item)
    }

}

@BindingAdapter("fitPushCountFree")
fun TextView.setPushFree(item: FitHistory?){
    item?.let {
        text = getPushFree(item)
    }

}

@BindingAdapter("fitSitCount")
fun TextView.setSit(item: FitHistory?){
    item?.let {
        text = getSitPractice(item)
    }

}

@BindingAdapter("fitSitCountFree")
fun TextView.setSitFree(item: FitHistory?){
    item?.let {
        text = getSitFree(item)
    }

}

@BindingAdapter("fitSquatCount")
fun TextView.setSquat(item: FitHistory?){
    item?.let {
        text = getSquatPractice(item)
    }

}

@BindingAdapter("fitSquatCountFree")
fun TextView.setSquatFree(item: FitHistory?){
    item?.let {
        text = getSquatFree(item)
    }

}

@BindingAdapter("fitCalorie")
fun TextView.setCalorie(item: FitHistory?){
    item?.let {
        text = getCalorie(item)
    }

}
@BindingAdapter("fitPercents")
fun TextView.setPercents(item: FitHistory?){
    item?.let {
        if (getPercents(item) == "100%"){
            visibility = View.GONE
        }else{
            visibility = View.VISIBLE
            text = getPercents(item)

        }
    }

}