package com.example.a7minuteworkout

import android.app.Dialog
import android.content.Intent
import android.media.MediaPlayer
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.speech.tts.TextToSpeech
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.a7minuteworkout.databinding.ActivityExerciseBinding
import com.example.a7minuteworkout.databinding.ActivityMainBinding
import com.example.a7minuteworkout.databinding.DialogCustomBackConfirmationBinding
import org.w3c.dom.Text
import java.lang.Exception
import java.util.*
import kotlin.collections.ArrayList

class ExerciseActivity : AppCompatActivity(), TextToSpeech.OnInitListener {
    private var binding : ActivityExerciseBinding? = null
    private var restDuration: Long = 10000
    private var restTimer: CountDownTimer? = null
    private var restProgress: Int = 0

    private var exerciseDuration: Long = 30000
    private var exerciseTimer: CountDownTimer? = null
    private var exerciseProgress: Int = 0

    private var exerciseList : ArrayList<ExerciseModel>? = null
    private var currentExercisePosition = -1

    private var tts: TextToSpeech? = null
    private var player: MediaPlayer? = null
    
    private var exerciseAdapter : ExerciseStatusAdapter? = null



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityExerciseBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        setSupportActionBar(binding?.toolbarExercise)

        exerciseList = Constants.defaultExerciseList()

        tts = TextToSpeech(this, this)

        if(supportActionBar != null){
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
        }
        binding?.toolbarExercise?.setNavigationOnClickListener {
            customDialogForBackButton()
        }

        setupRestView()
        setupExerciseStatusRecyclerView()

    }

    override fun onBackPressed() {
        customDialogForBackButton()
        //super.onBackPressed()
    }

    private fun customDialogForBackButton(){
        val customDialog = Dialog(this)
        val dialogBinding = DialogCustomBackConfirmationBinding.inflate(layoutInflater)
        customDialog.setContentView(dialogBinding.root)
        customDialog.setCanceledOnTouchOutside(false)
        dialogBinding.btnYes.setOnClickListener{
            this@ExerciseActivity.finish()
            customDialog.dismiss()
        }
        dialogBinding.btnNo.setOnClickListener {
            customDialog.dismiss()
        }
        customDialog.show()
    }

    private fun setupExerciseStatusRecyclerView(){
        binding?.rvExerciseStatus?.layoutManager =
            //GridLayoutManager()
            LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)

        exerciseAdapter = ExerciseStatusAdapter(exerciseList!!)
        binding?.rvExerciseStatus?.adapter = exerciseAdapter
    }

    private fun setupRestView(){

        try{
            val soundURI = Uri.parse(
                "android.resource://com.example.a7minuteworkout/" + R.raw.press_start)
            player = MediaPlayer.create(applicationContext, soundURI)
            player?.isLooping = false
            player?.start()
        }catch (e: Exception){
            e.printStackTrace()
        }

        binding?.flRestView?.visibility = View.VISIBLE
        binding?.tvTitle?.visibility = View.VISIBLE
        binding?.flExerciseView?.visibility = View.INVISIBLE
        binding?.tvExerciseName?.visibility = View.INVISIBLE
        binding?.ivImage?.visibility = View.INVISIBLE
        binding?.tvNextExercise?.visibility = View.VISIBLE
        if(restTimer!= null){
            restTimer?.cancel()
            restProgress = 0
        }


        binding?.tvNextExercise?.text = exerciseList!![currentExercisePosition+1].getName()
        setRestProgressBar()
    }

    private fun setupExerciseView(){
        binding?.flRestView?.visibility = View.INVISIBLE
        binding?.tvTitle?.visibility = View.INVISIBLE
        binding?.flExerciseView?.visibility = View.VISIBLE
        binding?.tvExerciseName?.visibility = View.VISIBLE
        binding?.ivImage?.visibility = View.VISIBLE
        binding?.tvNextExercise?.visibility = View.INVISIBLE
        if(exerciseTimer != null){
            exerciseTimer?.cancel()
            exerciseProgress = 0
        }

        speakOut(exerciseList!![currentExercisePosition].getName())

        binding?.ivImage?.setImageResource(exerciseList!![currentExercisePosition].getImage())
        binding?.tvExerciseName?.text = "${exerciseList?.get(currentExercisePosition)?.getName()}"

        setExerciseProgressBar()
    }

    private fun setRestProgressBar(){
        binding?.progressBar?.progress = restProgress

        restTimer = object: CountDownTimer(restDuration, 1000) {
            override fun onTick(p0: Long) {
                restProgress++
                binding?.progressBar?.progress = (restDuration/1000).toInt() - restProgress
                binding?.tvTimer?.text = ("${restDuration/1000 - restProgress}")
            }

            override fun onFinish() {
                currentExercisePosition++

                exerciseList!![currentExercisePosition].setIsSelected(true)
                exerciseAdapter!!.notifyDataSetChanged()
                setupExerciseView()
            }
        }.start()

        binding?.ibSkip?.setOnClickListener {
            restTimer?.cancel()
            currentExercisePosition++
            setupExerciseView()
        }
    }

    private fun setExerciseProgressBar(){
        binding?.progressExercise?.progress = exerciseProgress

        exerciseTimer = object: CountDownTimer(exerciseDuration, 1000) {
            override fun onTick(p0: Long) {
                exerciseProgress++
                binding?.progressExercise?.progress = (exerciseDuration/1000).toInt() - exerciseProgress
                binding?.tvTimerExercise?.text = ("${exerciseDuration/1000 - exerciseProgress}")
            }

            override fun onFinish() {

                if(currentExercisePosition < exerciseList?.size!! - 1){
                    exerciseList!![currentExercisePosition].setIsSelected(false)
                    exerciseList!![currentExercisePosition].setIsCompleted(true)
                    exerciseAdapter!!.notifyDataSetChanged()
                    setupRestView()
                }else{
                    /*Toast.makeText(
                        this@ExerciseActivity,
                        "Congratz!",
                        Toast.LENGTH_LONG
                    ).show()*/
                    finish()
                    val intent = Intent(this@ExerciseActivity, FinishActivity::class.java)
                    startActivity(intent)
                }

            }
        }.start()

        binding?.ibSkip?.setOnClickListener {
            restTimer?.cancel()
            exerciseList!![currentExercisePosition].setIsSelected(false)
            exerciseList!![currentExercisePosition].setIsCompleted(true)
            exerciseAdapter!!.notifyDataSetChanged()
            if(currentExercisePosition < exerciseList?.size!! - 1){
                setupRestView()
            }else{
                /*Toast.makeText(
                    this@ExerciseActivity,
                    "Congratz!",
                    Toast.LENGTH_LONG
                ).show()*/
                finish()
                val intent = Intent(this@ExerciseActivity, FinishActivity::class.java)
                startActivity(intent)
            }
    }
    }
    override fun onDestroy() {
        super.onDestroy()

        if(restTimer!= null){
            restTimer?.cancel()
            restProgress = 0
        }

        if(exerciseTimer != null){
            exerciseTimer?.cancel()
            exerciseProgress = 0
        }

        if(tts != null){
            tts!!.stop()
            tts!!.shutdown()
        }

        if(player != null){
            player!!.stop()
        }

        binding = null
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            val result = tts?.setLanguage(Locale.US)

            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                Log.e("TTS", "The language specified is not supported!")
            }
        }else {
            Log.e("TTS", "Initialization Failed!")
        }
    }

    private fun speakOut(text: String) {
        tts!!.speak(text, TextToSpeech.QUEUE_FLUSH, null, "")
    }

}
