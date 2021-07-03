package com.panda.app.fitapp.splash


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.panda.app.fitapp.R
import com.panda.app.fitapp.databinding.FragmentSplashBindingImpl
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch



/**
 * A simple [Fragment] subclass.
 */
class SplashFragment : Fragment() {
    private val _exit = MutableLiveData<Boolean?>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?): View {
        // Inflate the layout for this fragment
        val binding: FragmentSplashBindingImpl = DataBindingUtil.inflate(
            inflater, R.layout.fragment_splash, container, false)
        binding.lifecycleOwner = this


        requireActivity().window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN)

        (activity as AppCompatActivity?)!!.supportActionBar!!.hide()


        _exit.value =false

        GlobalScope.launch(context = Dispatchers.Main) {
            delay(2000)
            _exit.value =true

        }

        _exit.observe(viewLifecycleOwner, Observer {
            if (it == true){
                findNavController().navigate(SplashFragmentDirections.actionSplashFragmentToMainFragment())

            }
        })


        return binding.root
    }



}
