package com.brave.mvvmrapid.core.filter

import android.text.InputFilter
import android.text.Spanned
import com.blankj.utilcode.util.ToastUtils
import java.util.regex.Pattern

/**
 * ***author***     ：brave tou
 *
 * ***blog***       ：https://blog.csdn.net/bravetou
 *
 * ***time***       ：2022/9/9 15:30
 *
 * ***desc***       ：常用输入过滤器
 *
 * @param regex 需要过滤的字符串，正则表达式
 * @param hint 可选参数是否弹出提示语句
 * @param keep 是否需要保留除开正则过滤以外的部分
 */
class CommonInputFilter private constructor(
    private val regex: String,
    private val hint: String? = null,
    private val keep: Boolean = false,
) : InputFilter {
    /**
     * 常用正则过滤器
     * @param source 新输入的字符串
     * @param start 新输入的字符串起始下标，一般为0
     * @param end 新输入的字符串终点下标，一般为source长度-1
     * @param dest 输入之前文本框内容
     * @param dstart 原内容起始坐标，一般为0
     * @param dend 原内容终点坐标，一般为dest长度-1
     * @return 输入内容
     */
    override fun filter(
        source: CharSequence?,
        start: Int,
        end: Int,
        dest: Spanned?,
        dstart: Int,
        dend: Int,
    ): CharSequence? {
        val compile = Pattern.compile(regex)
        val matcher = compile.matcher(source.toString())
        // 如果找到匹配该正则表达式的内容
        if (matcher.find()) {
            // 如果提示语不为空则弹出
            if (!hint.isNullOrEmpty()) {
                ToastUtils.showLong(hint)
            }
            // 如果需要保留正则以外的字符串
            if (keep) {
                // 用空字符串("")去替换所有正则匹配部分
                return matcher.replaceAll("")
            }
            return "" // 否则拦截所有字符串输入内容
        }
        return null // 不拦截输入内容
    }

    companion object {
        /**
         * 创建新实例
         *
         * @param regex 需要过滤的字符串，正则表达式
         * @param hint 可选参数是否弹出提示语句
         * @param keep 是否需要保留除开正则过滤以外的部分
         */
        @JvmStatic
        @JvmOverloads
        fun newInstance(
            regex: String,
            hint: String? = null,
            keep: Boolean = false,
        ): CommonInputFilter {
            return CommonInputFilter(regex, hint, keep)
        }
    }
}