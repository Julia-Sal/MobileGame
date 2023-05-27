package com.jsfsapps.pam

import android.app.Activity
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.RectF
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import kotlin.random.Random
import android.widget.*

class GameMovement (private val imgPlayer: ImageView,
                    private val imgFly: ImageView,
                    private val imgAngrySpider: ImageView,
                    private val txtPoints: EditText,
                    private val txtGameOver: TextView,
                    private val txtGameOverPoints: TextView,
                    private val layout : FrameLayout
){
        private val speed = 20f
        private val angrySpiderSpeed = 60f
        private var screenWidth = 0f
        private var screenHeight = 0f
        val hitboxMargin = 30f
        var playerHitbox = RectF(0f,0f,0f,0f)
        var spiderHitbox = RectF(1f,1f, 1f,1f)
        lateinit var model: Bitmap
        private fun setHitbox(){
            playerHitbox = RectF(
                imgPlayer.x + hitboxMargin,
                imgPlayer.y + hitboxMargin,
                imgPlayer.x + imgPlayer.width - hitboxMargin,
                imgPlayer.y + imgPlayer.height - hitboxMargin
            )

            spiderHitbox = RectF(
                imgAngrySpider.x + hitboxMargin,
                imgAngrySpider.y + hitboxMargin,
                imgAngrySpider.x + imgAngrySpider.width - hitboxMargin,
                imgAngrySpider.y + imgAngrySpider.height - hitboxMargin
            )
        }

        fun handleAccelerometerEvent(x: Float, y: Float) {
            spiderMovement(x, y)
            angrySpiderMovement(x, y)
            setHitbox()
            checkIfObstackleGotTouched()
            gainPoints()
        }

        private fun spiderMovement(x: Float, y: Float) {
            if (imgPlayer.x >= screenWidth) {
                imgPlayer.x = 0f + 1
                animate(imgPlayer, x, y, 200, speed)
            } else if (imgPlayer.x <= 0f) {
                imgPlayer.x = screenWidth - 1
                animate(imgPlayer, x, y, 200, speed)
            } else if (imgPlayer.y >= screenHeight) {
                imgPlayer.y = 0f + 1
                animate(imgPlayer, x, y, 200, speed)
            } else if (imgPlayer.y <= 0f) {
                imgPlayer.y = screenHeight - 1
                animate(imgPlayer, x, y, 200, speed)
            } else {
                animate(imgPlayer, x, y, 200, speed)
            }
        }

        private fun animate(imgModel: ImageView, x: Float, y: Float, dur: Long, speed: Float){
            imgModel.animate()
                .translationXBy(y * speed)
                .translationYBy(x * speed)
                .duration = dur
    }

        private fun angrySpiderMovement(x: Float, y: Float) {
            if (imgAngrySpider.x >= screenWidth) {
                imgAngrySpider.x = 0f + 1
                animate(imgAngrySpider, -x, -y, 200, angrySpiderSpeed)
            } else if (imgAngrySpider.x <= 0f) {
                imgAngrySpider.x = screenWidth - 1
                animate(imgAngrySpider, -x, -y, 200, angrySpiderSpeed)
            } else if (imgAngrySpider.y >= screenHeight) {
                imgAngrySpider.y = 0f + 1
                animate(imgAngrySpider, -x, -y, 200, angrySpiderSpeed)
            } else if (imgAngrySpider.y <= 0f) {
                imgAngrySpider.y = screenHeight - 1
                animate(imgAngrySpider, -x, -y, 200, angrySpiderSpeed)
            } else {
                animate(imgAngrySpider, -x, -y, 200, angrySpiderSpeed)
            }
        }

        private fun gainPoints() {
            if (isCollision(imgPlayer, imgFly)) {
                val currentValue = txtPoints.text.toString().toInt()
                txtPoints.setText((currentValue + 1).toString())
                generateImageView(imgFly, 100, 100)
                imgFly.animate().rotation(Random.nextInt(-50, 50).toFloat()).duration = 10
            }
        }

        private fun checkIfObstackleGotTouched() {
            if (RectF.intersects(playerHitbox, spiderHitbox)) {
                println("playerHitbox")
                txtGameOver.visibility = View.VISIBLE
                txtGameOverPoints.visibility = View.VISIBLE
                txtPoints.setText("0")
            }
        }

        private fun isCollision(player: ImageView, otherObject: ImageView): Boolean {
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

        private fun generateImageView(imageView: ImageView, width: Int, height: Int) {
            val randomX = Random.nextInt(10, screenWidth.toInt() - width - 10)
            val randomY = Random.nextInt(10, screenHeight.toInt() - height - 10)
            imageView.x = randomX.toFloat()
            imageView.y = randomY.toFloat()
        }

        fun hideGameOverViews() {
            txtGameOver.visibility = View.INVISIBLE
            txtGameOverPoints.visibility = View.INVISIBLE
        }

        fun updateScreenSize(screenWidth: Float, screenHeight: Float) {
            this.screenWidth = layout.width.toFloat()
            this.screenHeight = layout.height.toFloat()
        }

        fun resetPlayerPosition() {
            imgPlayer.x = 0f
            imgPlayer.y = 0f
        }


}