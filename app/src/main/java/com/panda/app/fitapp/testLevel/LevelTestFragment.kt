package com.panda.app.fitapp.testLevel


import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.panda.app.fitapp.R
import com.panda.app.fitapp.Util.determineLevelPush
import com.panda.app.fitapp.databinding.FragmentLevelTestBinding


/**
 * A simple [Fragment] subclass.
 */
class LevelTestFragment : Fragment() {

    private  var pushCount:Int = 0

    private  var sitCount:Int = 0

    private  var squatCount:Int = 0
    private lateinit var txtLevel:TextView
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding: FragmentLevelTestBinding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_level_test, container, false
        )

        // Inflate the layout for this fragment
        binding.lifecycleOwner = this

        val args = LevelTestFragmentArgs.fromBundle(requireArguments())

        if (args.level.contains("push", ignoreCase = true)) {
            txtLevel = binding.levelTxt

            getData(args)
           setView(binding)
        }
        val levelTestViewModel =
            ViewModelProvider(
                this
            ).get(LevelTestViewModel::class.java)

        binding.levelViewModel = levelTestViewModel


        //Add an Observer to the state variable for Navigating when a Quality icon is tapped.
        levelTestViewModel.navigateToPushUps.observe(viewLifecycleOwner,  {
            if (it == true) { // Observed state is true.
                this.findNavController().navigate(LevelTestFragmentDirections.actionLevelTestFragmentToPushUpsFragment("level"))

                // Reset state to make sure we only navigate once, even if the device
                // has a configuration change.
                levelTestViewModel.doneNavigating()
            }
        })

        return  binding.root

    }

    private fun setView(binding: FragmentLevelTestBinding){
        binding.txtDe.visibility = View.GONE
        binding.btStart.visibility = View.GONE
        binding.imgCheckList.visibility = View.GONE

        binding.levelTxt.visibility = View.VISIBLE
        binding.pushTxt.visibility = View.VISIBLE
        binding.pushVal.visibility = View.VISIBLE
        binding.sitTxt.visibility = View.VISIBLE
        binding.sitVal.visibility = View.VISIBLE
        binding.squatTxt.visibility = View.VISIBLE
        binding.squatVal.visibility = View.VISIBLE

        binding.pushVal.text = pushCount.toString()
        binding.sitVal.text = sitCount.toString()
        binding.squatVal.text = squatCount.toString()


    }

    private fun getData(args: LevelTestFragmentArgs){
        val string = args.level

        val push1: String = string.substringAfter("push:")

        val push: String = push1.substringBefore("sit:")


        val sit1: String = string.substringAfter("sit:")

        val sit: String = sit1.substringBefore("squat:")


        val squat: String = string.substringAfter("squat:")

        pushCount = getIntValueFromString(push)
        sitCount = getIntValueFromString(sit)
        squatCount = getIntValueFromString(squat)

        val sharedPref = activity?.getPreferences(Context.MODE_PRIVATE)

        sharedPref!!.edit().putInt("push",pushCount).apply()

        sharedPref.edit().putInt("sit",sitCount).apply()

        sharedPref.edit().putInt("squat",squatCount).apply()

        val levelPush = determineLevelPush(pushCount)
        val levelSit = determineLevelPush(sitCount)
        val levelSquat = determineLevelPush(squatCount)

        val totalLevel = ((levelPush + levelSit + levelSquat)/3)

        sharedPref.edit().putInt("level",totalLevel).apply()

        (getString(R.string.your_level) + totalLevel).also { txtLevel.text = it }

    }

    private fun getIntValueFromString(value : String): Int {
        var returnValue = ""
        value.forEach {
            val item = it.toString().toIntOrNull()
            if(item is Int){
                returnValue += item.toString()
            }

        }
        return returnValue.toInt()

    }
}
