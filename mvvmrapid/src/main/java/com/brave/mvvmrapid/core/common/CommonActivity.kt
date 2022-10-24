package com.brave.mvvmrapid.core.common

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModelProvider
import androidx.viewbinding.ViewBinding
import com.brave.mvvmrapid.utils.GenericsHelper
import com.brave.mvvmrapid.utils.inflate
import com.brave.viewbindingdelegate.activityViewBinding

/**
 * ***author***     ：brave tou
 *
 * ***blog***       ：https://blog.csdn.net/bravetou
 *
 * ***time***       ：2022/9/8 18:11
 *
 * ***desc***       ：Activity常用类
 */
@Suppress("UNCHECKED_CAST", "REDUNDANT_MODIFIER", "SortModifiers", "PrivatePropertyName")
abstract class CommonActivity<Binding : ViewBinding, VM : CommonViewModel>
    : AppCompatActivity(), ICommonView {
    private val TAG by lazy { this::class.java.simpleName }

    private val allGenerics by lazy { GenericsHelper(javaClass).classes }

    open val binding: Binding by activityViewBinding(onViewDestroyed = {
        if (it is ViewDataBinding) it.unbind()
    }, viewBinder = { _ ->
        initViewBinding() ?: allGenerics.filterIsInstance<Class<Binding>>()
            .find { ViewBinding::class.java.isAssignableFrom(it) }
            ?.inflate(layoutInflater)
        ?: error("Generic <Binding> not found")
    }, viewNeedsInitialization = false)

    open fun initViewBinding(): Binding? = null

    open val viewModel: VM by lazy {
        initViewModel() ?: allGenerics
            .filterIsInstance<Class<VM>>()
            .find { CommonViewModel::class.java.isAssignableFrom(it) }
            ?.let { ViewModelProvider(this)[viewModelKey, it] }
        ?: error("Generic <VM> not found")
    }

    open fun initViewModel(): VM? = null

    protected val context: Context by lazy { this }

    protected val activity: FragmentActivity by lazy { this }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initStart()
        initSystemBar()
        initBinding()
        updateDefaultUI()
        initGlobalBus()
        initView(savedInstanceState)
        initData()
        // 页面事件监听的方法，一般用于ViewModel层转到View层的事件注册
        initObserver()
        initEnd()
    }

    private fun initBinding() {
        binding.let { binding ->
            // 设置布局
            setContentView(binding.root)
            // [ViewDataBinding]
            if (binding is ViewDataBinding) {
                // 关联ViewModel
                binding.setVariable(variableId, viewModel)
                // 支持LiveData绑定xml，数据改变，UI自动会更新
                binding.lifecycleOwner = this
                // 让ViewModel拥有View的生命周期感应
                lifecycle.addObserver(viewModel)
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
    open protected val viewModelKey: String = "taskId_${TAG}_$taskId"

    /**
     * 刷新布局
     */
    open fun refreshLayout() {
        binding.let { binding ->
            if (binding is ViewDataBinding) {
                binding.setVariable(variableId, viewModel)
            }
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
     * @param bundle 参数
     */
    @JvmOverloads
    inline fun <reified AC : Activity> startActivity(bundle: Bundle? = null) {
        val intent = Intent(this, AC::class.java)
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
    fun <T : Any> getParam(key: String, default: T? = null): T? {
        val bundle = intent?.extras ?: return null
        return (bundle.get(key) as? T?) ?: default
    }
}