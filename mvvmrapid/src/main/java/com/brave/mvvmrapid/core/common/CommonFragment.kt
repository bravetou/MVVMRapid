@file:Suppress(
    "UNCHECKED_CAST", "unused",
    "MemberVisibilityCanBePrivate"
)

package com.brave.mvvmrapid.core.common

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.viewbinding.ViewBinding
import com.brave.mvvmrapid.utils.BindingHelper
import java.lang.reflect.ParameterizedType

/**
 * ***author***     ：brave tou
 *
 * ***blog***       ：https://blog.csdn.net/bravetou
 *
 * ***time***       ：2022/9/9 14:27
 *
 * ***desc***       ：Fragment常用类
 */
abstract class CommonFragment<Binding : ViewBinding, VM : CommonViewModel>
    : Fragment(), ICommonView {
    // binding
    private lateinit var _binding: Binding
    val binding: Binding
        get() = _binding

    // viewModel
    private lateinit var _viewModel: VM
    val viewModel: VM
        get() = _viewModel

    /**
     * 是数据绑定，即是[ViewDataBinding]
     */
    val isDataBinding: Boolean
        get() = binding is ViewDataBinding

    /**
     * 只能在[isDataBinding]为true时使用，否则抛出异常
     */
    val dataBinding: ViewDataBinding
        get() = if (isDataBinding) {
            binding as ViewDataBinding
        } else {
            throw RuntimeException("[${binding}] is not [${ViewDataBinding::class.java}] or a subclass of [${ViewDataBinding::class.java}]")
        }

    override fun onDestroyView() {
        super.onDestroyView()
        if (isDataBinding) {
            dataBinding.unbind()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        // 类型
        val type = javaClass.genericSuperclass
        // 不是带泛型的参数化类型，则抛出异常
        if (type !is ParameterizedType)
            throw RuntimeException("$this is not a parameterized type with generics")
        // 初始化[Binding]
        _binding = initViewBinding() ?: kotlin.run {
            // [Binding]
            val cls = type.actualTypeArguments[0] as Class<Binding>
            // 反射初始化[Binding]
            BindingHelper.getBindingByClass(
                layoutInflater, cls, binding@{
                    val layoutId = initLayoutId()
                    return@binding if (null != layoutId) {
                        BindingHelper.getBindingByLayoutId(inflater, layoutId, container) as Binding
                    } else null
                })
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initStart()
        initSystemBar()
        // 私有的初始化[CommonViewModel]的方法
        initBinding()
        initGlobalBus()
        initView(savedInstanceState)
        initData()
        // 页面事件监听的方法，一般用于ViewModel层转到View层的事件注册
        initObserver()
        initEnd()
    }

    /**
     * 初始化[CommonViewModel]并与绑定[ViewDataBinding]
     */
    private fun initBinding() {
        // 类型
        val type = javaClass.genericSuperclass
        // 不是带泛型的参数化类型，则抛出异常
        if (type !is ParameterizedType)
            throw RuntimeException("$this is not a parameterized type with generics")
        // 初始化[VM]
        _viewModel = initViewModel() ?: kotlin.run {
            // [VM]
            val cls = type.actualTypeArguments[1] as Class<VM>
            // 使用[ViewModelProvider]初始化[VM]
            ViewModelProvider(this)[viewModelKey, cls]
        }
        // [ViewDataBinding]
        if (isDataBinding) {
            // 关联ViewModel
            dataBinding.setVariable(variableId, viewModel)
            // 支持LiveData绑定xml，数据改变，UI自动会更新
            dataBinding.lifecycleOwner = this
            // 让ViewModel拥有View的生命周期感应
            lifecycle.addObserver(viewModel)
            // 弃用
            // 注入[LifecycleOwner]生命周期
            // this.viewModel?.injectLifecycleOwner(this)
        }
    }

    /**
     * 初始化[viewModel]的id
     */
    protected abstract val variableId: Int

    /**
     * 可选方法
     *
     * 初始化[Binding]
     * @return [Binding]
     */
    protected fun initViewBinding(): Binding? {
        return null
    }

    /**
     * 可选方法
     *
     * 初始化根布局
     * @return 根布局id
     */
    @LayoutRes
    protected fun initLayoutId(): Int? {
        return null
    }

    /**
     * 可选方法
     *
     * 初始化[VM]
     * @return [VM]
     */
    protected fun initViewModel(): VM? {
        return null
    }

    /**
     * ViewModel密匙
     */
    protected val viewModelKey: String
        get() {
            return "taskId$tag"
        }

    /**
     * 刷新布局
     */
    fun refreshLayout() {
        if (isDataBinding) {
            dataBinding.setVariable(variableId, viewModel)
        }
    }

    override fun initStart() {}

    override fun initSystemBar() {}

    override fun initGlobalBus() {}

    override fun initData() {}

    override fun initObserver() {}

    override fun initEnd() {}

    /**
     * 跳转页面
     * @param clz 所跳转的目的Activity类
     * @param bundle 参数
     */
    @JvmOverloads
    fun <AC : Activity> startActivity(clz: Class<in AC>, bundle: Bundle? = null) {
        activity?.apply {
            Intent(this, clz).also {
                bundle?.let { data -> it.putExtras(data) }
                startActivity(it)
            }
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
        activity?.apply {
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
        return null
    }

    /**
     * 获取片段传递参数
     * @param T 泛型参数类型
     * @param key 对应key
     * @param default 默认值
     * @return T 参数类型
     */
    @JvmOverloads
    fun <T : Any> getArgumentsParam(key: String, default: T? = null): T? {
        val bundle = arguments ?: return null
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