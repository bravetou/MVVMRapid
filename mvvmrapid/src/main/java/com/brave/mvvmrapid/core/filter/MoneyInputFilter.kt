package com.brave.mvvmrapid.core.filter

import android.text.InputFilter
import android.text.Spanned
import com.blankj.utilcode.util.RegexUtils

/**
 * ***author*** ：brave tou
 *
 * ***data***   : 2021/9/28 16:08
 *
 * ***desc***   : 金额输入过滤器
 *
 * @param keepCount 需要保留的小数位数，默认保留两位小数
 */
@Suppress("unused")
class MoneyInputFilter private constructor(private val keepCount: Int = 2) : InputFilter {
    /**
     * 金额过滤器
     * @param source 新输入的字符串
     * @param start 新输入的字符串起始下标，一般为0
     * @param end 新输入的字符串终点下标，一般为source长度-1
     * @param dest 输入之前文本框内容
     * @param dstart 原内容起始坐标，一般为0
     * @param dend 原内容终点坐标，一般为dest长度-1
     *
     * @return 输入内容
     */
    override fun filter(
        source: CharSequence?,
        start: Int,
        end: Int,
        dest: Spanned?,
        dstart: Int,
        dend: Int
    ): CharSequence? {
        // 打印输入前后坐标变换
        // Log.d("MoneyInputFilter", "filter: => $start => $end => $dstart => $dend")
        // 判断输入内容是否为空
        if (source.isNullOrEmpty()) {
            return ""
        }
        // 正则判断是否输入为数字或小数点
        if (!RegexUtils.isMatch("[0-9.]", source)) {
            return ""
        }
        // 如果首位为0
        if (dest.toString().startsWith("0")) {
            // 原字符串存在小数点
            if (dest.toString().contains(".")) {
                // 输入位置在0之后
                if (dstart == 1) {
                    return ""
                }
            }
            // 原字符串不存在小数点
            else {
                // 新输入字符串开始位置不是小数点
                if (!source.toString().startsWith(".")) {
                    return ""
                }
            }
        }
        // 判断小数点是否在第一位
        if (source == "." && dstart == 0 && dend == 0) {
            return "0."
        }
        // 判断小数点是否存在，并且小数点后是否已有keepCount位字符
        val radixPointIndex = dest?.toString()?.indexOf(".") ?: -1
        if (radixPointIndex != -1 && ((dest?.length ?: 0) - radixPointIndex > keepCount)) {
            // 判断现在输入的字符是不是在小数点后面
            if ((dest?.length ?: 0) - dstart < keepCount + 1) {
                return ""
            }
        }
        return null
    }

    companion object {
        /**
         * 创建新实例
         */
        @JvmStatic
        @JvmOverloads
        fun newInstance(keepCount: Int = 2): MoneyInputFilter {
            return MoneyInputFilter(keepCount)
        }
    }
}