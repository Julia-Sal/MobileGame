package com.jsfsapps  .pam

import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.ActivityInfo
import android.content.res.Configuration
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.MotionEvent
import android.view.View
import android.view.WindowInsets
import androidx.appcompat.app.AppCompatActivity
import com.jsfsapps.pam.databinding.ActivityFullscreenBinding
import android.widget.*


class FullscreenActivity : AppCompatActivity(){

    private lateinit var binding: ActivityFullscreenBinding
    private lateinit var fullscreenContent: TextView
    private lateinit var fullscreenContentControls: LinearLayout
    private val hideHandler = Handler(Looper.myLooper()!!)


    private lateinit var gameMovement: GameMovement
    private lateinit var managePlayer: ManagePlayer
    private lateinit var manageMap: ManageMap
    private var mSensorManager: SensorManager? = null
    private var mAccelerometer: Sensor? = null
    private var modeType = true

    private val mSensorEventListener = object : SensorEventListener {
        override fun onAccuracyChanged(sensor: Sensor, accuracy: Int) {
            // implementacja metody onAccuracyChanged
        }

        override fun onSensorChanged(event: SensorEvent) {
            if (event.sensor.type == Sensor.TYPE_ACCELEROMETER) {
                gameMovement.handleAccelerometerEvent(event.values[0], event.values[1]);
                }
            }
        }

    private fun setupGame(imgPlayer: ImageView) {
        gameMovement = GameMovement(
            imgPlayer = imgPlayer,
            imgFly = binding.imgFly,
            imgAngrySpider = binding.imgAngrySpider,
            txtPoints = binding.txtPoints,
            txtGameOver = binding.txtGameOver,
            layout = binding.webLayout
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) //lock orientation
        binding = ActivityFullscreenBinding.inflate(layoutInflater)
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN
        initializeViews()
        managePlayer = ManagePlayer()
        manageMap = ManageMap()
        val container = findViewById<FrameLayout>(R.id.mainLayout) // Znajdź kontener LinearLayout

        var imgPlayer = managePlayer.createPlayer(this, container)
        setupGame(imgPlayer)

        //game sensors
        mSensorManager = getSystemService(SENSOR_SERVICE) as SensorManager
        mAccelerometer = mSensorManager!!.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)

        binding.txtGameOver.setOnClickListener {
            gameMovement.hideGameOverViews()
        }
        binding.imgBtnBack.setOnClickListener {
            val intent = Intent(this, Menu::class.java)
            startActivity(intent)
        }
        binding.btnChangeMode.setOnClickListener{
            manageMap.setMap(binding.webLayout, modeType, binding.txtPoints, binding.imgBtnBack)
            modeType = !modeType

        }


    }

    @SuppressLint("InlinedApi")
    private val hidePart2Runnable = Runnable {
        if (Build.VERSION.SDK_INT >= 30) {
            fullscreenContent.windowInsetsController?.hide(WindowInsets.Type.statusBars() or WindowInsets.Type.navigationBars())
        } else {
            fullscreenContent.systemUiVisibility =
                View.SYSTEM_UI_FLAG_LOW_PROFILE or
                        View.SYSTEM_UI_FLAG_FULLSCREEN or
                        View.SYSTEM_UI_FLAG_LAYOUT_STABLE or
                        View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY or
                        View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION or
                        View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
        }
    }

    private var isFullscreen: Boolean = false

    private fun initializeViews() {
        binding = ActivityFullscreenBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        fullscreenContent = binding.fullscreenContent
        fullscreenContentControls = binding.fullscreenContentControls
    }




    override fun onWindowFocusChanged(hasFocus: Boolean) {

        super.onWindowFocusChanged(hasFocus)
        if (hasFocus) {

            val layout = binding.webLayout
            var screenWidth = layout.width
            var screenHeight = layout.height
            gameMovement.updateScreenSize(screenWidth.toFloat(), screenHeight.toFloat())
        }
    }
    override fun onResume() {
        super.onResume()
        hideSystemUI()
        gameMovement.resetPlayerPosition()
        mSensorManager?.registerListener(
            mSensorEventListener,
            mAccelerometer,
            SensorManager.SENSOR_DELAY_NORMAL
        )
    }
    override fun onPause() {
        super.onPause()
        mSensorManager?.unregisterListener(mSensorEventListener)
    }
    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)
        hide()
    }
    private fun hide() {
        supportActionBar?.hide()
        fullscreenContentControls.visibility = View.GONE
        isFullscreen = false

        hideHandler.postDelayed(hidePart2Runnable, UI_ANIMATION_DELAY.toLong())
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        hideSystemUI()
    }

    private fun hideSystemUI() {
        window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_FULLSCREEN
                or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY)
    }

    companion object {
        private const val AUTO_HIDE = true
        private const val AUTO_HIDE_DELAY_MILLIS = 3000
        private const val UI_ANIMATION_DELAY = 300
    }
}

