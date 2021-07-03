package com.panda.app.fitapp.pushup

import android.app.AlertDialog
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.os.SystemClock
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.elconfidencial.bubbleshowcase.BubbleShowCaseBuilder
import com.elconfidencial.bubbleshowcase.BubbleShowCaseSequence
import com.google.android.gms.ads.AdRequest
import com.panda.app.fitapp.R
import com.panda.app.fitapp.database.FitDatabase
import com.panda.app.fitapp.databinding.FragmentPushUpsBinding
import kotlinx.android.synthetic.main.tutorial_dialog.view.*


/**
 * A simple [Fragment] subclass.
 */
class PushUpsFragment : Fragment() {
    private lateinit var binding: FragmentPushUpsBinding
    private var pushUpsViewModel: PushUpsViewModel? = null
    private var pushCount: String = "0"
    private lateinit var sharedPref: SharedPreferences
    private var pushCountRecord: Int = 0
    var play = true

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {


        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_push_ups, container, false
        )

        binding.lifecycleOwner = this

        val application = requireNotNull(this.activity).application

        val adRequest = AdRequest.Builder().build()
        binding.adView.loadAd(adRequest)


        val args = PushUpsFragmentArgs.fromBundle(requireArguments())

        when (args.numLevel) {
            "level" -> {
                binding.done.visibility = View.VISIBLE
                binding.chronometer.visibility = View.GONE
                binding.play.visibility = View.GONE
                binding.recordTxt.visibility = View.GONE
                binding.recordImg.visibility = View.GONE
                binding.playPause.visibility = View.GONE


            }
            "Easy" -> {
                binding.done.visibility = View.GONE
                binding.recordTxt.visibility = View.GONE
                binding.recordImg.visibility = View.GONE
                BubbleShowCaseSequence()
                    .addShowCase(getSimpleShowCaseBuilder()) //First BubbleShowCase to show
                    .addShowCase(getSimpleShowCaseBuilder2()) // This one will be showed when firstShowCase is dismissed
                    .show() //Display the ShowCaseSequence

            }
            else -> {
                BubbleShowCaseSequence()
                    .addShowCase(getSimpleShowCaseBuilder()) //First BubbleShowCase to show
                    .addShowCase(getSimpleShowCaseBuilder2()) // This one will be showed when firstShowCase is dismissed
                    .show() //Display the ShowCaseSequence
            }
        }

        sharedPref = activity?.getPreferences(Context.MODE_PRIVATE)!!

        pushCountRecord = sharedPref.getInt("record_push", 0)

        (getString(R.string.record) + pushCountRecord).also { txt ->
            binding.recordTxt.text = txt }

        val dataSource = FitDatabase.getInstance(application).fitDatabaseDao


        val viewModelFactory = PushUpsViewModelFactory(
            dataSource, application, args.numLevel
            , sharedPref.getInt("push", 0),
            sharedPref.getInt("sit", 0),
            sharedPref.getInt("squat", 0), sharedPref.getInt("gender", 0),
            sharedPref.getString("weight", "75")!!.toInt(),
            sharedPref.getString("height", "175")!!.toInt(),
            sharedPref.getString("age", "25")!!.toInt(),
            sharedPref.getInt("level", 0).toString().toInt()
        )


        pushUpsViewModel =
            ViewModelProvider(
                this, viewModelFactory
            ).get(PushUpsViewModel::class.java)



        binding.pushUpsViewModel = pushUpsViewModel


        pushUpsViewModel!!.incressPushUps.observe(viewLifecycleOwner, {
            binding.textPushUps.text = it.toString()
            pushCount = it.toString()
            if (it != null) {
                binding.progressBar.progress = it

                if (pushCount.toInt() > pushCountRecord) {
                    pushCountRecord = pushCount.toInt()
                    sharedPref.edit().putInt("record_push", pushCount.toInt()).apply()
                    (getString(R.string.record) + pushCountRecord).also { txt ->
                        binding.recordTxt.text = txt }

                }

            }

        })

        pushUpsViewModel!!.setMaxProgress.observe(viewLifecycleOwner, {
            if (args.numLevel != "nothing") {
                binding.progressBar.max = it!!

            }
        })


        pushUpsViewModel!!.sensorDataLiveData.observe(viewLifecycleOwner, {
            Log.d("RPMFragment", it)
        })

        val chronometer = binding.chronometer

        var timeWhenStopped: Long = 0


        pushUpsViewModel!!.playChronometer.observe(viewLifecycleOwner, {
            if (it!!) {
                chronometer.base = SystemClock.elapsedRealtime() + timeWhenStopped
                chronometer.start()
            } else {
                timeWhenStopped = chronometer.base - SystemClock.elapsedRealtime()
                chronometer.stop()

            }


        })


        pushUpsViewModel!!.navigateToSitUps.observe(viewLifecycleOwner, {
            if (it == true) { // Observed state is true.
                if (args.numLevel == "level") {
                    this.findNavController()
                        .navigate(PushUpsFragmentDirections.actionPushUpsFragmentToSitUpsFragment("push:$pushCount"))
                } else {
                    val elapsedMillis =
                        SystemClock.elapsedRealtime() - chronometer.base

                    pushUpsViewModel?.getChronometer(elapsedMillis.toString())


                    activity?.onBackPressed()
                }
                // Reset state to make sure we only navigate once, even if the device
                // has a configuration change.
                pushUpsViewModel!!.doneNavigatingSit()


            }
        })


        pushUpsViewModel!!.navigateToSitUpsFrom.observe(viewLifecycleOwner, {
            if (it == true) { // Observed state is true.
                this.findNavController()
                    .navigate(PushUpsFragmentDirections.actionPushUpsFragmentToSitUpsFragment("Easy"))

                val elapsedMillis =
                    SystemClock.elapsedRealtime() - chronometer.base

                pushUpsViewModel?.getChronometer(elapsedMillis.toString())


                // Reset state to make sure we only navigate once, even if the device
                // has a configuration change.
                pushUpsViewModel!!.doneNavigatingSitFrom()
            }
        })

        binding.playPause.setOnClickListener {
            if (play) {
                pushUpsViewModel!!.playChrono()
                binding.playPause.playAnimation()
                play = false
            } else {
                pushUpsViewModel!!.pauseChrono()
                binding.playPause.progress = 0F
                play = true
            }
        }

        binding.questionImg.setOnClickListener {
            tutorialDialog()
        }



        return binding.root
    }


    private fun getSimpleShowCaseBuilder(): BubbleShowCaseBuilder {
        return BubbleShowCaseBuilder(requireActivity())
            .title(getString(R.string.start))
            .description(getString(R.string.tap_here))
            .showOnce("SHOW_CASE_ID_START") //Id to show only once the BubbleShowCase
            .targetView(binding.playPause)

    }


    private fun getSimpleShowCaseBuilder2(): BubbleShowCaseBuilder {
        return BubbleShowCaseBuilder(requireActivity())
            .title(getString(R.string.info))
            .description(getString(R.string.see_how_do))
            .showOnce("SHOW_CASE_ID_INFO") //Id to show only once the BubbleShowCase
            .targetView(binding.questionImg)
    }

    override fun onResume() {
        super.onResume()
        pushUpsViewModel?.registerSensors()

    }

    override fun onPause() {
        super.onPause()
        pushUpsViewModel?.unregisterSensors()


    }

    override fun onDestroy() {
        binding.progressBar.progress = 0
        super.onDestroy()
    }

    private fun tutorialDialog() {
        val mDialogView = LayoutInflater.from(activity).inflate(R.layout.tutorial_dialog, null)
        //AlertDialogBuilder
        val mBuilder = AlertDialog.Builder(activity)
            .setView(mDialogView)
            .setTitle(getString(R.string.tutorial))

        //show dialog
        mDialogView.tutorial_txt.text = getString(R.string.push_ups_tutorial)


        mBuilder.setPositiveButton(getString(R.string.ok)) { dialog, _ ->

            dialog.dismiss()
        }

        mBuilder.show()

    }

}
