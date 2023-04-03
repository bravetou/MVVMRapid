@file:JvmName("DrawableHelper")

package com.brave.mvvmrapid.utils

import android.content.res.ColorStateList
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import androidx.annotation.ColorInt
import androidx.annotation.ColorRes
import androidx.core.graphics.drawable.DrawableCompat

/**
 * Drawable染色
 */
fun Drawable?.dyeing(
    @ColorInt
    color: Int
): Drawable {
    return this?.let {
        val wrap = DrawableCompat.wrap(it)
        DrawableCompat.setTintList(wrap, ColorStateList.valueOf(color))
        wrap
    } ?: ColorDrawable(color)
}

/**
 * Drawable染色
 */
fun Drawable?.dyeingRes(
    @ColorRes
    resId: Int
): Drawable {
    return this.dyeing(globalContext.getColorIntX(resId))
}

/**
 * Drawable染色
 */
fun Drawable?.dyeingStr(colorStr: String): Drawable {
    return this.dyeing(colorStr.getColorIntX())
}