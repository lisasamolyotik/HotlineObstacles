package com.obstacles.hotline.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.activity.addCallback
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.navigation.fragment.navArgs
import com.obstacles.hotline.R
import com.obstacles.hotline.databinding.YouWinScreenBinding

class YouWinFragment : Fragment(R.layout.you_win_screen) {
    private var _binding: YouWinScreenBinding? = null
    private val binding get() = _binding!!

    var callback: OnBackPressedCallback? = null
    val args: LevelFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = YouWinScreenBinding.inflate(inflater, container, false)


        callback = requireActivity().onBackPressedDispatcher.addCallback(this) {
            binding.root.findNavController().navigate(R.id.action_youWinFragment_to_menuFragment)
        }

        binding.optionsButton.setOnClickListener {
            it.findNavController().navigate(R.id.action_youWinFragment_to_gameSettingsFragment)
        }

        val level = args.level

        binding.retryButton.setOnClickListener {
            val action = YouWinFragmentDirections.actionYouWinFragmentToLevelFragment(level)
            it.findNavController().navigate(action)
        }
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}