package com.capstone.mypocket

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.capstone.mypocket.databinding.FragmentSavingsBinding

class SavingsFragment : Fragment() {

    private lateinit var binding: FragmentSavingsBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSavingsBinding.inflate(inflater, container, false)
        binding.buttonContainer.setOnClickListener {
            findNavController().navigate(R.id.action_savingsFragment_to_newPageFragment)
        }
        return binding.root
    }
}
