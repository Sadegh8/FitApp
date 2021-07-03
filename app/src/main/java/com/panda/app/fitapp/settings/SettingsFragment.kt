package com.panda.app.fitapp.settings


import android.app.AlertDialog
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.preference.ListPreference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.SwitchPreference
import androidx.work.Constraints
import androidx.work.OneTimeWorkRequest
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.panda.app.fitapp.R
import com.panda.app.fitapp.Util.MyWorker
import kotlinx.android.synthetic.main.dialog_layout_time_picker.view.*
import java.util.*
import java.util.concurrent.TimeUnit


/**
 * A simple [Fragment] subclass.
 */
const val TIME_DIALOG_ID = 1111
const val TAG = "periodic_work_fit"
open class SettingsFragment : PreferenceFragmentCompat() {
    var hour = 12
    var min = 0
    lateinit var reminder: SwitchPreference
    lateinit var language: ListPreference
    private lateinit var  sharedPref : SharedPreferences


    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.settings, rootKey)


        sharedPref = activity?.getPreferences(Context.MODE_PRIVATE)!!

         reminder = findPreference("reminder")!!

         language = findPreference("language1")!!

        language.summary = language.entry

        reminder.setOnPreferenceChangeListener { _, newValue ->
            if (newValue == true) {
                timePickerDialog()
            }

            true
        }


        val unit: ListPreference? = findPreference("unit")

        unit!!.setOnPreferenceChangeListener { _, newValue ->
            sharedPref.edit().putString("unitVal" , newValue.toString()).apply()

            true
        }

        hour = sharedPref.getInt("hour", 12)

        min = sharedPref.getInt("min", 0)

        if (reminder.isChecked) {
            reminder.summary = getString(R.string.every_day) + " " + hour + ":" + min
        }



    }


    private fun timePickerDialog() {
        val mDialogView = LayoutInflater.from(activity).inflate(R.layout.dialog_layout_time_picker, null)
        //AlertDialogBuilder
        val mBuilder = AlertDialog.Builder(activity)
            .setView(mDialogView)
            .setTitle("Set Time")
        //show dialog

        mDialogView.time_picker.setOnTimeChangedListener { _, hourOfDay, minute ->
            hour = hourOfDay

            min = minute

        }


        mBuilder.setPositiveButton(getString(R.string.done)) { dialog, _ ->

            sharedPref.edit().putInt("hour",hour).apply()

            sharedPref.edit().putInt("min",min).apply()

            val currentDate = Calendar.getInstance()

            val constraints = Constraints.Builder().build()

            val dueDate = Calendar.getInstance()

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


            WorkManager.getInstance(requireContext()).enqueue(dailyWorkRequest)

            Toast.makeText(activity,getString(R.string.every_day)+ " "+  hour+":"+min,Toast.LENGTH_LONG).show()
            reminder.summary = getString(R.string.every_day) + " "+  hour+":"+min

            dialog.dismiss()

        }

        mBuilder.setNegativeButton(getString(R.string.cancel)) { dialog, _ ->
            reminder.isChecked = false
            dialog.dismiss()
        }
        mBuilder.show()
    }

}