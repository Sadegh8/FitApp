package com.panda.app.fitapp.profile


import android.app.AlertDialog
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.*
import android.widget.NumberPicker
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.ui.NavigationUI
import com.panda.app.fitapp.R
import com.panda.app.fitapp.database.FitDatabase
import com.panda.app.fitapp.databinding.FragmentProfileBinding
import kotlinx.android.synthetic.main.bmi_layout.view.*
import kotlinx.android.synthetic.main.dialog_layout.view.*
import java.math.RoundingMode
import java.text.DecimalFormat
import java.text.NumberFormat
import java.util.*
import kotlin.math.pow
import kotlin.math.roundToInt


/**
 * A simple [Fragment] subclass.
 */
class ProfileFragment : Fragment() , SharedPreferences.OnSharedPreferenceChangeListener {
    private lateinit var binding: FragmentProfileBinding
    private lateinit var  sharedPref : SharedPreferences
    private var profileViewModel: ProfileViewModel? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?): View {
        binding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_profile, container, false)

        sharedPref = activity?.getPreferences(Context.MODE_PRIVATE)!!

        val application = requireNotNull(this.activity).application

        val dataSource = FitDatabase.getInstance(application).fitDatabaseDao

        val viewModelFactory = ProfileViewModelFactory(dataSource,application)

        setHasOptionsMenu(true)

        sharedPref.registerOnSharedPreferenceChangeListener(this)

        profileViewModel =
            ViewModelProvider(
                this, viewModelFactory
            ).get(ProfileViewModel::class.java)


        if (sharedPref.getInt("gender",0) == 0){
            binding.txtGender.text = getString(R.string.male)
        }else{
            binding.txtGender.text = getString(R.string.female)

        }

        val unit = sharedPref.getString("unitVal","1")

        if (unit.equals("1")){
          val data =  sharedPref.getString("weightLbs","0")!!.toDouble().roundToInt()

            (data.toString() + "kg").also { binding.txtWeight.text = it }
        }else{
            var lbs = sharedPref.getString("weightLbs","0")
            lbs = (lbs!!.toDouble() * 2.205).toString()

            val d =  lbs.toBigDecimal().setScale(1, RoundingMode.UP).toDouble()

            "$d lbs".also { binding.txtWeight.text = it }

        }


        if (unit.equals("1")) {
            val data =  sharedPref.getString("heightFoot","0")!!.toDouble().roundToInt()

            "$data cm".also { binding.txtHeight.text = it }
        }else{
            var foot = sharedPref.getString("heightFoot","0")
            foot = (foot!!.toDouble() / 30.48).toString()

            val f =  foot.toBigDecimal().setScale(1, RoundingMode.UP).toDouble()

            "$f ft".also { binding.txtHeight.text = it }

        }

        binding.txtAge.text =  sharedPref.getString("age","-")

        binding.level.text =  sharedPref.getInt("level",0).toString()


        profileViewModel!!.totalPush.observe(viewLifecycleOwner, {
            it?.let {
            binding.pushTotal.text = it.toString()
            }

        })

        profileViewModel!!.totalPushFree.observe(viewLifecycleOwner, {
            it?.let {
                binding.pushTotalFree.text = it.toString()
            }

        })


        profileViewModel!!.totalSit.observe(viewLifecycleOwner, {
            it?.let {
                binding.sitTotal.text = it.toString()
            }

        })

        profileViewModel!!.totalSitFree.observe(viewLifecycleOwner, {
            it?.let {
                binding.sitTotalFree.text = it.toString()
            }

        })


        profileViewModel!!.totalSquat.observe(viewLifecycleOwner,  {
            it?.let {
                binding.squatTotal.text = it.toString()
            }

        })

        profileViewModel!!.totalSquatFree.observe(viewLifecycleOwner,  {
            it?.let {
                binding.squatTotalFree.text = it.toString()
            }

        })


        binding.constraintGender.setOnClickListener {
            genderDialog()
        }

        binding.constraintWeight.setOnClickListener {
            weightDialog()
        }

        binding.constraintHeight.setOnClickListener {
            heightDialog()
        }

        binding.constraintAge.setOnClickListener {
            ageDialog()
        }

        bmi()

        binding.infoBmi.setOnClickListener {
            bmiInfoDialog()
        }

        return binding.root
    }

    private fun bmi() {
        val weight = sharedPref.getString("weight", "0")!!.toInt()

        var height = sharedPref.getString("height", "0")!!.toDouble()

        if (height.toInt() != 0){


        height /= 100

        val heightPow = (height.pow(2.0))

        val bmi = (weight / heightPow)

        val rounded = roundOffDecimal(bmi)


        binding.bmi.text = rounded.toString()
        }
    }


    private fun roundOffDecimal(number: Double): Double {
        val nf: NumberFormat = NumberFormat.getInstance(Locale.ENGLISH)
        val formatter = nf as DecimalFormat
        formatter.applyPattern("#0.00")

        formatter.roundingMode = RoundingMode.CEILING
        return formatter.format(number).toDouble()
    }

    private fun genderDialog() {
        val listItems =
            arrayOf(getString(R.string.male), getString(R.string.female))

        val builder =
            AlertDialog.Builder(activity)
        builder.setTitle(getString(R.string.gender))

        var checkedItem = 0 //this will checked the item when user open the dialog

        val beforeItem = sharedPref.getInt("gender",0)
        checkedItem = if (beforeItem == 0){
            0
        }else{
            1
        }
        builder.setSingleChoiceItems(
            listItems,
            checkedItem
        ) { _, which ->
            binding.txtGender.text = listItems[which]
            sharedPref.edit().putInt("gender", which).apply()
            profileViewModel!!.updateGender(which)
        }

        builder.setPositiveButton(getString(R.string.done)) { dialog, _ -> dialog.dismiss() }
        val dialog = builder.create()
        dialog.show()
    }

    private fun weightDialog() {
        val mDialogView = LayoutInflater.from(activity).inflate(R.layout.dialog_layout, null)
        //AlertDialogBuilder
        val mBuilder = AlertDialog.Builder(activity)
            .setView(mDialogView)
            .setTitle(getString(R.string.weight))
        //show dialog

       val unit = sharedPref.getString("unitVal","1")

        val np: NumberPicker = mDialogView.numberPicker

        val npCenter: NumberPicker = mDialogView.numberPickerCenter

        val np2: NumberPicker = mDialogView.numberPickerTow

       val unitText : TextView = mDialogView.txt_type

        val unitTextCenter : TextView = mDialogView.txt_type_center

        var unitTxt = "kg"

        if (unit.equals("1")){
            np2.visibility =View.GONE
            np.visibility = View.GONE
            unitText.visibility = View.GONE
            npCenter.maxValue = 250
            npCenter.minValue = 0
        }else {
            unitTextCenter.visibility = View.GONE
            unitText.visibility = View.VISIBLE

            np2.visibility =View.VISIBLE
            np.visibility = View.VISIBLE
            npCenter.visibility = View.GONE

            unitTxt = "lbs"
            np.maxValue = 250
            np.minValue = 0

            np2.maxValue = 9
            np2.minValue =0
        }

        mDialogView.txt_type.text = unitTxt


        mBuilder.setPositiveButton(getString(R.string.done)) { dialog, _ ->
            if (unit.equals("1")){
                sharedPref.edit().putString("weight", npCenter.value.toString()).apply()
                sharedPref.edit().putString("weightLbs", npCenter.value.toString()).apply()


            }else{
                val c: Double = (np.value.toString() + "." + np2.value).toDouble()
                sharedPref.edit().putString("weightLbs", (c/2.205).toString()).apply()


                sharedPref.edit().putString("weight", (c/2.205).roundToInt().toString()).apply()

            }

            if (unit.equals("1")){
                (sharedPref.getString("weightLbs","0") + unitTxt).also { binding.txtWeight.text = it }
            }else{
                var lbs = sharedPref.getString("weightLbs","0")
                lbs = (lbs!!.toDouble() * 2.205).toString()
                val d =  lbs!!.toBigDecimal().setScale(1, RoundingMode.UP).toDouble()

                "$d lbs".also { binding.txtWeight.text = it }

            }

            val weight = sharedPref.getString("weight","0")!!.toInt()

            profileViewModel!!.updateWeight(weight)

            dialog.dismiss()
            bmi()

        }

        mBuilder.setNegativeButton(getString(R.string.cancel)) { dialog, _ -> dialog.dismiss() }
        mBuilder.show()

        if (unit.equals("1")) {
            npCenter.value = (sharedPref.getString("weightLbs","20"))!!.toDouble().roundToInt()

        }else{
            var lbs = sharedPref.getString("weightLbs","0")
            lbs = (lbs!!.toDouble() * 2.205).toString()

           val d = lbs.toString()

            val result = d.substringBefore('.')

            val result2 = d.substringAfter('.')



            np.value = result.toInt()

            np2.value = result2.toInt()
        }


    }


    private fun heightDialog() {
        val mDialogView = LayoutInflater.from(activity).inflate(R.layout.dialog_layout, null)
        //AlertDialogBuilder
        val mBuilder = AlertDialog.Builder(activity)
            .setView(mDialogView)
            .setTitle(getString(R.string.height))
        //show dialog
        val unit = sharedPref.getString("unitVal","1")

        val np: NumberPicker = mDialogView.numberPicker

        val npCenter: NumberPicker = mDialogView.numberPickerCenter

        val np2: NumberPicker = mDialogView.numberPickerTow

        val unittx : TextView = mDialogView.txt_type

        val unittxCenter : TextView = mDialogView.txt_type_center


        mDialogView.txt_type.text = getString(R.string.cm)


        if (unit.equals("1")){
            np2.visibility =View.GONE
            np.visibility = View.GONE
            unittx.visibility = View.GONE
            npCenter.maxValue = 220
            npCenter.minValue = 0

            unittxCenter.text = getString(R.string.cm)
        }else {
            unittxCenter.visibility = View.GONE
            unittx.visibility = View.VISIBLE

            unittx.text = getString(R.string.feet)

            np2.visibility =View.VISIBLE
            np.visibility = View.VISIBLE
            npCenter.visibility = View.GONE

            np.maxValue = 7
            np.minValue = 0

            np2.maxValue = 11
            np2.minValue =0
        }

        mBuilder.setPositiveButton(getString(R.string.done)) { dialog, _ ->

            if (unit.equals("1")) {
                sharedPref.edit().putString("height", npCenter.value.toString()).apply()
                sharedPref.edit().putString("heightFoot", npCenter.value.toString()).apply()

            }else{
                val c: Double = (np.value.toString() + "." + np2.value).toDouble()

                sharedPref.edit().putString("heightFoot", (c*30.48).toString()).apply()

                sharedPref.edit().putString("height", (c*30.48).roundToInt().toString()).apply()
            }

            if (unit.equals("1")){
                (sharedPref.getString("heightFoot","0") + "cm").also { binding.txtHeight.text = it }
            }else{
                var foot = sharedPref.getString("heightFoot","0")
                foot = (foot!!.toDouble() / 30.48).toString()
                val f =  foot.toBigDecimal().setScale(1, RoundingMode.UP).toDouble()

                "$f ft".also { binding.txtHeight.text = it }

            }
            val foot = sharedPref.getString("height","0")!!.toInt()

            profileViewModel!!.updateHeight(foot)

            dialog.dismiss()

            bmi()

        }

        mBuilder.setNegativeButton(getString(R.string.cancel)) { dialog, _ -> dialog.dismiss() }
        mBuilder.show()

        if (unit.equals("1")) {
            npCenter.value = (sharedPref.getString("heightFoot","180"))!!.toDouble().roundToInt()

        }else{
            var lbs = sharedPref.getString("heightFoot","6.1")
            lbs = (lbs!!.toDouble() / 30.48).toString()

            val d = lbs.toString()

            val result = d.substringBefore('.')

            var result2 = d.substringAfter('.')

            result2 = result2[0].toString()



            np.value = result.toInt()

            np2.value = result2.toInt()
        }


    }

    private fun ageDialog() {
        val mDialogView = LayoutInflater.from(activity).inflate(R.layout.dialog_layout, null)
        //AlertDialogBuilder
        val mBuilder = AlertDialog.Builder(activity)
            .setView(mDialogView)
            .setTitle(getString(R.string.age))
        //show dialog

        val np: NumberPicker = mDialogView.numberPicker


        val npCenter: NumberPicker = mDialogView.numberPickerCenter

        val np2: NumberPicker = mDialogView.numberPickerTow

        val unittx : TextView = mDialogView.txt_type

        val unittxCenter : TextView = mDialogView.txt_type_center


        mDialogView.txt_type.text = getString(R.string.cm)

            np2.visibility =View.GONE
            np.visibility = View.GONE
            unittx.visibility = View.GONE
            npCenter.maxValue = 120
            npCenter.minValue = 0

            unittxCenter.visibility = View.GONE

        mDialogView.txt_type.visibility = View.GONE

        mBuilder.setPositiveButton(getString(R.string.done)) { dialog, _ ->
            sharedPref.edit().putString("age", npCenter.value.toString()).apply()

            binding.txtAge.text =  sharedPref.getString("age","0")
            profileViewModel!!.updateAge(npCenter.value)

            dialog.dismiss()
        }

        mBuilder.setNegativeButton(getString(R.string.cancel)) { dialog, _ -> dialog.dismiss() }

        mBuilder.show()

        npCenter.minValue = 5
        npCenter.maxValue = 130
        npCenter.value = sharedPref.getString("age","20")!!.toInt()

    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.profile_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
                return NavigationUI.onNavDestinationSelected(item,requireView().findNavController())||
                super.onOptionsItemSelected(item)

    }

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences?, key: String?) {

    }


    private fun bmiInfoDialog() {
        val mDialogView = LayoutInflater.from(activity).inflate(R.layout.bmi_layout, null)
        //AlertDialogBuilder
        val mBuilder = AlertDialog.Builder(activity)
            .setView(mDialogView)
            .setTitle(getString(R.string.bmi))
        //show dialog

        val weight = sharedPref.getString("weight", "0")!!.toInt()

        var height = sharedPref.getString("height", "0")!!.toDouble()

        height /= 100

        val heightPow = (height.pow(2.0))

        val bmi = (weight / heightPow)

        val rounded = roundOffDecimal(bmi)

        mDialogView.bmi_val.text = rounded.toString()

        if (rounded < 18.5){
            mDialogView.details.text = getString(R.string.underweight)
        }else if (rounded >= 18.5 && rounded < 25.0){
            mDialogView.details.text = getString(R.string.normal_bmi)
        }else if (rounded > 25 && rounded < 30){
            mDialogView.details.text = getString(R.string.overweight )

        }else if (rounded > 30){
            mDialogView.details.text = getString(R.string.obesity  )

        }

        mBuilder.setPositiveButton(getString(R.string.ok)) { dialog, _ ->

            dialog.dismiss()
        }


        mBuilder.show()


    }
}
