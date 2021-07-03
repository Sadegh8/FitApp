package com.panda.app.fitapp.congratulation


import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.panda.app.fitapp.R
import com.panda.app.fitapp.Util.*
import com.panda.app.fitapp.databinding.FragmentCongratulationBinding
import com.panda.app.fitapp.mainworkout.WorkoutViewModel

/**
 * A simple [Fragment] subclass.
 */
class CongratulationFragment : Fragment() {
    private lateinit var binding: FragmentCongratulationBinding
    private var workoutViewModel: WorkoutViewModel? = null
    private lateinit var  sharedPref : SharedPreferences

    private var currentLevel = LevelData
    private var currentLevelSit = SitData
    private var currentLevelSquat = SQuatData

    private lateinit var levelData: LevelData.psuhLevel
    private lateinit var sitData: SitData.sitLevel
    private lateinit var squatData: SQuatData.squatLevel

    private var totalPushUps =0

    private var totalSitUps =0

    private var totalSquat =0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {


        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_congratulation, container, false)


        sharedPref = activity?.getPreferences(Context.MODE_PRIVATE)!!


        val levelPush =  sharedPref.getInt("push",0)
        val levelSit =  sharedPref.getInt("sit",0)
        val levelSquat =   sharedPref.getInt("squat",0)


        val levelPush1 = determineLevelPush(levelPush)

        levelData = currentLevel.pushLevels[levelPush1]

        var x = 0
        while (x < levelData.rep.size) {

            totalPushUps += levelData.rep[x]

            x++
        }

        val levelSIt =  determineLevelSit(levelSit)

        sitData = currentLevelSit.sitLevels[levelSIt]


        var z = 0
        while (z < sitData.rep.size) {

            totalSitUps += sitData.rep[z]


            z++
        }


        val levelSquat1 =  determineLevelSquat(levelSquat)

        squatData = currentLevelSquat.squatLevels[levelSquat1]


        var t = 0
        while (t < squatData.rep.size) {

            totalSquat += squatData.rep[t]
            t++
        }

        binding.pushPro.text = totalPushUps.toString()

        binding.sitPro.text = totalSitUps.toString()

        binding.squatPro.text = totalSquat.toString()

        return binding.root
    }


}
