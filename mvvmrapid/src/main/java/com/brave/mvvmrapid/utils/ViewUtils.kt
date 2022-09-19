@file:Suppress("unused")

package com.brave.mvvmrapid.utils

import android.graphics.Color
import android.graphics.drawable.StateListDrawable
import android.view.View
import android.widget.ImageView
import androidx.annotation.ColorInt
import androidx.annotation.ColorRes
import androidx.core.content.ContextCompat
import androidx.core.graphics.toColorInt
import com.blankj.utilcode.util.Utils

/**
 * 如果该[View]是[ImageView]则调用[ImageView.setImageDrawable]
 *
 * 否则调用[View.setBackground]
 */
fun View?.drawBackground(): DrawableGenerate {
    return DrawableGenerate.newInstance().with(this)
}

/**
 * View图片颜色渲染（View为ImageView默认渲染位图，其他则默认渲染背景）
 *
 * @param color 渲染为指定颜色
 */
fun View?.drawableTint(@ColorInt color: Int) {
    if (null == this) return
    val mDrawable =
        if (this is ImageView) {
            this.drawable ?: this.background
        } else {
            this.background
        } ?: return
    mDrawable.dyeing(color)
}

/**
 * View图片颜色渲染（View为ImageView默认渲染位图，其他则默认渲染背景）
 *
 * @param resId 渲染为指定颜色资源
 */
fun View?.drawableTintRes(@ColorRes resId: Int) {
    return this.drawableTint(
        try {
            ContextCompat.getColor(Utils.getApp(), resId)
        } catch (e: Exception) {
            Color.TRANSPARENT
        }
    )
}

/**
 * View图片颜色渲染（View为ImageView默认渲染位图，其他则默认渲染背景）
 *
 * @param colorString 渲染为指定颜色字符串
 */
fun View?.drawableTintStr(colorString: String) {
    return this.drawableTint(
        try {
            colorString.toColorInt()
        } catch (e: Exception) {
            Color.TRANSPARENT
        }
    )
}

/**
 * 封裝View的[StateListDrawable]绘制
 *
 * @param isDrawBg 强制绘制背景（默认不强制）
 */
fun View.drawState(isDrawBg: Boolean = false): StateListDrawable {
    val drawable = StateListDrawable()
    when {
        isDrawBg -> {
            this.background = drawable
        }
        this is ImageView -> {
            this.setImageDrawable(drawable)
        }
        else -> {
            this.background = drawable
        }
    }
    return drawable
}