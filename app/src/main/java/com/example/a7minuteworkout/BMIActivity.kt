package com.example.a7minuteworkout

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.example.a7minuteworkout.databinding.ActivityBmiBinding
import java.math.BigDecimal
import java.math.RoundingMode


class BMIActivity : AppCompatActivity() {

    companion object {
        private const val METRIC_UNITS_VIEW = "METRIC_UNIT_VIEW" // Metric Unit View
        private const val US_UNITS_VIEW = "US_UNITS_VIEW" // US Unit View
    }
    private var currentVisibleView: String =
        METRIC_UNITS_VIEW // holds value of current view

    private var binding: ActivityBmiBinding? = null



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBmiBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        setSupportActionBar(binding?.toolbarBmiActivity)
        if(supportActionBar != null){
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
            supportActionBar?.title = "CALCULATE BMI"
        }
        binding?.toolbarBmiActivity?.setNavigationOnClickListener {
            onBackPressed()
        }

        makeVisibleMetricUnitsView()

        binding?.rgUnits?.setOnCheckedChangeListener {_, checkedId: Int ->

            if(checkedId == R.id.rbMetricUnits){
                makeVisibleMetricUnitsView()
            }else{
                makeVisibleMetricUSUnitsView()}

        }

        binding?.btnCalculateUnits?.setOnClickListener {
            if (validateMetricUnits()) {
                if (currentVisibleView == METRIC_UNITS_VIEW){
                    val heightValue: Float =
                        binding?.etMetricUnitHeight?.text.toString().toFloat() / 100
                    val weightValue: Float =
                        binding?.etMetricUnitWeight?.text.toString().toFloat()
                    val bmi = weightValue / (heightValue * heightValue)

                    // TODO display BMI Results
                    displayBMIResults(bmi)
                } else {
                    val feetValue: Float =
                        binding?.etMetricUSUnitHeightFeet?.text.toString().toFloat()
                    val inchValue: Float =
                        binding?.etMetricUSUnitHeightInch?.text.toString().toFloat()
                    val weightValue: Float = binding?.etMetricUnitWeight?.text.toString().toFloat()
                    val totalInch: Float = feetValue * 12 + inchValue
                    val bmi = weightValue / (totalInch * totalInch) * 703

                    // TODO display BMI Results
                    displayBMIResults(bmi)
                }
            }else {
                Toast.makeText(
                    this@BMIActivity,
                    "Please enter valid values",
                    Toast.LENGTH_SHORT
                ).show()

            }
        }
    }

    private fun makeVisibleMetricUnitsView(){
        currentVisibleView = METRIC_UNITS_VIEW
        binding?.tilMetricUnitWeight?.visibility = View.VISIBLE
        binding?.tilMetricUnitHeight?.visibility = View.VISIBLE
        binding?.tilMetricUnitWeight?.hint = "WEIGHT (in kg)"
        binding?.tilMetricUSUnitHeightFeet?.visibility = View.GONE
        binding?.tilMetricUSUnitHeightInch?.visibility = View.GONE

        binding?.etMetricUnitWeight?.text!!.clear()
        binding?.etMetricUnitHeight?.text!!.clear()
        binding?.llDisplayBMIResult?.visibility = View.INVISIBLE

    }

    private fun makeVisibleMetricUSUnitsView(){
        currentVisibleView = US_UNITS_VIEW
        binding?.tilMetricUnitWeight?.visibility = View.VISIBLE
        binding?.tilMetricUnitHeight?.visibility = View.INVISIBLE
        binding?.tilMetricUnitWeight?.hint = "WEIGHT (in lb)"
        binding?.tilMetricUSUnitHeightFeet?.visibility = View.VISIBLE
        binding?.tilMetricUSUnitHeightInch?.visibility = View.VISIBLE

        binding?.etMetricUnitWeight?.text!!.clear()
        binding?.etMetricUSUnitHeightFeet?.text!!.clear()
        binding?.etMetricUSUnitHeightInch?.text!!.clear()
        binding?.llDisplayBMIResult?.visibility = View.INVISIBLE

    }

    private fun displayBMIResults(bmi:Float){

        val bmiLabel : String
        val bmiDescription : String

        if(bmi.compareTo(15f) <= 0){
            bmiLabel = "Very severly underweight"
            bmiDescription = "Oops! You really need to take better care of yourself! Eat more!"
        }else if(bmi.compareTo(15f) > 0 && bmi.compareTo(16f) <= 0)
        {
            bmiLabel = "severly underweight"
            bmiDescription = "Oops! You really need to take better care of yourself! Eat more!"
        }else if(bmi.compareTo(16f) > 0 && bmi.compareTo(18.5f) <= 0) {
            bmiLabel = "Underweight"
            bmiDescription = "Oops! You really need to take better care of yourself! Eat more!"
        }else if(bmi.compareTo(18.5f) > 0 && bmi.compareTo(25f) <= 0) {
            bmiLabel = "Normal"
            bmiDescription = "Congratulations! You are in a good shape!"
        }else if(bmi.compareTo(25f) > 0 && bmi.compareTo(30f) <= 0) {
            bmiLabel = "OverWeight"
            bmiDescription = "Oops! You really need to take better care of yourself! Workout more!"
        }else if(bmi.compareTo(30f) > 0 && bmi.compareTo(35f) <= 0) {
            bmiLabel = "Obese Class | (Moderately obese)"
            bmiDescription = "Oops! You really need to take better care of yourself! Workout more!"
        }else if(bmi.compareTo(35f) > 0 && bmi.compareTo(40f) <= 0) {
            bmiLabel = "Obese Class || (Severely obese)"
            bmiDescription = "Oops! You really need to take better care of yourself! Workout more!"
        }else{
            bmiLabel = "Obese Class ||| (Very Severely obese)"
            bmiDescription = "Oops! You are in a very dangerous condition! Act now!"
        }

        val bmiValue = BigDecimal(bmi.toDouble())
            .setScale(2,RoundingMode.HALF_EVEN).toString()
        binding?.llDisplayBMIResult?.visibility = View.VISIBLE
        binding?.tvBMIValue?.text = bmiValue
        binding?.tvBMIType?.text = bmiLabel
        binding?.tvBMIDescription?.text = bmiDescription
    }

    private fun validateMetricUnits():Boolean{
        var isValid = true
        if (currentVisibleView == METRIC_UNITS_VIEW){
            if(binding?.etMetricUnitWeight?.text.toString().isEmpty()){
                isValid = false
            }else if(binding?.etMetricUnitHeight?.text.toString().isEmpty()){
                isValid = false}
        }else if (currentVisibleView == US_UNITS_VIEW){
            if(binding?.etMetricUSUnitHeightFeet?.text.toString().isEmpty()){
                    isValid = false
            }else if(binding?.etMetricUSUnitHeightInch?.text.toString().isEmpty()){
                    isValid = false
                }
            }
        return isValid
    }
}