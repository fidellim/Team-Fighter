package com.fidellim.teamfighter

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.os.PersistableBundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog

class MainActivity : AppCompatActivity() {

    internal lateinit var tapMeButton : Button
    internal lateinit var gameScoreTextView : TextView
    internal lateinit var timeLeftTextView : TextView
    internal var score = 0
    internal var isGameStarted = false
    internal lateinit var countDownTimer: CountDownTimer
    internal val TAG = MainActivity::class.java.simpleName

    //milliseconds
    internal val initialCountDown: Long = 60000
    internal val countDownInterval: Long = 1000
    internal var timeLeftOnTimer: Long = 60000

    companion object {
        private val SCORE_KEY = "SCORE_KEY"
        private val TIME_LEFT_KEY = "TIME_LEFT_KEY"
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        super.onCreateOptionsMenu(menu)
        menuInflater.inflate(R.menu.menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        if(item.itemId == R.id.action_about){
            showInfo()
        }

        return true
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        tapMeButton = findViewById<Button>(R.id.btnTapMe)
        gameScoreTextView = findViewById<TextView>(R.id.gameScore)
        timeLeftTextView = findViewById<TextView>(R.id.timeLeft)

        if(savedInstanceState != null){
            score = savedInstanceState.getInt(SCORE_KEY)
            timeLeftOnTimer = savedInstanceState.getLong(TIME_LEFT_KEY)
            restoreGame()
            Log.d(TAG, " onCreate called. Score is $score")
        }else{
            Log.d(TAG, " onCreate called. Score is $score")
            resetGame()
        }

        tapMeButton.setOnClickListener(){ view ->
            val bounceAnimation = AnimationUtils.loadAnimation(this, R.anim.bounce)
            view.startAnimation(bounceAnimation)
            incrementScore()
        }

    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)

        outState.putInt(SCORE_KEY, score)
        outState.putLong(TIME_LEFT_KEY, timeLeftOnTimer)
        countDownTimer.cancel()
        Log.d(TAG, "onSaveInstanceState: Saving score: $score & Time left: $timeLeftOnTimer")
    }

    override fun onDestroy() {
        super.onDestroy()

        Log.d(TAG, "onDestroy called.")
    }

    private fun endGame() {
        Toast.makeText(this, getString(R.string.gameOver, score.toString()), Toast.LENGTH_LONG).show()
        resetGame()
    }

    private fun startGame(){
        countDownTimer.start()
        isGameStarted = true
    }

    private fun resetGame() {
        score = 0
        gameScoreTextView.text = getString(R.string.your_score, score.toString())
        val initialTimeLeft = initialCountDown/1000
        timeLeftTextView.text = getString(R.string.time_left, initialTimeLeft.toString())

        countDownTimer = object : CountDownTimer(initialCountDown, countDownInterval) {
            override fun onTick(millisUntilFinished: Long) {
                timeLeftOnTimer = millisUntilFinished
                val timeLeft = millisUntilFinished / 1000
                timeLeftTextView.text = getString(R.string.time_left, timeLeft.toString())
            }

            override fun onFinish() {
                endGame()
            }
        }

        isGameStarted = false
    }

    //increment score
    private fun incrementScore () {

        //other of saying
        //isGameStarted == false
        if (!isGameStarted){
            startGame()
        }

        score = score + 1
        var newScore = getString(R.string.your_score, score.toString())
        gameScoreTextView.text = newScore

        val blinkAnimation = AnimationUtils.loadAnimation(this, R.anim.blink)
        gameScoreTextView.startAnimation(blinkAnimation)


    }

    //when user change orientation of device
    private fun restoreGame() {
        gameScoreTextView.text = getString(R.string.your_score, score.toString())

        val restoreTime = timeLeftOnTimer /1000
        timeLeftTextView.text = getString(R.string.time_left, restoreTime.toString())

        countDownTimer = object : CountDownTimer(timeLeftOnTimer, countDownInterval) {
            override fun onTick(millisUntilFinished: Long) {
                timeLeftOnTimer = millisUntilFinished
                val timeLeft = millisUntilFinished / 1000
                timeLeftTextView.text = getString(R.string.time_left, timeLeft.toString())
            }

            override fun onFinish() {
                endGame()
            }
        }

        countDownTimer.start()
        isGameStarted = true
    }

    private fun showInfo() {
        val dialogTitle = getString(R.string.about_title, BuildConfig.VERSION_NAME)
        val dialogMessage = getString(R.string.about_message)
        val builder = AlertDialog.Builder(this)
        builder.setTitle(dialogTitle)
        builder.setMessage(dialogMessage)
        builder.create().show()
    }
}
