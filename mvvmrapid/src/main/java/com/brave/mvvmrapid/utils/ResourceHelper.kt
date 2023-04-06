@file:JvmName("ResourceHelper")

package com.brave.mvvmrapid.utils

import android.app.Activity
import android.app.Application
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import androidx.annotation.ColorInt
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.core.content.ContextCompat
import androidx.core.graphics.toColorInt
import androidx.fragment.app.Fragment
import com.blankj.utilcode.util.Utils
import com.brave.mvvmrapid.core.CommonApp

/**
 * 全局的context
 */
val globalContext: Context
    get() {
        return CommonApp.instance ?: Utils.getApp() ?: throw Exception("context is not obtained.")
    }

/**
 * 获取颜色值
 */
@ColorInt
fun Context?.getColorIntX(
    @ColorRes
    resId: Int
): Int {
    val mContext = this ?: globalContext
    return try {
        ContextCompat.getColor(mContext, resId)
    } catch (e: Exception) {
        e.printStackTrace()
        Color.TRANSPARENT
    }
}

/**
 * 获取颜色值
 */
@ColorInt
fun Fragment?.getColorIntX(
    @ColorRes
    resId: Int
): Int {
    return this?.context.getColorIntX(resId)
}

/**
 * 获取颜色值
 */
@ColorInt
fun Activity?.getColorIntX(
    @ColorRes
    resId: Int
): Int {
    return (this as? Context?).getColorIntX(resId)
}

/**
 * 获取颜色值
 */
@ColorInt
fun Application?.getColorIntX(
    @ColorRes
    resId: Int
): Int {
    return this?.applicationContext.getColorIntX(resId)
}

/**
 * 获取颜色值
 */
@JvmOverloads
@ColorInt
fun String?.getColorIntX(
    @ColorInt
    defColor: Int = Color.TRANSPARENT
): Int {
    if (null == this) return defColor
    return try {
        this.toColorInt()
    } catch (e: Exception) {
        e.printStackTrace()
        defColor
    }
}

/**
 * 获取Drawable
 */
fun Context?.getDrawableX(
    @DrawableRes
    resId: Int
): Drawable {
    val mContext = this ?: globalContext
    return try {
        ContextCompat.getDrawable(mContext, resId) ?: ColorDrawable(Color.TRANSPARENT)
    } catch (e: Exception) {
        e.printStackTrace()
        ColorDrawable(Color.TRANSPARENT)
    }
}

/**
 * 获取Drawable
 */
fun Fragment?.getDrawableX(
    @DrawableRes
    resId: Int
): Drawable {
    return this?.context.getDrawableX(resId)
}

/**
 * 获取Drawable
 */
fun Activity?.getDrawableX(
    @DrawableRes
    resId: Int
): Drawable {
    return (this as? Context?).getDrawableX(resId)
}

/**
 * 获取Drawable
 */
fun Application?.getDrawableX(
    @DrawableRes
    resId: Int
): Drawable {
    return this?.applicationContext.getDrawableX(resId)
}