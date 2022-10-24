package com.brave.mvvm.example.ui.fragment.delegate

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.brave.mvvm.example.R
import com.brave.mvvm.example.databinding.FragmentDelegateTemplateBinding
import com.brave.viewbindingdelegate.CreateMethod
import com.brave.viewbindingdelegate.viewBinding

class DelegateTemplateNoReflectionFragment :
    Fragment(R.layout.fragment_delegate_template) {
    companion object {
        const val TAG = "DelegateTemplateNoReflectionFragment"
    }

    private val binding by viewBinding(FragmentDelegateTemplateBinding::bind)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.tvContent.text = "fragment => 无反射"
    }
}

class DelegateTemplateReflectInflateFragment :
    Fragment() {
    companion object {
        const val TAG = "DelegateTemplateReflectInflateFragment"
    }

    private val binding by viewBinding<FragmentDelegateTemplateBinding>(CreateMethod.INFLATE)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = binding.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.tvContent.text = "fragment => 反射 inflate"
    }
}

class DelegateTemplateReflectBindFragment :
    Fragment(R.layout.fragment_delegate_template) {
    companion object {
        const val TAG = "DelegateTemplateReflectBindFragment"
    }

    private val binding: FragmentDelegateTemplateBinding by viewBinding()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.tvContent.text = "fragment => 反射 bind"
    }
}