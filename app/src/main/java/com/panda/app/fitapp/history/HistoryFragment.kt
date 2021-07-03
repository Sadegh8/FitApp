package com.panda.app.fitapp.history


import android.app.AlertDialog
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar


import com.panda.app.fitapp.R
import com.panda.app.fitapp.database.FitDatabase
import com.panda.app.fitapp.databinding.FragmentHistoryBinding
import com.panda.app.fitapp.pushup.HistoryViewModelFactory
import kotlinx.android.synthetic.main.sort_layout.view.*


/**
 * A simple [Fragment] subclass.
 */
class HistoryFragment : Fragment() {
    private lateinit var binding: FragmentHistoryBinding
    private var historyViewModel: HistoryViewModel? = null
    private lateinit var adapter: FitAdapter
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_history, container, false
        )


        binding.lifecycleOwner = this
        setHasOptionsMenu(true)


        val application = requireNotNull(this.activity).application

        val dataSource = FitDatabase.getInstance(application).fitDatabaseDao

        val viewModelFactory = HistoryViewModelFactory(dataSource, application)


        historyViewModel =
            ViewModelProvider(
                this, viewModelFactory
            ).get(HistoryViewModel::class.java)

        binding.historyViewModel = historyViewModel


        adapter = FitAdapter(FitHistoryListener {
            historyViewModel!!.onFitClicked(it)
        }, FitHistoryLongListener {
            historyViewModel!!.onFitClickedLong(it)
        })

        binding.historyList.adapter = adapter

        historyViewModel!!.fits.observe(viewLifecycleOwner,  {
            it?.let {
                if (it.isNotEmpty()) {
                    adapter.submitList(it)
                    binding.emptyImg.visibility = View.GONE
                    binding.emptyTxt.visibility = View.GONE
                }else{
                    binding.emptyImg.visibility = View.VISIBLE
                    binding.emptyTxt.visibility = View.VISIBLE
                    binding.historyList.visibility =View.GONE
                }
            }

        })

        historyViewModel!!.showSnackBarEvent.observe(viewLifecycleOwner,  { it ->
            when {
                it -> {

                }
                else -> {
                    historyViewModel!!.fits.observe(viewLifecycleOwner,  {
                        it?.let {
                            adapter.submitList(it)
                        }

                    })
                }
            }

        })

        historyViewModel!!.navigateToDetail.observe(viewLifecycleOwner,  { fitId ->
            fitId?.let {
                this.findNavController().navigate(
                    HistoryFragmentDirections
                        .actionHistoryFragmentToDetailsFragment(fitId))
                historyViewModel!!.onDetailedNavigated()


            }
        })

        historyViewModel!!.deleteItem.observe(viewLifecycleOwner,  {
            it.let {
                dialogOneItemDelete(it)
            }
        })

        return binding.root
    }


    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.history_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.clear_all) {
            dialog()
        } else if (item.itemId == R.id.Settings) {
            sortDialog()
        }

        return super.onOptionsItemSelected(item)

    }

    private fun dialog() {
        val builder = AlertDialog.Builder(activity)
        builder.setTitle(getString(R.string.clear_all))
        builder.setMessage(getString(R.string.delete_all))
        //builder.setPositiveButton("OK", DialogInterface.OnClickListener(function = x))

        builder.setPositiveButton(android.R.string.yes) { dialog, which ->
            historyViewModel!!.onClear()


            val snackbar: Snackbar = Snackbar.make(
                requireActivity().findViewById(R.id.viewSnack),
                getString(R.string.cleared),
                Snackbar.LENGTH_SHORT)

            val snackBarView: View = snackbar.view
            snackBarView.setBackgroundColor(requireContext().resources.getColor(R.color.back_item))

            snackbar.show()

        }

        builder.setNegativeButton(android.R.string.no) { _, _ ->

        }

        builder.show()
    }


    private fun dialogOneItemDelete(id: Long) {
        val builder = AlertDialog.Builder(activity)
        builder.setTitle("Clear")
        builder.setMessage("Clear this item?")
        //builder.setPositiveButton("OK", DialogInterface.OnClickListener(function = x))

        builder.setPositiveButton(android.R.string.yes) { _, _ ->
            historyViewModel!!.onClearOneItem(id)


            val snackBar: Snackbar = Snackbar.make(
                requireActivity().findViewById(R.id.viewSnack),
                getString(R.string.cleared),
                Snackbar.LENGTH_SHORT)

            val snackBarView: View = snackBar.view
            snackBarView.setBackgroundColor(requireContext().resources.getColor(R.color.back_item))

            snackBar.show()
        }

        builder.setNegativeButton(android.R.string.no) { _, _ ->

        }

        builder.show()
    }


    private fun sortDialog() {
        val mDialogView = LayoutInflater.from(activity).inflate(R.layout.sort_layout, null)
        //AlertDialogBuilder
        val mBuilder = AlertDialog.Builder(activity)
            .setView(mDialogView)
//            .setTitle("Sort By")
        //show dialog
        val mAlertDialog = mBuilder.show()
        //login button click of custom layout
        mDialogView.oldest.setOnClickListener {
            historyViewModel!!.fitsSort.observe(viewLifecycleOwner,  {
                it?.let {
                    adapter.submitList(it)
                }

            })
            //dismiss dialog
            mAlertDialog.dismiss()

        }
        mDialogView.newest.setOnClickListener {
            historyViewModel!!.fits.observe(viewLifecycleOwner,  {
                it?.let {
                    adapter.submitList(it)
                }

            })
            //dismiss dialog
            mAlertDialog.dismiss()
        }
    }
}
