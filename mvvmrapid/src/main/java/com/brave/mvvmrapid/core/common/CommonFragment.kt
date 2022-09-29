package com.brave.mvvmrapid.core.common

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.viewbinding.ViewBinding
import com.brave.mvvmrapid.core.CommonConfig
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
@Suppress(
    "UNCHECKED_CAST",
    "REDUNDANT_MODIFIER",
    "SortModifiers"
)
abstract class CommonFragment<Binding : ViewBinding, VM : CommonViewModel>
    : Fragment(), ICommonView {
    companion object {
        private val TAG = CommonFragment::class.java.simpleName
    }

    /**
     * 是否使用[Class]实现（默认使用）
     *
     * 如您将其修改为`false`，
     * 则您将必须实现[binding]与[viewModel]方法
     */
    open val isImplUsingClass: Boolean
        get() = true

    // binding
    private var _binding: Binding? = null

    /**
     * 可重写此方法实现您的[ViewBinding]
     *
     * 如未重写此方法，此处会通过[Binding]来反射实现，
     * 注意此处必须是直接传递泛型。
     *
     * 特别注意[Fragment.getLayoutInflater]可能为空，
     * 所以需要使用 `by lazy` 来重写此方法。
     */
    open val binding: Binding by lazy {
        _binding ?: throw RuntimeException(
            "The [binding] method cannot be empty. You can either use the [Binding] generic parameter or re-implement the [binding] method"
        )
    }

    // viewModel
    private var _viewModel: VM? = null

    /**
     * 可重写此方法实现您的[CommonViewModel]
     *
     * 如未重写此方法，此处会通过[ViewModelProvider]来实现,
     * 注意此处必须是直接传递泛型。
     */
    open val viewModel: VM by lazy {
        _viewModel ?: throw RuntimeException(
            "The [viewModel] method cannot be empty, you can use the [VM] generic parameter, or you can re-implement the [viewModel] method"
        )
    }

    /**
     * 是数据绑定，即是[ViewDataBinding]
     */
    private val isDataBinding: Boolean
        get() = binding is ViewDataBinding

    /**
     * 只能在[isDataBinding]为true时使用，否则抛出异常
     */
    private val dataBinding: ViewDataBinding
        get() = if (isDataBinding) {
            binding as ViewDataBinding
        } else {
            throw RuntimeException("[binding] is not [${ViewDataBinding::class.java}] or a subclass of [${ViewDataBinding::class.java}]")
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
        if (isImplUsingClass) {
            // 类型
            val type = javaClass.genericSuperclass
            // 调试当前的泛型的参数化类型
            if (CommonConfig.DEBUG) {
                Log.d(TAG, "type => $type")
                Log.d(TAG, "====================================")
            }
            // 是带泛型的参数化类型
            if (type is ParameterizedType) {
                // 泛型参数类型数组长度
                val arguments = type.actualTypeArguments ?: arrayOf()
                // 如果存在一个及其以上的泛型则取出第一个
                if (arguments.isNotEmpty()) {
                    initBindingOrViewModel(arguments[0] as? Class<*>?, inflater)
                }
                // 如果存在两个及其以上的泛型则取出第二个
                if (arguments.size > 1) {
                    initBindingOrViewModel(arguments[1] as? Class<*>?, inflater)
                }
            }
        }
        // 设置布局
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
     * 初始化[ViewDataBinding]与[CommonViewModel]的绑定
     */
    private fun initBinding() {
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
     * 初始化[_binding]或者[_viewModel]
     */
    private fun initBindingOrViewModel(clazz: Class<*>?, inflater: LayoutInflater) {
        if (null == clazz) return
        when {
            ViewBinding::class.java.isAssignableFrom(clazz) -> {
                // 初始化[Binding]
                _binding = clazz.inflate(inflater)
            }
            CommonViewModel::class.java.isAssignableFrom(clazz) -> {
                // 初始化[VM]
                val cls = clazz as Class<VM>
                // 使用[ViewModelProvider]初始化[VM]
                _viewModel = ViewModelProvider(this)[viewModelKey, cls]
            }
        }
    }

    /**
     * 初始化[viewModel]的id
     */
    open protected abstract val variableId: Int

    /**
     * ViewModel密匙
     */
    open protected val viewModelKey: String
        get() {
            return "taskId$tag"
        }

    /**
     * 刷新布局
     */
    open fun refreshLayout() {
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