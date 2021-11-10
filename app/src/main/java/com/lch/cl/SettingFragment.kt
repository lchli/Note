
package com.lch.cl

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.lch.cl.ad.InterAdUtil
import com.lch.cl.util.ActivityScopeStore
import com.lch.cln.databinding.FragmentSearchBinding

class SettingFragment : Fragment() {
    private lateinit var binding: FragmentSearchBinding
    private val vm: SettingVm by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
         binding = FragmentSearchBinding.inflate(inflater, container, false).apply {

        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.lifecycleOwner=viewLifecycleOwner
        binding.state=vm

    }

    override fun onResume() {
        super.onResume()

        val mInterAdUtil= ActivityScopeStore.of(MainActivity::class.java)[InterAdUtil::class.java]
        (mInterAdUtil as? InterAdUtil)?.show(requireActivity())
    }
}
