package com.click.retina

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.click.retina.databinding.NetworkErrorBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class RetryBottomSheetFragment : BottomSheetDialogFragment() {

    private var _binding: NetworkErrorBinding? = null
    private val binding get() = _binding!!

    var retryAction: (() -> Unit)? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = NetworkErrorBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.retryButton.setOnClickListener {
            retryAction?.invoke()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
