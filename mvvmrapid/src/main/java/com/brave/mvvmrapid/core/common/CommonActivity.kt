package com.brave.mvvmrapid.core.common

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModelProvider
import androidx.viewbinding.ViewBinding
import com.brave.mvvmrapid.core.CommonConfig
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
abstract class CommonActivity<Binding : ViewBinding, VM : CommonViewModel> : AppCompatActivity(),
    ICommonView {
    private val _tag by lazy { this::class.java.simpleName }

    private val _helper by lazy { GenericsHelper(javaClass) }

    protected open val binding: Binding by activityViewBinding(onViewDestroyed = {
        if (it is ViewDataBinding) it.unbind()
    }, viewBinder = { _ ->
        initViewBinding() ?: _helper.find<ViewBinding, Binding>() //
            ?.inflate(layoutInflater) //
        ?: error("Generic <Binding> not found")
    }, viewNeedsInitialization = false)

    protected open fun initViewBinding(): Binding? = null

    protected open val viewModel: VM by lazy {
        initViewModel() ?: _helper.find<CommonViewModel, VM>() //
            ?.let { ViewModelProvider(this)[viewModelKey, it] } //
        ?: error("Generic <VM> not found")
    }

    protected open fun initViewModel(): VM? = null

    protected val context: Context by lazy { this }

    protected val activity: FragmentActivity by lazy { this }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initStart()
        initBinding()
        // 此处考虑到与immersionBar一起使用时需要使用布局文件中的View，所以与initBinding交换位置
        initSystemBar()
        updateDefaultUI()
        initGlobalBus()
        initView(savedInstanceState)
        initData()
        // 页面事件监听的方法，一般用于ViewModel层转到View层的事件注册
        initObserver()
        initEnd()
    }

    protected open fun initBinding() {
        // 设置布局
        setContentView(getRootView())
        binding.let { binding ->
            // [ViewDataBinding]
            if (binding is ViewDataBinding) {
                // 关联ViewModel
                binding.setVariable(variableId, viewModel)
                // 支持LiveData绑定xml，数据改变，UI自动会更新
                binding.lifecycleOwner = this
            }
            // 让ViewModel拥有View的生命周期感应
            lifecycle.addObserver(viewModel)
        }
    }

    /**
     * 获取根布局
     * @return 根布局，默认返回[Binding]的[root][ViewBinding.getRoot]
     */
    protected open fun getRootView(): View = binding.root

    /**
     * 初始化[viewModel]的id
     */
    protected abstract val variableId: Int

    /**
     * ViewModel密匙
     */
    protected open val viewModelKey: String = "taskId_${_tag}_$taskId"

    /**
     * 刷新布局
     */
    protected open fun refreshLayout() {
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

    protected open fun onStart(text: String) {}
    protected open fun onComplete() {}
    protected open fun onError(e: Throwable) {}

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
     * 跳转页面
     * @param launcher 可用于启动活动或处理已准备调用的启动程序
     * @param bundle 参数
     */
    @JvmOverloads
    inline fun <reified AC : Activity> startActivityForResult(
        launcher: ActivityResultLauncher<Intent>,
        bundle: Bundle = bundleOf(),
    ) {
        launcher.launch(Intent(this, AC::class.java).also { intent ->
            intent.putExtras(bundle)
        })
    }

    override fun onDestroy() {
        // 销毁时取消所有回调函数的持有
        _callbacks.clear()
        _launcher.unregister()
        // 解绑ViewDataBinding
        binding.also { binding -> if (binding is ViewDataBinding) binding.unbind() }
        super.onDestroy()
    }

    /**
     * 该[方法][startActivityForResult]需与[finishForResult]联用
     * @param callback 回调函数，若重写[onActivityResult]方法后，未回调super，则无法使用
     */
    @JvmOverloads
    fun <AC : Activity> startActivityForResult(
        cls: Class<AC>,
        bundle: Bundle = bundleOf(),
        requestCode: Int? = null,
        callback: (Bundle) -> Unit = {},
    ) {
        val code = requestCode ?: cls.hashCode()
        _callbacks[code] = callback
        _launcher.launch(Intent(this, cls).also { intent ->
            bundle.putInt(_requestKey, code)
            intent.putExtras(bundle)
        })
    }

    /**
     * 该[方法][finishForResult]可与[startActivityForResult]联用
     */
    @JvmOverloads
    fun finishForResult(resultCode: Int = RESULT_OK, data: Bundle = bundleOf()) {
        val intent = Intent()
        data.putInt(_requestKey, _requestCode)
        intent.putExtras(data)
        setResult(resultCode, intent)
        finish()
    }

    /**
     * 请求码
     */
    private val _requestCode by lazy { intent?.extras?.getInt(_requestKey, -1) ?: -1 }
    private val _requestKey by lazy { CommonConfig.REQUEST_CODE }
    private val _callbacks = linkedMapOf<Int, (Bundle) -> Unit>()

    /**
     * 带返回结果的启动程序的通用协议
     */
    private val _contract = ActivityResultContracts.StartActivityForResult()

    /**
     * 可用于启动活动或处理已准备调用的启动程序
     */
    private val _launcher = registerForActivityResult(_contract, ::onActivityResult)

    /**
     * 启动程序返回之后的结果回调
     */
    open fun onActivityResult(result: ActivityResult?) {
        result ?: return
        val bundle = result.data?.extras ?: return
        _callbacks.filter { (requestCode, _) ->
            requestCode == bundle.getInt(_requestKey)
        }.forEach {
            it.value(bundle)
        }
    }
}