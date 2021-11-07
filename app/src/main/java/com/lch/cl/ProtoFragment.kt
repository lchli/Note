package com.lch.cl

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.materialstudies.owl.R
import com.materialstudies.owl.databinding.ProtoFragmentBinding
import com.materialstudies.owl.util.transition.MaterialContainerTransition

class ProtoFragment: Fragment() {

    private lateinit var binding: ProtoFragmentBinding
    private val args: ProtoFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = ProtoFragmentBinding.inflate(inflater, container, false).apply {

        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.lifecycleOwner=viewLifecycleOwner
        binding.title.text=args.title
        binding.protoContent.text=args.content
        binding.back.setOnClickListener {
            findNavController().navigateUp()
        }


    }
}