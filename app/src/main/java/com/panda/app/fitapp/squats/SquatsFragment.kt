package com.panda.app.fitapp.squats


import android.app.AlertDialog
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.os.SystemClock
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.elconfidencial.bubbleshowcase.BubbleShowCaseBuilder
import com.elconfidencial.bubbleshowcase.BubbleShowCaseSequence
import com.google.android.gms.ads.AdRequest
import com.panda.app.fitapp.R
import com.panda.app.fitapp.database.FitDatabase
import com.panda.app.fitapp.databinding.FragmentSquatsBinding
import com.panda.app.fitapp.pushup.SquatViewModelFactory
import kotlinx.android.synthetic.main.tutorial_dialog.view.*

/**
 * A simple [Fragment] subclass.
 */
class SquatsFragment : Fragment() {
    private var squatsViewModel: SquatsViewModel? = null
    private lateinit var binding: FragmentSquatsBinding
    private var sitCountAndSitCount: String = "0"
    private var squatCount: String = "0"
    private lateinit var  sharedPref : SharedPreferences
    private var squatCountRecord :Int = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_squats, container, false
        )
        binding.lifecycleOwner = this


        val args = SquatsFragmentArgs.fromBundle(requireArguments())

        val application = requireNotNull(this.activity).application



        val adRequest = AdRequest.Builder().build()
        binding.adView.loadAd(adRequest)


        when {
            args.sitcount.contains("push", ignoreCase = true) -> {
                binding.done.visibility = View.VISIBLE
                binding.chronometer.visibility =View.GONE
                binding.play.visibility =View.GONE
                binding.recordTxt.visibility = View.GONE
                binding.playPause.visibility =View.GONE
                sitCountAndSitCount = args.sitcount
                binding.recordImg.visibility = View.GONE


            }
            args.sitcount == "Easy" -> {
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


        val viewModelFactory = SquatViewModelFactory(dataSource,application, args.sitcount
            , sharedPref.getInt("squat",0),sharedPref.getInt("gender",0),
            sharedPref.getString("weight","75")!!.toInt())


        squatCountRecord = sharedPref.getInt("record_squat",0)

        (getString(R.string.record) + squatCountRecord).also { binding.recordTxt.text = it }


        squatsViewModel =
            ViewModelProvider(
                this,viewModelFactory
            ).get(SquatsViewModel::class.java)

        binding.squatViewModel = squatsViewModel



        squatsViewModel!!.increaseSquats.observe(viewLifecycleOwner,  {
            binding.textSquat.text = it.toString()
            squatCount = it.toString()
            if (it != null) {
                binding.progressBar.progress = it

                if (squatCount.toInt() > squatCountRecord) {
                    squatCountRecord = squatCount.toInt()
                    sharedPref.edit().putInt("record_squat", squatCount.toInt()).apply()
                    (getString(R.string.record) + squatCountRecord).also { binding.recordTxt.text = it }

                }
            }

        })

        squatsViewModel!!.sensorDataLiveData.observe(viewLifecycleOwner,  {
            Log.d("SquatFragment", it)
        })

        var timeWhenStopped: Long = 0

        val chronometer = binding.chronometer
        squatsViewModel!!.playChronometer.observe(viewLifecycleOwner,  {
            if (it!!) {
                chronometer.base = SystemClock.elapsedRealtime() + timeWhenStopped
                chronometer.start()
            }else{
                timeWhenStopped = chronometer.base - SystemClock.elapsedRealtime()

                chronometer.stop()

            }
        })


        squatsViewModel!!.navigateToLevel.observe(viewLifecycleOwner,  {
            if (it == true) { // Observed state is true.
                if (args.sitcount.contains("push", ignoreCase = true)) {

                    this.findNavController()
                        .navigate(
                            SquatsFragmentDirections.actionSquatsFragmentToLevelTestFragment
                                ("$sitCountAndSitCount squat:$squatCount")
                        )
                }else{
                    val elapsedMillis =
                        SystemClock.elapsedRealtime() - chronometer.base

                    squatsViewModel?.getChronometer(elapsedMillis.toString())


                    activity?.onBackPressed()
                }
                // Reset state to make sure we only navigate once, even if the device
                // has a configuration change.
               squatsViewModel!!.doneNavigatingLevel()
            }
        })

        squatsViewModel!!.setMaxProgress.observe(viewLifecycleOwner,  {
            if (args.sitcount != "nothing") {
                binding.progressBar.max = it!!

            }
        })


        squatsViewModel!!.navigateToCongratulation.observe(viewLifecycleOwner,  {
            if (it == true) { // Observed state is true.
                this.findNavController()
                    .navigate(SquatsFragmentDirections.actionSquatsFragmentToCongratulationFragment())

                val elapsedMillis =
                    SystemClock.elapsedRealtime() - chronometer.base

                squatsViewModel?.getChronometer(elapsedMillis.toString())


                // Reset state to make sure we only navigate once, even if the device
                // has a configuration change.
                squatsViewModel!!.doneNavigatingCongrat()
            }
        })

        var play = true
        binding.playPause.setOnClickListener {
            if (play) {
                squatsViewModel!!.playChrono()
                play = false
                binding.playPause.playAnimation()
            }else{
                squatsViewModel!!.pauseChrono()
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
        squatsViewModel?.registerSensors()
    }

    override fun onPause() {
        super.onPause()
        squatsViewModel?.unregisterSensors()
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

        mDialogView.tutorial_img.setImageResource(R.drawable.ic_squat_phone)

        mDialogView.tutorial_txt.text = getString(R.string.squat_tutorial)


        mBuilder.setPositiveButton(getString(R.string.ok)) { dialog, _ ->

            dialog.dismiss()
        }


        mBuilder.show()


    }
}
