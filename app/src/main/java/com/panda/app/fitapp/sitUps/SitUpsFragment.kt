package com.panda.app.fitapp.sitUps

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
import com.panda.app.fitapp.databinding.FragmentSitUpsBinding
import com.panda.app.fitapp.pushup.SitUpViewModelFactory
import kotlinx.android.synthetic.main.tutorial_dialog.view.*


/**
 * A simple [Fragment] subclass.
 */
class SitUpsFragment : Fragment() {
    private lateinit var binding: FragmentSitUpsBinding
    private var sitUpsViewModel: SitUpsViewModel? = null
    private var sitCount: String = "0"
    private var pushCount: String = "0"
    private lateinit var  sharedPref : SharedPreferences
    private var sitCountRecord :Int = 0
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?): View {

        binding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_sit_ups, container, false)

        binding.lifecycleOwner = this
        binding.progressBar.progress = 0

        val application = requireNotNull(this.activity).application


        val args = SitUpsFragmentArgs.fromBundle(requireArguments())



        val adRequest = AdRequest.Builder().build()
        binding.adView.loadAd(adRequest)


        pushCount = args.pushcount

        when {
            args.pushcount.contains("push", ignoreCase = true) -> {
                binding.done.visibility = View.VISIBLE
                binding.chronometer.visibility =View.GONE
                binding.play.visibility =View.GONE
                binding.recordTxt.visibility = View.GONE
                binding.playPause.visibility =View.GONE
                binding.recordImg.visibility = View.GONE

            }
            args.pushcount == "Easy" -> {
                binding.done.visibility = View.GONE
                binding.recordTxt.visibility = View.GONE
                binding.recordImg.visibility = View.GONE


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

        val dataSource = FitDatabase.getInstance(application).fitDatabaseDao


        val viewModelFactory = SitUpViewModelFactory(dataSource,application, args.pushcount
            , sharedPref.getInt("sit",0),sharedPref.getInt("gender",0),
            sharedPref.getString("weight","75")!!.toInt())

        sitCountRecord = sharedPref.getInt("record_sit",0)

        (getString(R.string.record)  + sitCountRecord.toString()).also { binding.recordTxt.text = it }


        sitUpsViewModel =
            ViewModelProvider(
                this,viewModelFactory
            ).get(SitUpsViewModel::class.java)

        binding.sitUpsViewModel = sitUpsViewModel





        sitUpsViewModel!!.increaseSitUps.observe(viewLifecycleOwner,  {
            binding.textSitUps.text = it.toString()
            sitCount = it.toString()
            if (it != null) {
                binding.progressBar.progress = it

                if (sitCount.toInt() > sitCountRecord) {
                    sitCountRecord = sitCount.toInt()
                    sharedPref.edit().putInt("record_sit", sitCount.toInt()).apply()
                    (getString(R.string.record)  + sitCountRecord.toString()).also { binding.recordTxt.text = it }

                }

            }

        })

        sitUpsViewModel!!.sensorDataLiveData.observe(viewLifecycleOwner,  {
            Log.d("RPMFragment", it)
        })
        var timeWhenStopped: Long = 0
        val chronometer = binding.chronometer
        sitUpsViewModel!!.playChronometer.observe(viewLifecycleOwner,  {
            if (it!!) {
                chronometer.base = SystemClock.elapsedRealtime() + timeWhenStopped
                chronometer.start()
            }else{
                timeWhenStopped = chronometer.base - SystemClock.elapsedRealtime()

                chronometer.stop()

            }
        })

        sitUpsViewModel!!.navigateToSquat.observe(viewLifecycleOwner,  {
            if (it == true) { // Observed state is true.
                if (args.pushcount.contains("push", ignoreCase = true)) {
                    this.findNavController()
                        .navigate(SitUpsFragmentDirections.actionSitUpsFragmentToSquatsFragment("$pushCount sit:$sitCount"))
                }else{
                    val elapsedMillis =
                        SystemClock.elapsedRealtime() - chronometer.base

                    sitUpsViewModel?.getChronometer(elapsedMillis.toString())


                    activity?.onBackPressed()
                }
                // Reset state to make sure we only navigate once, even if the device
                // has a configuration change.
                sitUpsViewModel!!.doneNavigatingSquat()
            }
        })

        sitUpsViewModel!!.setMaxProgress.observe(viewLifecycleOwner,  {
            if (args.pushcount != "nothing") {
                binding.progressBar.max = it!!

            }
        })



        sitUpsViewModel!!.navigateToSquatFrom.observe(viewLifecycleOwner,  {
            if (it == true) { // Observed state is true.
                this.findNavController()
                    .navigate(SitUpsFragmentDirections.actionSitUpsFragmentToSquatsFragment("Easy"))

                val elapsedMillis =
                    SystemClock.elapsedRealtime() - chronometer.base

                sitUpsViewModel?.getChronometer(elapsedMillis.toString())


                // Reset state to make sure we only navigate once, even if the device
                // has a configuration change.
                sitUpsViewModel!!.doneNavigatingSquatFrom()
            }
        })
        var play = true
        binding.playPause.setOnClickListener {
            if (play) {
                sitUpsViewModel!!.playChrono()
                play = false
                binding.playPause.playAnimation()
            }else{
                sitUpsViewModel!!.pauseChrono()
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
        sitUpsViewModel?.registerSensors()
    }

    override fun onPause() {
        super.onPause()
        sitUpsViewModel?.unregisterSensors()
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

        mDialogView.tutorial_img.setImageResource(R.drawable.ic_sit_ups_phone)

        mDialogView.tutorial_txt.text = getString(R.string.sit_ups_tutorial)


        mBuilder.setPositiveButton(getString(R.string.ok)) { dialog, _ ->

            dialog.dismiss()
        }


        mBuilder.show()


    }
}
