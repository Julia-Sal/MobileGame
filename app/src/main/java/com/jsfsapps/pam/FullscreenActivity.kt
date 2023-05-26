package com.jsfsapps  .pam

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.ActivityInfo
import android.graphics.Point
import android.graphics.Rect
import android.graphics.drawable.Drawable
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.DisplayMetrics
import android.view.MotionEvent
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import com.jsfsapps.pam.databinding.ActivityFullscreenBinding
import kotlin.random.Random
import android.view.ViewGroup.LayoutParams
import android.widget.*


class FullscreenActivity : AppCompatActivity(){

    private lateinit var binding: ActivityFullscreenBinding
    private lateinit var fullscreenContent: TextView
    private lateinit var fullscreenContentControls: LinearLayout
    private val hideHandler = Handler(Looper.myLooper()!!)

    lateinit var screenWidth: String
    lateinit var screenHeight: String


    //game changeable
    val speed = 10;
    val angrySpiderSpeed = 20;

    //game - define sensor variables
    private var mSensorManager: SensorManager? = null
    private var mAccelerometer: Sensor? = null
    private lateinit var imgPlayer: ImageView
    private lateinit var layout: FrameLayout
    private lateinit var imgFly: ImageView
    private lateinit var imgAngrySpider: ImageView
    private lateinit var txtPoints: EditText
    private lateinit var txtGameOver: TextView
    private lateinit var txtGameOverPoints: TextView
    private lateinit var imgBtnBack: ImageButton

    private val mSensorEventListener = object : SensorEventListener {
        override fun onAccuracyChanged(sensor: Sensor, accuracy: Int) {
            // implementacja metody onAccuracyChanged
        }

        override fun onSensorChanged(event: SensorEvent) {
            if (event.sensor.type == Sensor.TYPE_ACCELEROMETER) {

                val x = event.values[0]
                val y = event.values[1]

                checkIfObstackleGotTouched()
                gainPoints()
                spiderMovement(x,y)
                angrySpiderMovement(x,y)

                }
            }
        }
  /*  txtGameOver.setOnClickListener {
        Toast.makeText(this, "Kliknięto TextView!", Toast.LENGTH_SHORT).show()
    }*/

    fun spiderMovement(x: Float, y: Float){
        if(imgPlayer.x>=screenWidth.toFloat()){
            imgPlayer.x = 0f+1
        }else if(imgPlayer.x<=0f){
            imgPlayer.x = screenWidth.toFloat() - 1
        }else if(imgPlayer.y>=screenHeight.toFloat()){
            imgPlayer.y = 0f+1
        }else if(imgPlayer.y<=0f){
            imgPlayer.y = screenHeight.toFloat() - 1
        }else{
            imgPlayer.animate()
                .translationXBy(y*speed)
                .translationYBy(x*speed)
                .duration = 0
        }
    }
    fun angrySpiderMovement(x: Float, y: Float){
        if(imgAngrySpider.x>=screenWidth.toFloat()){
            imgAngrySpider.x = 0f+1
        }else if(imgAngrySpider.x<=0f){
            imgAngrySpider.x = screenWidth.toFloat() - 1
        }else if(imgAngrySpider.y>=screenHeight.toFloat()){
            imgAngrySpider.y = 0f+1
        }else if(imgAngrySpider.y<=0f){
            imgAngrySpider.y = screenHeight.toFloat() - 1
        }else{
            imgAngrySpider.animate()
                .translationXBy(-y*angrySpiderSpeed)
                .translationYBy(-x*angrySpiderSpeed)
                .duration = 0
        }
    }
    fun gainPoints(){
        if(isCollision(imgPlayer, imgFly)){
            val currentValue = txtPoints.text.toString().toInt()
            txtPoints.setText((currentValue + 1).toString())
            generateImageView(imgFly, 100, 100)
            imgFly.animate().rotation(Random.nextInt(-50, 50).toFloat()).duration = 10
        }
    }
    fun checkIfObstackleGotTouched(){
        if(isCollision(imgPlayer, imgAngrySpider)){
            txtGameOver.visibility = View.VISIBLE
            txtGameOverPoints.visibility = View.VISIBLE
            txtPoints.setText((0).toString())
        }
    }

    fun isCollision(player: ImageView, otherObject: ImageView): Boolean {
        val playerX = player.x
        val playerY = player.y
        val playerWidth = player.width
        val playerHeight = player.height

        val otherX = otherObject.x
        val otherY = otherObject.y
        val otherWidth = otherObject.width
        val otherHeight = otherObject.height

        return playerX + playerWidth >= otherX &&
                playerX <= otherX + otherWidth &&
                playerY + playerHeight >= otherY &&
                playerY <= otherY + otherHeight

    }

    private fun generateImageView(imageView: ImageView, width: Int, height: Int){
        val randomX = Random.nextInt(10, screenWidth.toInt() - width - 10)
        val randomY = Random.nextInt(10, screenHeight.toInt() - height - 10)
        imageView.setX(randomX.toFloat())
        imageView.setY(randomY.toFloat())
    }

    //game-end


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
    private val showPart2Runnable = Runnable {
        supportActionBar?.show()
        fullscreenContentControls.visibility = View.VISIBLE
    }
    private var isFullscreen: Boolean = false

    private val hideRunnable = Runnable { hide() }

    private val delayHideTouchListener = View.OnTouchListener { view, motionEvent ->
        when (motionEvent.action) {
            MotionEvent.ACTION_DOWN -> if (AUTO_HIDE) {
                delayedHide(AUTO_HIDE_DELAY_MILLIS)
            }
            MotionEvent.ACTION_UP -> view.performClick()
            else -> {
            }
        }
        false
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) //lock orientation


        binding = ActivityFullscreenBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        isFullscreen = true
        fullscreenContent = binding.fullscreenContent
        fullscreenContent.setOnClickListener { toggle() }
        fullscreenContentControls = binding.fullscreenContentControls
        imgPlayer = binding.imgPlayer // initialize imgPlayer
        layout = binding.webLayout
        imgFly = binding.imgFly
        txtPoints = binding.txtPoints
        imgAngrySpider = binding.imgAngrySpider
        txtGameOver = binding.txtGameOver
        txtGameOverPoints = binding.txtGameOverPoints

        imgBtnBack = binding.imgBtnBack
        imgBtnBack.setOnClickListener {
            val intent = Intent(this, menu::class.java)
            startActivity(intent)
        }

        //game sensors
        mSensorManager = getSystemService(SENSOR_SERVICE) as SensorManager
        mAccelerometer = mSensorManager!!.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        //game-end

        txtGameOver.setOnClickListener {
            txtGameOverPoints.visibility = View.INVISIBLE
            txtGameOver.visibility = View.INVISIBLE
        }
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (hasFocus) {
            screenWidth = layout.width.toString()
            screenHeight = layout.height.toString()

        }
    }

    override fun onResume() {
        super.onResume()
        imgPlayer.x = 0f
        imgPlayer.y = 0f
        mSensorManager?.registerListener(mSensorEventListener, mAccelerometer, SensorManager.SENSOR_DELAY_NORMAL)
    }

    override fun onPause() {
        super.onPause()
        mSensorManager?.unregisterListener(mSensorEventListener)
    }

    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)

        delayedHide(100)
    }

    private fun toggle() {
        if (isFullscreen) {
            hide()
        } else {
            show()
        }
    }

    private fun hide() {
        supportActionBar?.hide()
        fullscreenContentControls.visibility = View.GONE
        isFullscreen = false

        hideHandler.removeCallbacks(showPart2Runnable)
        hideHandler.postDelayed(hidePart2Runnable, UI_ANIMATION_DELAY.toLong())
    }

    private fun show() {
        if (Build.VERSION.SDK_INT >= 30) {
            fullscreenContent.windowInsetsController?.show(WindowInsets.Type.statusBars() or WindowInsets.Type.navigationBars())
        } else {
            fullscreenContent.systemUiVisibility =
                View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or
                        View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
        }
        isFullscreen = true

        hideHandler.removeCallbacks(hidePart2Runnable)
        hideHandler.postDelayed(showPart2Runnable, UI_ANIMATION_DELAY.toLong())
    }

    private fun delayedHide(delayMillis: Int) {
        hideHandler.removeCallbacks(hideRunnable)
        hideHandler.postDelayed(hideRunnable, delayMillis.toLong())
    }

    companion object {
        private const val AUTO_HIDE = true
        private const val AUTO_HIDE_DELAY_MILLIS = 3000
        private const val UI_ANIMATION_DELAY = 300
    }
}

