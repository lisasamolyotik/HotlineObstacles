package com.obstacles.hotline.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.activity.addCallback
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.obstacles.hotline.R
import com.obstacles.hotline.databinding.TryAgainScreenBinding

class TryAgainFragment : Fragment(R.layout.try_again_screen) {
    private var _binding: TryAgainScreenBinding? = null
    private val binding get() = _binding!!

    var callback: OnBackPressedCallback? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = TryAgainScreenBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.retryButton.setOnClickListener {
            val action = TryAgainFragmentDirections.actionTryAgainFragmentToLevelFragment(1)
            it.findNavController().navigate(action)
        }

        binding.optionsButton.setOnClickListener {
            it.findNavController().navigate(R.id.action_tryAgainFragment_to_gameSettingsFragment)
        }

        callback = requireActivity().onBackPressedDispatcher.addCallback(this) {
            binding.root.findNavController().navigate(R.id.action_tryAgainFragment_to_menuFragment)
            isEnabled = true
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        callback?.isEnabled = false
        _binding = null
    }
}