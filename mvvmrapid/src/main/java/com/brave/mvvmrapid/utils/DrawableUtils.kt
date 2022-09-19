@file:Suppress("unused")

package com.brave.mvvmrapid.utils

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import androidx.annotation.ColorInt
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import androidx.core.graphics.toColorInt
import com.blankj.utilcode.util.Utils

/**
 * 获取Drawable
 */
fun Context?.getDrawable(@DrawableRes resId: Int): Drawable? {
    return (this ?: Utils.getApp().applicationContext)?.let {
        ContextCompat.getDrawable(it, resId)
    }
}

/**
 * Drawable染色
 */
fun Drawable?.dyeing(@ColorInt color: Int): Drawable {
    return this?.let {
        val wrap = DrawableCompat.wrap(it)
        DrawableCompat.setTintList(wrap, ColorStateList.valueOf(color))
        wrap
    } ?: ColorDrawable(color)
}

/**
 * Drawable染色
 */
fun Drawable?.dyeingRes(@ColorRes resId: Int): Drawable {
    return this.dyeing(
        try {
            ContextCompat.getColor(Utils.getApp(), resId)
        } catch (e: Exception) {
            Color.TRANSPARENT
        }
    )
}

/**
 * Drawable染色
 */
fun Drawable?.dyeingStr(colorString: String): Drawable {
    return this.dyeing(
        try {
            colorString.toColorInt()
        } catch (e: Exception) {
            Color.TRANSPARENT
        }
    )
}