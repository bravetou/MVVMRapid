package com.brave.mvvmrapid.core.filter

import android.text.InputFilter
import android.text.Spanned
import android.util.Log
import com.blankj.utilcode.util.RegexUtils
import com.brave.mvvmrapid.core.CommonConfig

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
        if (CommonConfig.DEBUG) {
            // 打印输入前后坐标变换
            Log.d(TAG, "新输入[source] => ($source)[$start, $end]")
            Log.d(TAG, "原文本[dest] => ($dest)[$dstart, $dend]")
            Log.d(TAG, "====================================")
        }
        // 判断输入内容是否为空
        if (source.isNullOrEmpty()) {
            if (CommonConfig.DEBUG) {
                Log.d(TAG, "删除按键")
                Log.d(TAG, "====================================")
            }
            if (!dest.isNullOrEmpty()) {
                val newStr = "${dest.subSequence(0, dstart)}${dest.subSequence(dend, dest.length)}"
                if (CommonConfig.DEBUG) {
                    Log.d(TAG, "新文本[newStr] => ($newStr)")
                    Log.d(TAG, "====================================")
                }
                if (newStr.startsWith("0.")) {
                    return ""
                }
                if (newStr.startsWith("0")) {
                    if (newStr.length > 1) {
                        return dest.subSequence(dstart, dend)
                    }
                }
                if (newStr.startsWith(".")) {
                    return "0"
                }
            }
            return ""
        }
        // 正则判断是否输入为数字或小数点
        val regex = if (keepCount <= 0) {
            "[0-9]"
        } else {
            "[0-9.]"
        }
        if (!RegexUtils.isMatch(regex, source)) {
            return ""
        }
        // 判断光标位置
        when (dstart) {
            // 光标在首位，位置索引为0
            0 -> {
                // 原字符串不为空
                if (!dest.isNullOrEmpty()) {
                    // 新输入的字符串是“0”开始
                    if (source.startsWith("0")) {
                        return ""
                    }
                }
            }
            // 光标在原字符串上的其他位置
            else -> {
                // 如果原字符串是“0”开始
                if (dest?.startsWith("0") == true) {
                    // 原字符串存在小数点
                    if (dest.contains(".")) {
                        // 光标位置与小数点位置索引相同
                        if (dstart == dest.indexOf(".")) {
                            return ""
                        }
                    }
                    // 原字符串不存在小数点
                    else {
                        // 新输入字符串开始位置不是小数点
                        if (!source.startsWith(".")) {
                            return ""
                        }
                    }
                }
            }
        }
        // 判断新输入字符串是否有两次及其以上的小数点出现
        if (source.contains(".") && source.indexOf(".") != source.lastIndexOf(".")) {
            return ""
        }
        // 判断原字符串与新输入字符串是否同时存在小数点
        if (dest?.contains(".") == true && source.contains(".")) {
            return ""
        }
        // 判断小数点是否在第一位
        if (source == "." && dstart == 0 && dend == 0) {
            return "0."
        }
        // 判断小数点是否存在，并且小数点后是否已有keepCount位字符
        val radixPointIndex = dest?.indexOf(".") ?: -1
        if (radixPointIndex != -1 && ((dest?.length ?: 0) - radixPointIndex > keepCount)) {
            // 判断现在输入的字符是不是在小数点后面
            if ((dest?.length ?: 0) - dstart < keepCount + 1) {
                return ""
            }
        }
        return null
    }

    companion object {
        private val TAG = MoneyInputFilter::class.java.simpleName

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