package com.jsfsapps.pam

import android.graphics.Color
import android.widget.FrameLayout
import android.widget.ImageButton
import android.widget.TextView

class ManageMap {
    fun setMap(layout: FrameLayout, modeType: Boolean, txtPoints: TextView, btnBack: ImageButton){
        if(modeType) {
            //biały
            btnBack.setBackgroundResource(R.drawable.left_arrow_dark)
            layout.setBackgroundResource(R.drawable.background_web_light)
            txtPoints.setTextColor(Color.BLACK)
        }else{
            //czarny
            layout.setBackgroundResource(R.drawable.background_web_dark)
            txtPoints.setTextColor(Color.WHITE)
            btnBack.setBackgroundResource(R.drawable.left_arrow_white)
        }
    }
}