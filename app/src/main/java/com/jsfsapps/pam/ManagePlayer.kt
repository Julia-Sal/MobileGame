package com.jsfsapps.pam

import android.content.Context
import android.graphics.Bitmap
import android.view.Gravity
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.core.content.res.ResourcesCompat
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException

class ManagePlayer{

    public var currentModel = R.drawable.current_model

    fun createPlayer(context: Context, container: FrameLayout): ImageView{

        val imageView = ImageView(context)
        imageView.layoutParams = FrameLayout.LayoutParams(
            convertDpToPx(context, 90),
            convertDpToPx(context, 90),
            Gravity.CENTER
        )
        imageView.setBackgroundResource(android.R.color.transparent)
        imageView.contentDescription = context.getString(R.string.imgPlayerContentDescription)
        println(currentModel)
        imageView.setImageResource(currentModel)

        container.addView(imageView)
        return imageView
    }

    private fun convertDpToPx(context: Context, dp: Int): Int {
        val density = context.resources.displayMetrics.density
        return (dp * density).toInt()
    }

}