package com.brave.mvvm.example.bean

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class FunctionBean(
    val id: Int = -1,
    val imgRes: Int = -1,
    val name: String = ""
) : Parcelable