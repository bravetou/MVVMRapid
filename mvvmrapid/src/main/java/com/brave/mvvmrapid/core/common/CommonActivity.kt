package com.brave.mvvmrapid.core.common

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModelProvider
import androidx.viewbinding.ViewBinding
import com.brave.mvvmrapid.core.CommonConfig
import com.brave.mvvmrapid.utils.inflate
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
@Suppress("UNCHECKED_CAST", "REDUNDANT_MODIFIER", "SortModifiers")
abstract class CommonActivity<Binding : ViewBinding, VM : CommonViewModel>
    : AppCompatActivity(), ICommonView {
    @Suppress("PrivatePropertyName")
    private val TAG by lazy { this::class.java.simpleName }

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
     * 特别注意[Activity.getLayoutInflater]可能为空，
     * 所以需要使用 `by lazy` 来重写此方法。
     */
    open val binding: Binding by lazy {
        _binding ?: throw NullPointerException(
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
        _viewModel ?: throw NullPointerException(
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

    protected val context: Context by lazy { this }

    protected val activity: FragmentActivity by lazy { this }

    override fun onDestroy() {
        super.onDestroy()
        if (isDataBinding) {
            dataBinding.unbind()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initStart()
        initSystemBar()
        // 私有的初始化[ViewBinding]和[CommonViewModel]的方法
        initBinding()
        updateDefaultUI()
        initGlobalBus()
        initView(savedInstanceState)
        initData()
        // 页面事件监听的方法，一般用于ViewModel层转到View层的事件注册
        initObserver()
        initEnd()
    }

    /**
     * 初始化[ViewBinding]和[CommonViewModel]
     */
    private fun initBinding() {
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
                // 循环当前泛型
                type.actualTypeArguments.forEach { argument ->
                    argument?.let { initBindingOrViewModel(it as? Class<*>?) }
                }
            }
        }
        // 设置布局
        setContentView(binding.root)
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
    private fun initBindingOrViewModel(clazz: Class<*>?) {
        if (null == clazz) return
        when {
            ViewBinding::class.java.isAssignableFrom(clazz) -> {
                // 初始化[Binding]
                _binding = clazz.inflate(layoutInflater)
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
            return "taskId$taskId"
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
     * 默认UI更新
     */
    private fun updateDefaultUI() {
        viewModel.defUI.onStart.observe(this) { text -> onStart(text ?: "") }
        viewModel.defUI.onComplete.observe(this) { onComplete() }
        viewModel.defUI.onError.observe(this) { e -> onError(e) }
    }

    open fun onStart(text: String) {}
    open fun onComplete() {}
    open fun onError(e: Throwable) {}

    /**
     * 跳转页面
     * @param clz 所跳转的目的Activity类
     * @param bundle 参数
     */
    @JvmOverloads
    open fun <AC : Activity> startActivity(clz: Class<in AC>, bundle: Bundle? = null) {
        val intent = Intent(this, clz)
        bundle?.let { data -> intent.putExtras(data) }
        startActivity(intent)
    }

    /**
     * 获取页面传递参数，使用[CommonActivity.startActivity]方法进入的
     * @param T 泛型参数类型
     * @param key 对应key
     * @param default 默认值
     * @return T 参数类型
     */
    @JvmOverloads
    open fun <T : Any> getParam(key: String, default: T? = null): T? {
        val bundle = intent?.extras ?: return null
        return (bundle.get(key) as? T?) ?: default
    }
}