package com.panda.app.fitapp.mainworkout


import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController

import com.panda.app.fitapp.R
import com.panda.app.fitapp.Util.*
import com.panda.app.fitapp.databinding.FragmentWorkoutBinding

/**
 * A simple [Fragment] subclass.
 */
class WorkoutFragment : Fragment() {
    private lateinit var binding: FragmentWorkoutBinding
    private var workoutViewModel: WorkoutViewModel? = null
    private lateinit var  sharedPref : SharedPreferences

    private var currentLevel = LevelData
    private var currentLevelSit = SitData
    private var currentLevelSquat = SQuatData



    private lateinit var levelData: LevelData.psuhLevel
    private lateinit var sitData: SitData.sitLevel
    private lateinit var squatData: SQuatData.squatLevel



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_workout, container, false
        )
        binding.lifecycleOwner = this


        workoutViewModel =
            ViewModelProvider(
                this
            ).get(WorkoutViewModel::class.java)


        binding.workoutViewModel = workoutViewModel

        sharedPref = activity?.getPreferences(Context.MODE_PRIVATE)!!


        val levelPush =  sharedPref.getInt("push",0)
        val levelSit =  sharedPref.getInt("sit",0)
        val levelSquat =   sharedPref.getInt("squat",0)


        val levelPush1 = determineLevelPush(levelPush)

        levelData = currentLevel.pushLevels[levelPush1]

        var x = 0
        while (x < levelData.rep.size) {
            if (x == 0) {
                binding.pushSet.append(levelData.rep[x].toString())
            }else{
                binding.pushSet.append(","+levelData.rep[x].toString())

            }
            x++
        }

        val levelSIt =  determineLevelSit(levelSit)

        sitData = currentLevelSit.sitLevels[levelSIt]


        var z = 0
        while (z < sitData.rep.size) {
            if (z == 0) {
                binding.sitSet.append(sitData.rep[z].toString())
            }else{
                binding.sitSet.append(","+sitData.rep[z].toString())

            }
            z++
        }


        val levelSquat1 =  determineLevelSquat(levelSquat)

        squatData = currentLevelSquat.squatLevels[levelSquat1]


        var t = 0
        while (t < squatData.rep.size) {
            if (t == 0) {
                binding.squatSet.append(squatData.rep[t].toString())
            }else{
                binding.squatSet.append(","+squatData.rep[t].toString())

            }
            t++
        }


        workoutViewModel!!.navigateToPushUp.observe(viewLifecycleOwner, {
            if (it == true) { // Observed state is true.
                this.findNavController().navigate(WorkoutFragmentDirections.actionWorkoutFragmentToPushUpsFragment("Easy"))
                // Reset state to make sure we only navigate once, even if the device
                // has a configuration change.
                workoutViewModel?.doneNavigatingPush()
            }
        })

        return binding.root

    }


}
