package com.panda.app.earthquake.Utils

import android.app.Activity
import android.app.Dialog
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.view.Window
import android.widget.Button
import com.panda.app.fitapp.R

private const val DAYS_UNTIL_PROMOT = 0
private const val LUNCH_UNTIL_PROMOT = 2
class AppRater (val activity: Activity) {
    private var APP_TITLE: String? = null

    fun appLunched(context: Context) {
        APP_TITLE = context.getString(R.string.app_name)
        val prefs = context.getSharedPreferences("rate_app", 0)
        if (prefs.getBoolean("dontshowagain", false)) {
            return
        }
        val editor = prefs.edit()
        val launchCount = prefs.getLong("launch_count", 0) + 1
        editor.putLong("launch_count", launchCount)
        var dateFirstLaunch = prefs.getLong("date_first_launch", 0)
        if (dateFirstLaunch == 0L) {
            dateFirstLaunch = System.currentTimeMillis()
            editor.putLong("date_first_launch", dateFirstLaunch)
        }
        if (launchCount >= LUNCH_UNTIL_PROMOT) {
            if (System.currentTimeMillis() >= dateFirstLaunch + DAYS_UNTIL_PROMOT * 24 * 60 * 60 * 1000) {
//                showRAteDialog(context, editor)
                showDialog(context,editor)
            }
        }
        editor.apply()
    }

    fun showRAteDialog(context: Context, editor: SharedPreferences.Editor?) {
        var dialog = Dialog(context)
        val builder = androidx.appcompat.app.AlertDialog.Builder(context)
        val message = context.getString(R.string.message)
        val finalDialog = dialog
        val finalDialog1 = dialog
        val finalDialog2 = dialog
        builder.setMessage(message)
            .setTitle(APP_TITLE)
            .setIcon(context.applicationInfo.icon)
            .setPositiveButton(
                context.getString(R.string.rate_now)
            ) { dialogInterface, i ->
                editor!!.putBoolean("dontshowagain", true)
                editor.commit()
                val uri =
                    Uri.parse("market://details?id=" + context.packageName)
                val goToMarket = Intent(Intent.ACTION_VIEW, uri)
                // To count with Play market backstack, After pressing back button,
                // to taken back to our application, we need to add following flags to intent.
                goToMarket.addFlags(
                    Intent.FLAG_ACTIVITY_NO_HISTORY or
                            Intent.FLAG_ACTIVITY_NEW_DOCUMENT or
                            Intent.FLAG_ACTIVITY_MULTIPLE_TASK
                )
                try {
                    context.startActivity(goToMarket)
                } catch (e: ActivityNotFoundException) {
                    context.startActivity(
                        Intent(
                            Intent.ACTION_VIEW,
                            Uri.parse("http://play.google.com/store/apps/details?id=" + context.packageName)
                        )
                    )
                }
                finalDialog.dismiss()
            }
            .setNeutralButton(
                context.getString(R.string.exit)
            ) { _, _ ->
                //                        finalDialog1.dismiss();
                if (activity != null) {
                    activity.finishAffinity()
                    System.exit(0)
                }
            }
            .setNegativeButton(
                context.getString(R.string.no_thanks)
            ) { _, _ ->
                if (editor != null) {
                    editor.putBoolean("dontshowagain", true)
                    editor.commit()
                }
                finalDialog2.dismiss()
            }
        dialog = builder.create()
        dialog.show()
    }


    private fun rateDialog(context: Context){

    }

    private fun showDialog(context: Context, editor: SharedPreferences.Editor?) {
        val dialog = Dialog(activity!!)
        val builder = androidx.appcompat.app.AlertDialog.Builder(context)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(true)
        dialog.setContentView(R.layout.rate_dialog)

        val yesBtn = dialog.findViewById(R.id.set) as Button
        yesBtn.setOnClickListener {
            editor!!.putBoolean("dontshowagain", true)
            editor.commit()
            val uri =
                Uri.parse("market://details?id=" + context.packageName)
            val goToMarket = Intent(Intent.ACTION_VIEW, uri)
            // To count with Play market backstack, After pressing back button,
            // to taken back to our application, we need to add following flags to intent.
            goToMarket.addFlags(
                Intent.FLAG_ACTIVITY_NO_HISTORY or
                        Intent.FLAG_ACTIVITY_NEW_DOCUMENT or
                        Intent.FLAG_ACTIVITY_MULTIPLE_TASK
            )
            try {
                context.startActivity(goToMarket)
            } catch (e: ActivityNotFoundException) {
                context.startActivity(
                    Intent(
                        Intent.ACTION_VIEW,
                        Uri.parse("http://play.google.com/store/apps/details?id=" + context.packageName)
                    )
                )
            }
            dialog.dismiss()
        }


        val noBtn = dialog.findViewById(R.id.no) as Button
        noBtn.setOnClickListener {
            if (editor != null) {
                editor.putBoolean("dontshowagain", true)
                editor.commit()
            }
            dialog.dismiss()
        }


        val exitBtn = dialog.findViewById(R.id.cancel) as Button
        exitBtn.setOnClickListener {
            //                        finalDialog1.dismiss();
            if (activity != null) {
                activity.finishAffinity()
                System.exit(0)
            }
            dialog.dismiss()
        }
        dialog.show()


    }
}