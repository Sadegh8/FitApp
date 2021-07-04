package com.panda.app.fitapp


import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.NavigationUI
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.panda.app.earthquake.Utils.AppRater
import com.panda.app.fitapp.databinding.FragmentMainBinding

/**
 * A simple [Fragment] subclass.
 */
private const val TAG = "MainFragment"

class MainFragment : Fragment() {
    private var mInterstitialAd: InterstitialAd? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        requireActivity().window.clearFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )

        val binding: FragmentMainBinding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_main, container, false
        )

        (activity as AppCompatActivity?)!!.supportActionBar!!.show()
        binding.lifecycleOwner = this

        val mainViewModel =
            ViewModelProvider(
                this
            ).get(MainViewModel::class.java)

        binding.mainViewModel = mainViewModel

        setHasOptionsMenu(true)

        AppRater(requireActivity()).appLunched(requireActivity())

        interstitialAd()

        //Add an Observer to the state variable for Navigating when a Quality icon is tapped.
        mainViewModel.navigateToPushUps.observe(viewLifecycleOwner, {
            if (it == true) { // Observed state is true.
                this.findNavController()
                    .navigate(MainFragmentDirections.actionMainFragmentToPushUpsFragment("nothing"))

                // Reset state to make sure we only navigate once, even if the device
                // has a configuration change.
                mainViewModel.doneNavigating()
                mInterstitialAd?.show(requireActivity())
            }
        })

        // For squats
        mainViewModel.navigateToSquats.observe(viewLifecycleOwner, {
            if (it == true) { // Observed state is true.
                this.findNavController()
                    .navigate(MainFragmentDirections.actionMainFragmentToSquatsFragment("nothing"))

                // Reset state to make sure we only navigate once, even if the device
                // has a configuration change.
                mainViewModel.doneNavigatingSquats()
                mInterstitialAd?.show(requireActivity())
            }
        })

        //For sit ups
        mainViewModel.navigateToSit.observe(viewLifecycleOwner, {
            if (it == true) { // Observed state is true.
                this.findNavController()
                    .navigate(MainFragmentDirections.actionMainFragmentToSitUpsFragment("nothing"))

                // Reset state to make sure we only navigate once, even if the device
                // has a configuration change.
                mainViewModel.doneNavigatingSit()
                mInterstitialAd?.show(requireActivity())
            }
        })


        //For workout
        mainViewModel.navigateToWorkout.observe(viewLifecycleOwner, {
            if (it == true) { // Observed state is true.
                this.findNavController()
                    .navigate(MainFragmentDirections.actionMainFragmentToWorkoutFragment())

                // Reset state to make sure we only navigate once, even if the device
                // has a configuration change.
                mainViewModel.doneNavigatingWorkout()
            }
        })


        //For workout
        mainViewModel.navigateToLevelTest.observe(viewLifecycleOwner, {
            if (it == true) { // Observed state is true.
                this.findNavController()
                    .navigate(MainFragmentDirections.actionMainFragmentToLevelTestFragment("nothing"))

                // Reset state to make sure we only navigate once, even if the device
                // has a configuration change.
                mainViewModel.doneNavigatingLevelTest()
            }
        })

        //For workout
        mainViewModel.navigateToHistory.observe(viewLifecycleOwner, {
            if (it == true) { // Observed state is true.
                this.findNavController()
                    .navigate(MainFragmentDirections.actionMainFragmentToHistoryFragment())

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
        return NavigationUI.onNavDestinationSelected(item, requireView().findNavController()) ||
                super.onOptionsItemSelected(item)

    }

    private fun interstitialAd() {
        val adRequest = AdRequest.Builder().build()

        InterstitialAd.load(requireActivity(), "ca-app-pub-2820216276511886/7353321811", adRequest,
            object : InterstitialAdLoadCallback() {
                override fun onAdFailedToLoad(adError: LoadAdError) {
                    Log.d(TAG, adError.message)
                    mInterstitialAd = null
                }

                override fun onAdLoaded(interstitialAd: InterstitialAd) {
                    Log.d(TAG, "Ad was loaded.")
                    mInterstitialAd = interstitialAd
                }
            })

        mInterstitialAd?.fullScreenContentCallback = object : FullScreenContentCallback() {
            override fun onAdDismissedFullScreenContent() {
                Log.d(TAG, "Ad was dismissed.")
            }

            override fun onAdFailedToShowFullScreenContent(adError: AdError?) {
                Log.d(TAG, "Ad failed to show.")
            }

            override fun onAdShowedFullScreenContent() {
                Log.d(TAG, "Ad showed fullscreen content.")
                mInterstitialAd = null
            }
        }
    }

}
