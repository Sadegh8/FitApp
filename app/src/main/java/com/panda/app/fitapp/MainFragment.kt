package com.panda.app.fitapp


import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.*
import androidx.fragment.app.Fragment
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.NavigationUI
import com.panda.app.earthquake.Utils.AppRater
import com.panda.app.fitapp.databinding.FragmentMainBinding

/**
 * A simple [Fragment] subclass.
 */
class MainFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?): View? {
        activity!!.window.clearFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN)

        val binding: FragmentMainBinding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_main, container, false)

        (activity as AppCompatActivity?)!!.supportActionBar!!.show()


        binding.lifecycleOwner = this


        val mainViewModel =
            ViewModelProviders.of(
                this).get(MainViewModel::class.java)

        binding.mainViewModel = mainViewModel

        setHasOptionsMenu(true)



        AppRater(activity!!).appLunched(activity!!)

//      Add an Observer to the state variable for Navigating when a Quality icon is tapped.
        mainViewModel.navigateToPushUps.observe(viewLifecycleOwner, Observer {
            if (it == true) { // Observed state is true.
                this.findNavController().navigate(MainFragmentDirections.actionMainFragmentToPushUpsFragment("nothing"))

                // Reset state to make sure we only navigate once, even if the device
                // has a configuration change.
                mainViewModel.doneNavigating()
            }
        })

        // For squats
        mainViewModel.navigateToSquats.observe(viewLifecycleOwner, Observer {
            if (it == true) { // Observed state is true.
                this.findNavController().navigate(MainFragmentDirections.actionMainFragmentToSquatsFragment("nothing"))

                // Reset state to make sure we only navigate once, even if the device
                // has a configuration change.
                mainViewModel.doneNavigatingSquats()
            }
        })

        //For sit ups
        mainViewModel.navigateToSit.observe(viewLifecycleOwner, Observer {
            if (it == true) { // Observed state is true.
                this.findNavController().navigate(MainFragmentDirections.actionMainFragmentToSitUpsFragment("nothing"))

                // Reset state to make sure we only navigate once, even if the device
                // has a configuration change.
                mainViewModel.doneNavigatingSit()
            }
        })


        //For workout
        mainViewModel.navigateToWorkout.observe(viewLifecycleOwner, Observer {
            if (it == true) { // Observed state is true.
                this.findNavController().navigate(MainFragmentDirections.actionMainFragmentToWorkoutFragment())

                // Reset state to make sure we only navigate once, even if the device
                // has a configuration change.
                mainViewModel.doneNavigatingWorkout()
            }
        })


        //For workout
        mainViewModel.navigateToLevelTest.observe(viewLifecycleOwner, Observer {
            if (it == true) { // Observed state is true.
                this.findNavController().navigate(MainFragmentDirections.actionMainFragmentToLevelTestFragment("nothing"))

                // Reset state to make sure we only navigate once, even if the device
                // has a configuration change.
                mainViewModel.doneNavigatingLevelTest()
            }
        })

        //For workout
        mainViewModel.navigateToHistory.observe(viewLifecycleOwner, Observer {
            if (it == true) { // Observed state is true.
                this.findNavController().navigate(MainFragmentDirections.actionMainFragmentToHistoryFragment())

                // Reset state to make sure we only navigate once, even if the device
                // has a configuration change.
                mainViewModel.doneNavigatingHistory()
            }
        })

        // Inflate the layout for this fragment
        return binding.root
    }


    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.info_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return NavigationUI.onNavDestinationSelected(item,view!!.findNavController())||
                super.onOptionsItemSelected(item)

    }


}
