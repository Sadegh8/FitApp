package com.panda.app.fitapp.details


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.transition.TransitionInflater
import com.panda.app.fitapp.R
import com.panda.app.fitapp.database.FitDatabase
import com.panda.app.fitapp.databinding.FragmentDetailsBinding

/**
 * A simple [Fragment] subclass.
 */
class DetailsFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?): View {

        val binding: FragmentDetailsBinding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_details, container, false)

        sharedElementEnterTransition = TransitionInflater.from(context).inflateTransition(android.R.transition.move)


        val application = requireNotNull(this.activity).application
        val arguments = DetailsFragmentArgs.fromBundle(requireArguments())

        // Create an instance of the ViewModel Factory.
        val dataSource = FitDatabase.getInstance(application).fitDatabaseDao
        val viewModelFactory = DetailsViewModelFactory(arguments.fitId, dataSource)

        // Get a reference to the ViewModel associated with this fragment.
        val detailViewModel =
            ViewModelProvider(
                this, viewModelFactory).get(DetailsViewModel::class.java)

        binding.detail = detailViewModel

        binding.lifecycleOwner = this




        return binding.root
    }


}
