package com.brave.mvvmrapid.utils

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.view.View
import android.widget.ImageView
import androidx.annotation.*
import androidx.annotation.IntRange
import androidx.core.content.ContextCompat
import androidx.core.graphics.toColorInt
import com.blankj.utilcode.util.Utils
import com.brave.mvvmrapid.core.CommonApp
import java.lang.ref.WeakReference

/**
 * ***author***     ：brave tou
 *
 * ***blog***       ：https://blog.csdn.net/bravetou
 *
 * ***time***       ：2022/9/9 15:55
 *
 * ***desc***       ：Drawable生成工具
 */
@Suppress("unused", "ProtectedInFinal", "MemberVisibilityCanBePrivate", "USELESS_ELVIS")
class DrawableGenerate private constructor() {
    /**
     * 背景颜色
     */
    @ColorInt
    private var mBackgroundColor = Color.TRANSPARENT

    /**
     * 开始颜色
     */
    @ColorInt
    private var mStartColor: Int? = null

    /**
     * 中间颜色
     */
    @ColorInt
    private var mCenterColor: Int? = null

    /**
     * 结束颜色
     */
    @ColorInt
    private var mEndColor: Int? = null

    /**
     * 渐变的形状类型
     */
    private var mShape: Int = GradientDrawable.RECTANGLE

    /**
     * 渐变类型
     */
    private var mGradientType: Int = GradientDrawable.LINEAR_GRADIENT

    /**
     * 渐变方向
     */
    private var mGradientOrientation: GradientDrawable.Orientation =
        GradientDrawable.Orientation.LEFT_RIGHT

    /**
     * 边框宽度
     */
    @Px
    private var mBorderWidth = 0

    /**
     * 边框颜色
     */
    @ColorInt
    private var mBorderColor = Color.TRANSPARENT

    /**
     * 左上角圆角
     */
    @Px
    private var mRoundedCornersLT = 0.0f

    /**
     * 右上角圆角
     */
    @Px
    private var mRoundedCornersRT = 0.0f

    /**
     * 右下角圆角
     */
    @Px
    private var mRoundedCornersRB = 0.0f

    /**
     * 左下角圆角
     */
    @Px
    private var mRoundedCornersLB = 0.0f

    /**
     * 不透明度
     */
    @IntRange(from = 0, to = 255)
    private var mAlpha: Int = 255

    companion object {
        @JvmStatic
        fun newInstance() = DrawableGenerate()
    }

    /**
     * [Context]
     */
    private val context: Context by lazy {
        CommonApp.instance ?: Utils.getApp()
    }

    /**
     * 根据颜色资源ID获取颜色值
     *
     * @param resId 颜色资源ID
     */
    @ColorInt
    private fun getColorByRes(@ColorRes resId: Int): Int {
        return try {
            ContextCompat.getColor(context, resId)
        } catch (e: Exception) {
            e.printStackTrace()
            Color.TRANSPARENT
        }
    }

    /**
     * 根据颜色字符串获取颜色值
     *
     * @param colorString 颜色字符串
     */
    @ColorInt
    private fun getColorByStr(colorString: String): Int {
        return try {
            colorString.toColorInt()
        } catch (e: Exception) {
            e.printStackTrace()
            Color.TRANSPARENT
        }
    }

    /**
     * 设置背景颜色
     *
     * @param color Int颜色值
     */
    fun setBackgroundColor(@ColorInt color: Int): DrawableGenerate {
        this.mBackgroundColor = color
        return this
    }

    /**
     * 设置背景颜色
     *
     * @param resId 颜色资源ID
     */
    fun setBackgroundColorRes(@ColorRes resId: Int): DrawableGenerate {
        return setBackgroundColor(getColorByRes(resId))
    }

    /**
     * 设置背景颜色
     *
     * @param colorString 颜色字符串
     * @see toColorInt
     */
    fun setBackgroundColorStr(colorString: String): DrawableGenerate {
        return setBackgroundColor(getColorByStr(colorString))
    }

    /**
     * 设置边框宽度
     *
     * @param width 边框宽度，单位[Px]
     */
    fun setBorderWidth(@Px width: Int): DrawableGenerate {
        this.mBorderWidth = width
        return this
    }

    /**
     * 设置边框宽度
     *
     * @param width 边框宽度，单位[Px]
     */
    fun setBorderWidth(@Px width: Float): DrawableGenerate {
        return setBorderWidth(width.toInt())
    }

    /**
     * 设置边框宽度
     *
     * @param width 边框宽度，单位[Px]
     */
    fun setBorderWidth(@Px width: Double): DrawableGenerate {
        return setBorderWidth(width.toInt())
    }

    /**
     * 设置边框颜色
     *
     * @param color Int颜色值
     */
    fun setBorderColor(@ColorInt color: Int): DrawableGenerate {
        this.mBorderColor = color
        return this
    }

    /**
     * 设置边框颜色
     *
     * @param resId 颜色资源ID
     */
    fun setBorderColorRes(@ColorRes resId: Int): DrawableGenerate {
        return setBorderColor(getColorByRes(resId))
    }

    /**
     * 设置边框颜色
     *
     * @param colorString 颜色字符串
     */
    fun setBorderColorStr(colorString: String): DrawableGenerate {
        return setBorderColor(getColorByStr(colorString))
    }

    /**
     * 设置圆角
     *
     * @param corners 圆角半径，单位[Px]
     */
    fun setRoundedCorners(@Px corners: Float): DrawableGenerate {
        this.mRoundedCornersLT = corners
        this.mRoundedCornersRT = corners
        this.mRoundedCornersRB = corners
        this.mRoundedCornersLB = corners
        return this
    }

    /**
     * 设置圆角
     *
     * @param corners 圆角半径，单位[Px]
     */
    fun setRoundedCorners(@Px corners: Int): DrawableGenerate {
        return setRoundedCorners(corners.toFloat())
    }

    /**
     * 设置圆角
     *
     * @param corners 圆角半径，单位[Px]
     */
    fun setRoundedCorners(@Px corners: Double): DrawableGenerate {
        return setRoundedCorners(corners.toFloat())
    }

    /**
     * 设置圆角（左上角）
     *
     * @param corners 圆角半径，单位[Px]
     */
    fun setRoundedCornersLT(@Px corners: Float): DrawableGenerate {
        this.mRoundedCornersLT = corners
        return this
    }

    /**
     * 设置圆角（左上角）
     *
     * @param corners 圆角半径，单位[Px]
     */
    fun setRoundedCornersLT(@Px corners: Int): DrawableGenerate {
        return setRoundedCornersLT(corners.toFloat())
    }

    /**
     * 设置圆角（左上角）
     *
     * @param corners 圆角半径，单位[Px]
     */
    fun setRoundedCornersLT(@Px corners: Double): DrawableGenerate {
        return setRoundedCornersLT(corners.toFloat())
    }

    /**
     * 设置圆角（右上角）
     *
     * @param corners 圆角半径，单位[Px]
     */
    fun setRoundedCornersRT(@Px corners: Float): DrawableGenerate {
        this.mRoundedCornersRT = corners
        return this
    }

    /**
     * 设置圆角（右上角）
     *
     * @param corners 圆角半径，单位[Px]
     */
    fun setRoundedCornersRT(@Px corners: Int): DrawableGenerate {
        return setRoundedCornersRT(corners.toFloat())
    }

    /**
     * 设置圆角（右上角）
     *
     * @param corners 圆角半径，单位[Px]
     */
    fun setRoundedCornersRT(@Px corners: Double): DrawableGenerate {
        return setRoundedCornersRT(corners.toFloat())
    }

    /**
     * 设置圆角（右下角）
     *
     * @param corners 圆角半径，单位[Px]
     */
    fun setRoundedCornersRB(@Px corners: Float): DrawableGenerate {
        this.mRoundedCornersRB = corners
        return this
    }

    /**
     * 设置圆角（右下角）
     *
     * @param corners 圆角半径，单位[Px]
     */
    fun setRoundedCornersRB(@Px corners: Int): DrawableGenerate {
        return setRoundedCornersRB(corners.toFloat())
    }

    /**
     * 设置圆角（右下角）
     *
     * @param corners 圆角半径，单位[Px]
     */
    fun setRoundedCornersRB(@Px corners: Double): DrawableGenerate {
        return setRoundedCornersRB(corners.toFloat())
    }

    /**
     * 设置圆角（左下角）
     *
     * @param corners 圆角半径，单位[Px]
     */
    fun setRoundedCornersLB(@Px corners: Float): DrawableGenerate {
        this.mRoundedCornersLB = corners
        return this
    }

    /**
     * 设置圆角（左下角）
     *
     * @param corners 圆角半径，单位[Px]
     */
    fun setRoundedCornersLB(@Px corners: Int): DrawableGenerate {
        return setRoundedCornersLB(corners.toFloat())
    }

    /**
     * 设置圆角（左下角）
     *
     * @param corners 圆角半径，单位[Px]
     */
    fun setRoundedCornersLB(@Px corners: Double): DrawableGenerate {
        return setRoundedCornersLB(corners.toFloat())
    }

    /**
     * 设置透明度
     *
     * @param alpha 0-255范围内的一个透明值
     */
    fun setAlpha(@IntRange(from = 0, to = 255) alpha: Int): DrawableGenerate {
        this.mAlpha = alpha
        return this
    }

    /**
     * 设置透明度
     *
     * @param alpha 0.0-1.0范围内的一个透明度
     */
    fun setAlpha(@FloatRange(from = 0.0, to = 1.0) alpha: Float): DrawableGenerate {
        return setAlpha((alpha * 255).toInt())
    }

    /**
     * 设置透明度
     *
     * @param alpha 0.0-1.0范围内的一个透明度
     */
    fun setAlpha(@FloatRange(from = 0.0, to = 1.0) alpha: Double): DrawableGenerate {
        return setAlpha((alpha * 255).toInt())
    }

    /**
     * 设置渐变色，最多接收三个颜色
     *
     * 颜色大于等于三个时按三个颜色计算
     *
     * 以前三个颜色为准
     *
     * 三个颜色时 => 依次为 开始 居中 结束
     *
     * 两个颜色时 => 依次为 开始 结束
     *
     * 一个颜色时 => 直接调用设置为背景颜色
     *
     * @param colors Int颜色值
     */
    fun setGradientColor(@ColorInt vararg colors: Int): DrawableGenerate {
        val count = colors.size
        when {
            count >= 3 -> {
                this.mStartColor = colors[0]
                this.mCenterColor = colors[1]
                this.mEndColor = colors[2]
            }
            count >= 2 -> {
                this.mStartColor = colors[0]
                this.mEndColor = colors[1]
            }
            count >= 1 -> {
                this.mBackgroundColor = colors[0]
            }
            else -> {
            }
        }
        return this
    }

    /**
     * 设置渐变色，最多接收三个颜色
     *
     * 颜色大于等于三个时按三个颜色计算
     *
     * 以前三个颜色为准
     *
     * 三个颜色时 => 依次为 开始 居中 结束
     *
     * 两个颜色时 => 依次为 开始 结束
     *
     * 一个颜色时 => 直接调用设置为背景颜色
     *
     * @param resIds 颜色资源ID
     */
    fun setGradientColorRes(@ColorRes vararg resIds: Int): DrawableGenerate {
        val colors = mutableListOf<Int>()
        resIds.forEach {
            colors.add(getColorByRes(it))
        }
        val count = colors.size
        return when {
            count >= 3 -> setGradientColor(colors[0], colors[1], colors[2])
            count >= 2 -> setGradientColor(colors[0], colors[1])
            count >= 1 -> setGradientColor(colors[0])
            else -> this
        }
    }

    /**
     * 设置渐变色，最多接收三个颜色
     *
     * 颜色大于等于三个时按三个颜色计算
     *
     * 以前三个颜色为准
     *
     * 三个颜色时 => 依次为 开始 居中 结束
     *
     * 两个颜色时 => 依次为 开始 结束
     *
     * 一个颜色时 => 直接调用设置为背景颜色
     *
     * @param colorStrings 颜色字符串
     */
    fun setGradientColorStr(vararg colorStrings: String): DrawableGenerate {
        val colors = mutableListOf<Int>()
        colorStrings.forEach {
            colors.add(getColorByStr(it))
        }
        val count = colors.size
        return when {
            count >= 3 -> setGradientColor(colors[0], colors[1], colors[2])
            count >= 2 -> setGradientColor(colors[0], colors[1])
            count >= 1 -> setGradientColor(colors[0])
            else -> this
        }
    }

    /**
     * 设置渐变类型
     *
     * @see GradientDrawable.LINEAR_GRADIENT 渐变是线性的
     * @see GradientDrawable.RADIAL_GRADIENT 渐变是循环的
     * @see GradientDrawable.SWEEP_GRADIENT 渐变是扫视的
     */
    @JvmOverloads
    fun setGradientType(gradientType: Int = GradientDrawable.LINEAR_GRADIENT): DrawableGenerate {
        this.mGradientType = gradientType
        return this
    }

    /**
     * 设置渐变方向
     *
     * @see GradientDrawable.Orientation 枚举
     */
    @JvmOverloads
    fun setGradientOrientation(gradientOrientation: GradientDrawable.Orientation = GradientDrawable.Orientation.LEFT_RIGHT): DrawableGenerate {
        this.mGradientOrientation = gradientOrientation
        return this
    }

    /**
     * 設置渐变的形状类型
     *
     * @see GradientDrawable.RECTANGLE 形状是一个矩形，可能有圆角
     * @see GradientDrawable.OVAL 形状是一个椭圆
     * @see GradientDrawable.LINE 形状是一条线
     * @see GradientDrawable.RING 形状是一个环
     */
    fun setShape(shape: Int = GradientDrawable.RECTANGLE): DrawableGenerate {
        this.mShape = shape
        return this
    }

    /**
     * 必须必须调用此方法，最终生成一个[GradientDrawable]
     *
     * @return 返回[GradientDrawable]
     */
    fun build(): Drawable {
        val drawable = GradientDrawable()
        // 设置渐变的形状类型
        drawable.shape = mShape
        if (null == mStartColor && null == mEndColor) {
            // 设置背景颜色
            drawable.setColor(mBackgroundColor)
        } else {
            // 设置渐变色
            val colors = mutableListOf<Int>()
            mStartColor?.let { colors.add(it) }
            mCenterColor?.let { colors.add(it) }
            mEndColor?.let { colors.add(it) }
            // 默认不偏移，均匀渐变
            drawable.colors = colors.toIntArray()
            // 设置渐变类型
            drawable.gradientType = mGradientType
            // 设置渐变角度
            drawable.orientation = mGradientOrientation
        }
        // 设置透明度
        drawable.alpha = mAlpha
        // 设置圆角
        drawable.cornerRadii = mutableListOf(
            mRoundedCornersLT, mRoundedCornersLT, // 左上角X、Y
            mRoundedCornersRT, mRoundedCornersRT, // 右上角X、Y
            mRoundedCornersRB, mRoundedCornersRB, // 右下角X、Y
            mRoundedCornersLB, mRoundedCornersLB, // 左下角X、Y
        ).toFloatArray()
        // 设置边框
        if (mBorderWidth > 0.0f) {
            drawable.setStroke(mBorderWidth, mBorderColor)
        }
        // 附加drawable到指定View
        mView?.get()?.let { view ->
            if (isDrawBg) {
                view.background = drawable
            } else if (view is ImageView) {
                view.setImageDrawable(drawable)
            } else {
                view.background = drawable
            }
            mView = null // 此处使用完成后手动回收
        }
        return drawable
    }

    /**
     * 指定附加的[View]
     *
     * 此处使用弱引用持有防止内存泄露
     *
     * @param view 指定的[View]
     */
    fun with(view: View?): DrawableGenerate {
        view?.also {
            this.mView = WeakReference(it)
        }
        return this
    }

    /**
     * 强制绘制背景
     *
     * @param isDrawBg true为是
     */
    fun isDrawBackground(isDrawBg: Boolean): DrawableGenerate {
        this.isDrawBg = isDrawBg
        return this
    }

    /**
     * 指定附加的[View]
     */
    private var mView: WeakReference<View>? = null

    /**
     * 强制绘制背景（默认不强制绘制背景）
     */
    private var isDrawBg = false
}