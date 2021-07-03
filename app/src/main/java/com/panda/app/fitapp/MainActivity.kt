package com.panda.app.fitapp

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.IntentSender
import android.content.SharedPreferences
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.LiveData
import androidx.navigation.NavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.preference.PreferenceManager
import com.google.android.gms.ads.MobileAds
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.snackbar.Snackbar
import com.google.android.play.core.appupdate.AppUpdateManager
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.install.InstallState
import com.google.android.play.core.install.InstallStateUpdatedListener
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.InstallStatus
import com.google.android.play.core.install.model.UpdateAvailability
import com.panda.app.fitapp.Util.changeLang
import com.panda.app.fitapp.Util.setupWithNavController
import com.panda.app.fitapp.databinding.ActivityMainBinding


private const val SELECTED_LANGUAGE = "language"
class MainActivity : AppCompatActivity(), SharedPreferences.OnSharedPreferenceChangeListener {
    private lateinit var bottomNavigation: BottomNavigationView

    private lateinit var sharedPreferences: SharedPreferences

    private var currentNavController: LiveData<NavController>? = null

    private val appUpdateManager: AppUpdateManager by lazy { AppUpdateManagerFactory.create(this) }
    private val appUpdatedListener: InstallStateUpdatedListener by lazy {
        object : InstallStateUpdatedListener {
            override fun onStateUpdate(installState: InstallState) {
                when {
                    installState.installStatus() == InstallStatus.DOWNLOADED -> popupSnackBarForCompleteUpdate()
                    installState.installStatus() == InstallStatus.INSTALLED -> appUpdateManager.unregisterListener(this)

                }
            }
        }
    }


    override fun attachBaseContext(newBase: Context?) {
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences( newBase)
        val langCode = sharedPreferences.getString(SELECTED_LANGUAGE, "en")!!
        val context: Context = changeLang(newBase!!, langCode)!!
        super.attachBaseContext(context)
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)
        val theme = sharedPreferences.getBoolean("darkThemeVal", false)
        if (!theme) {
            AppCompatDelegate.setDefaultNightMode(
                AppCompatDelegate.MODE_NIGHT_NO
            )
        } else {
            AppCompatDelegate.setDefaultNightMode(
                AppCompatDelegate.MODE_NIGHT_YES
            )
        }
//        setLanguageOne(sharedPreferences)

        val binding =
            DataBindingUtil.setContentView<ActivityMainBinding>(this, R.layout.activity_main)
        bottomNavigation = binding.bottomNavigationView

        MobileAds.initialize(this) {}
        checkForAppUpdate()

        sharedPreferences.registerOnSharedPreferenceChangeListener(this)



        supportActionBar!!.elevation = 0F

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            window.statusBarColor = Color.BLACK
        }
//        val appBarConfiguration = AppBarConfiguration
//            .Builder(
//                R.id.mainFragment,
//                R.id.historyFragment,
//                R.id.profileFragment,
//                R.id.splashFragment
//            )
//            .build()
//        val navController = this.findNavController(R.id.nav_host_fragment)
////        NavigationUI.setupActionBarWithNavController(this, navController)
//        bottomNavigation.setupWithNavController(navController)
//        setupActionBarWithNavController(navController, appBarConfiguration)
//
//        navController.addOnDestinationChangedListener { _, destination, _ ->
//            when (destination.id) {
//                R.id.mainFragment -> bottomNavigation.visibility = View.VISIBLE
//                R.id.historyFragment -> bottomNavigation.visibility = View.VISIBLE
//                R.id.profileFragment -> bottomNavigation.visibility = View.VISIBLE
//
//                else -> bottomNavigation.visibility = View.GONE
//            }
//        }

        if (savedInstanceState == null) {
            setupBottomNavigationBar()
        }
    }

    /**
     * Called on first creation and when restoring state.
     */
    private fun setupBottomNavigationBar() {

        val navGraphIds =
            listOf(R.navigation.home, R.navigation.history, R.navigation.profile)

        // Setup the bottom navigation view with a list of navigation graphs
        val controller = bottomNavigation.setupWithNavController(
            navGraphIds = navGraphIds,
            fragmentManager = supportFragmentManager,
            containerId = R.id.nav_host_fragment,
            intent = intent
        )


        val appBarConfiguration = AppBarConfiguration
            .Builder(
                R.id.mainFragment2,
                R.id.historyFragment2,
                R.id.profileFragment2
            )
            .build()

        // Whenever the selected controller changes, setup the action bar.
        controller.observe(this, androidx.lifecycle.Observer { navController ->
            setupActionBarWithNavController(navController, appBarConfiguration)
            navController.addOnDestinationChangedListener { _, destination, _ ->
                when (destination.id) {
                    R.id.mainFragment2 -> bottomNavigation.visibility = View.VISIBLE
                    R.id.historyFragment2 -> bottomNavigation.visibility = View.VISIBLE
                    R.id.profileFragment2 -> bottomNavigation.visibility = View.VISIBLE

                    else -> bottomNavigation.visibility = View.GONE
                }
            }

        })
        currentNavController = controller

    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        setupBottomNavigationBar()

    }

    override fun onSupportNavigateUp(): Boolean {
        return currentNavController?.value?.navigateUp() ?: false

    }

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences?, key: String?) {
        if (key.equals("language1")) {
            val language = sharedPreferences!!.getString("language1", "1")
            when {
                language.equals("1") -> {
                    sharedPreferences.edit().putString(SELECTED_LANGUAGE, "en").apply()
                }
                language.equals("2") -> {
                    sharedPreferences.edit().putString(SELECTED_LANGUAGE, "sq").apply()
                }
                language.equals("3") -> {
                    sharedPreferences.edit().putString(SELECTED_LANGUAGE, "ar").apply()
                }
                language.equals("4") -> {
                    sharedPreferences.edit().putString(SELECTED_LANGUAGE, "fil").apply()
                }
                language.equals("5") -> {
                    sharedPreferences.edit().putString(SELECTED_LANGUAGE, "fr").apply()
                }
                language.equals("6") -> {
                    sharedPreferences.edit().putString(SELECTED_LANGUAGE, "de").apply()
                }
                language.equals("7") -> {
                    sharedPreferences.edit().putString(SELECTED_LANGUAGE, "el").apply()
                }
                language.equals("8") -> {
                    sharedPreferences.edit().putString(SELECTED_LANGUAGE, "hi").apply()
                }
                language.equals("9") -> {
                    sharedPreferences.edit().putString(SELECTED_LANGUAGE, "in").apply()
                }
                language.equals("10") -> {
                    sharedPreferences.edit().putString(SELECTED_LANGUAGE, "it").apply()
                }
                language.equals("11") -> {
                    sharedPreferences.edit().putString(SELECTED_LANGUAGE, "ja").apply()
                }
                language.equals("12") -> {
                    sharedPreferences.edit().putString(SELECTED_LANGUAGE, "ko").apply()
                }
                language.equals("13") -> {
                    sharedPreferences.edit().putString(SELECTED_LANGUAGE, "fa").apply()
                }
                language.equals("14") -> {
                    sharedPreferences.edit().putString(SELECTED_LANGUAGE, "pt").apply()
                }
                language.equals("15") -> {
                    sharedPreferences.edit().putString(SELECTED_LANGUAGE, "ru").apply()
                }
                language.equals("16") -> {
                    sharedPreferences.edit().putString(SELECTED_LANGUAGE, "es").apply()
                }
                language.equals("17") -> {
                    sharedPreferences.edit().putString(SELECTED_LANGUAGE, "tr").apply()
                }
            }

            recreate()
        }
        if (key.equals("darkThemeVal")) {
            val theme = sharedPreferences!!.getBoolean("darkThemeVal", false)

            if (!theme) {
                AppCompatDelegate.setDefaultNightMode(
                    AppCompatDelegate.MODE_NIGHT_NO
                )
            } else {
                AppCompatDelegate.setDefaultNightMode(
                    AppCompatDelegate.MODE_NIGHT_YES
                )

            }

        }
    }





    override fun onDestroy() {
        super.onDestroy()
//        sharedPreferences.unregisterOnSharedPreferenceChangeListener(this)
        PreferenceManager.getDefaultSharedPreferences(this)
            .unregisterOnSharedPreferenceChangeListener(this)
    }
    private fun checkForAppUpdate() {
        // Returns an intent object that you use to check for an update.
        val appUpdateInfoTask = appUpdateManager.appUpdateInfo

        // Checks that the platform will allow the specified type of update.
        appUpdateInfoTask.addOnSuccessListener { appUpdateInfo ->
            if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE) {
                // Request the update.
                try {
                    val installType = when {
                        appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.FLEXIBLE) -> AppUpdateType.FLEXIBLE
                        appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.IMMEDIATE) -> AppUpdateType.IMMEDIATE
                        else -> null
                    }
                    if (installType == AppUpdateType.FLEXIBLE) appUpdateManager.registerListener(appUpdatedListener)

                    appUpdateManager.startUpdateFlowForResult(
                        appUpdateInfo,
                         installType!!,
                        this,
                        APP_UPDATE_REQUEST_CODE)
                } catch (e: IntentSender.SendIntentException) {
                    e.printStackTrace()
                }
            }
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == APP_UPDATE_REQUEST_CODE) {
            if (resultCode != Activity.RESULT_OK) {
                Toast.makeText(this,
                    "App Update failed, please try again on the next app launch.",
                    Toast.LENGTH_SHORT)
                    .show()
            }
        }
    }

    private fun popupSnackBarForCompleteUpdate() {
        val snackBar = Snackbar.make(
            findViewById(R.id.viewSnack),
            "An update has just been downloaded.",
            Snackbar.LENGTH_INDEFINITE)
        snackBar.setAction("RESTART") { appUpdateManager.completeUpdate() }
        snackBar.setActionTextColor(ContextCompat.getColor(this, R.color.colorAccent))
        snackBar.show()
    }

    override fun onResume() {
        super.onResume()
        appUpdateManager
            .appUpdateInfo
            .addOnSuccessListener { appUpdateInfo ->

                // If the update is downloaded but not installed,
                // notify the user to complete the update.
                if (appUpdateInfo.installStatus() == InstallStatus.DOWNLOADED) {
                    popupSnackBarForCompleteUpdate()
                }

                //Check if Immediate update is required
                try {
                    if (appUpdateInfo.updateAvailability() == UpdateAvailability.DEVELOPER_TRIGGERED_UPDATE_IN_PROGRESS) {
                        // If an in-app update is already running, resume the update.
                        appUpdateManager.startUpdateFlowForResult(
                            appUpdateInfo,
                            AppUpdateType.IMMEDIATE,
                            this,
                            APP_UPDATE_REQUEST_CODE)
                    }
                } catch (e: IntentSender.SendIntentException) {
                    e.printStackTrace()
                }
            }
    }
    companion object {
        private const val APP_UPDATE_REQUEST_CODE = 1991
    }

}
