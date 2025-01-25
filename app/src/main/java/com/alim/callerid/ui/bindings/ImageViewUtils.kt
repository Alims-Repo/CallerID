package com.alim.callerid.ui.bindings

import android.R
import android.graphics.*
import android.graphics.drawable.BitmapDrawable
import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide

@BindingAdapter("imageUrl", "fallbackName", requireAll = false)
fun loadImage(view: ImageView, imageUrl: String?, fallbackName: String?) {
    if (!imageUrl.isNullOrEmpty()) {
        view.post {
            Glide.with(view.context)
                .load(imageUrl)
                .placeholder(R.color.darker_gray)
                .error(R.color.holo_red_light)
                .circleCrop()
                .into(view)
        }
    } else if (!fallbackName.isNullOrEmpty())
        view.setImageDrawable(generateLetterDrawable(view, fallbackName))
     else view.setImageResource(R.color.darker_gray)
}

fun generateLetterDrawable(view: ImageView, name: String): BitmapDrawable {
    val letter = name.trim().last().uppercase() //take(1).uppercase()

    val bgColor = getColorForLetter(letter)

    val size = 128
    val bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(bitmap)

    val bgPaint = Paint().apply {
        color = bgColor
        isAntiAlias = true
    }

    canvas.drawCircle(size / 2f, size / 2f, size / 2f, bgPaint)

    val textPaint = Paint().apply {
        color = Color.WHITE
        textSize = size / 2.5f
        textAlign = Paint.Align.CENTER
        typeface = Typeface.DEFAULT_BOLD
        isAntiAlias = true
    }

    val textY = (canvas.height / 2f) - (textPaint.descent() + textPaint.ascent()) / 2
    canvas.drawText(letter, canvas.width / 2f, textY, textPaint)

    return BitmapDrawable(view.resources, bitmap)
}

fun getColorForLetter(letter: String): Int {
    val colors = listOf(
        "#FF6200EE", "#FF3700B3", "#FF03DAC5", "#FFFF5722", "#FF4CAF50",
        "#FFFFC107", "#FF009688", "#FFE91E63", "#FF9C27B0", "#FF00BCD4"
    )

    return if (letter.isEmpty() || !letter[0].isLetter())
        Color.parseColor("#FFBDBDBD")
    else Color.parseColor(colors[(letter[0].uppercaseChar().code - 'A'.code) % colors.size])
}