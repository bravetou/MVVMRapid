package com.brave.mvvmrapid.core.common

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import androidx.annotation.LayoutRes
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModelProvider
import java.lang.reflect.ParameterizedType

/**
 * ***author***     ：brave tou
 *
 * ***blog***       ：https://blog.csdn.net/bravetou
 *
 * ***time***       ：2022/9/8 18:11
 *
 * ***desc***       ：Activity常用类
 */
@Suppress(
    "UNNECESSARY_SAFE_CALL",
    "UNCHECKED_CAST",
    "UNNECESSARY_NOT_NULL_ASSERTION",
    "MemberVisibilityCanBePrivate",
    "unused"
)
abstract class CommonActivity<V : ViewDataBinding, VM : CommonViewModel>
    : AppCompatActivity(), ICommonView {
    // binding
    protected lateinit var binding: V

    // viewModel
    protected lateinit var viewModel: VM

    /**
     * 初始化[viewModel]的id
     */
    protected abstract val variableId: Int

    protected val context: Context by lazy { this }

    protected val activity: FragmentActivity by lazy { this }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // 页面接受的参数方法
        initParam()
        // 初始化系统栏
        initSystemBar()
        // 私有的初始化DataBinding和ViewModel方法
        initViewDataBinding()
        // 初始化View
        initView()
        // 页面数据初始化方法
        initData()
        // 页面事件监听的方法，一般用于ViewModel层转到View层的事件注册
        initObserver()
    }

    override fun onDestroy() {
        super.onDestroy()
        binding?.unbind()
    }

    /**
     * 注入绑定
     */
    private fun initViewDataBinding() {
        val layoutId = initLayoutId()
        binding = if (null == layoutId) {
            val type = javaClass.genericSuperclass
            if (type is ParameterizedType) {
                val cls = type.actualTypeArguments[0] as Class<*>
                initViewBinding(cls)
            } else {
                throw RuntimeException("You must assign a value to [V] generics")
            }
        } else {
            // DataBindingUtil类需要在project的build中配置 dataBinding {enabled true }
            // 同步后会自动关联android.databinding包
            DataBindingUtil.setContentView(this, layoutId)
        }
        val vm = initViewModel()
        viewModel = if (null == vm) {
            val type = javaClass.genericSuperclass
            val modelClass: Class<*> = if (type is ParameterizedType) {
                type.actualTypeArguments[1] as Class<*>
            } else {
                // 如果没有指定泛型参数
                // 则默认使用[CommonViewModel]
                initDefaultViewModelClass()
            }
            // 带key创建会让使用相同Activity或者Fragment，创建的ViewModel数据独立
            ViewModelProvider(this)[viewModelKey, modelClass as Class<VM>]
        } else {
            vm
        }
        // 关联ViewModel
        binding?.setVariable(variableId, this.viewModel)
        // 支持LiveData绑定xml，数据改变，UI自动会更新
        binding?.lifecycleOwner = this
        // 让ViewModel拥有View的生命周期感应
        this.viewModel?.let { lifecycle?.addObserver(it) }
        // 弃用
        // 注入[LifecycleOwner]生命周期
        // this.viewModel?.injectLifecycleOwner(this)
    }

    /**
     * 通过反射初始化当前泛型类[V]的binding对象
     */
    protected fun initViewBinding(cls: Class<*>): V {
        val method = cls.getDeclaredMethod("inflate", LayoutInflater::class.java)
        val invoke = method.invoke(null, activity.layoutInflater) as V
        invoke?.root?.let { setContentView(it) }
        return invoke
    }

    /**
     * 初始化一个默认的继承至[VM]Class类
     */
    protected fun initDefaultViewModelClass(): Class<in VM> {
        return CommonViewModel::class.java
    }

    /**
     * ViewModel密匙
     */
    protected val viewModelKey: String
        get() {
            return "taskId$taskId"
        }

    /**
     * 初始化根布局
     * @return 布局layout的id
     */
    @LayoutRes
    protected fun initLayoutId(): Int? {
        return null
    }

    /**
     * 初始化ViewModel
     * @return 继承[CommonViewModel]的ViewModel
     */
    protected fun initViewModel(): VM? {
        return null
    }

    override fun initParam() {}

    override fun initSystemBar() {}

    /**
     * 刷新布局
     */
    fun refreshLayout() {
        binding?.setVariable(variableId, viewModel)
    }

    /**
     * 跳转页面
     * @param clz 所跳转的目的Activity类
     * @param bundle 参数
     */
    @JvmOverloads
    fun <AC : Activity> startActivity(clz: Class<in AC>, bundle: Bundle? = null) {
        Intent(this, clz).also {
            bundle?.let { data -> it.putExtras(data) }
            startActivity(it)
        }
    }

    /**
     * 获取页面传递参数，使用[CommonActivity.startActivity]方法进入的
     * @param T 泛型参数类型
     * @param key 对应key
     * @param default 默认值
     * @return T 参数类型
     */
    @JvmOverloads
    fun <T : Any> getParam(key: String, default: T? = null): T? {
        val bundle = intent?.extras ?: return null
        val param = bundle.get(key)
        return if (null != default) {
            if (null == param) default
            else param as T
        } else {
            if (null == param) null
            else param as T
        }
    }
}