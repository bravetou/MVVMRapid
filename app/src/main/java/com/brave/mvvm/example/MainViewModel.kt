package com.brave.mvvm.example

import android.app.Application
import android.view.View
import androidx.lifecycle.MutableLiveData
import com.brave.mvvmrapid.core.common.CommonViewModel

/**
 * ***author***     ：brave tou
 *
 * ***blog***       ：https://blog.csdn.net/bravetou
 *
 * ***time***       ：2022/9/23 15:43
 *
 * ***desc***       ：Main ViewModel
 */
class MainViewModel(application: Application) : CommonViewModel(application) {
    val helloWorld = MutableLiveData("hello world!")
    val helloVisibility = MutableLiveData(View.VISIBLE)
}