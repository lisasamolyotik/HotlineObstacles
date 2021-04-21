package com.obstacles.hotline.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.obstacles.hotline.R
import com.obstacles.hotline.databinding.MenuScreenBinding

class MenuFragment : Fragment(R.layout.menu_screen) {
    private var _binding: MenuScreenBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = MenuScreenBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.playButton.setOnClickListener {
            it.findNavController().navigate(R.id.action_menuFragment_to_levelFragment)
        }

        binding.rulesButton.setOnClickListener {
            it.findNavController().navigate(R.id.action_menuFragment_to_gameRulesFragment)
        }

        binding.settingsButton.setOnClickListener {
            it.findNavController().navigate(R.id.action_menuFragment_to_gameSettingsFragment)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}