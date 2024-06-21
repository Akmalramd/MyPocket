package com.capstone.mypocket

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.capstone.mypocket.databinding.FragmentAddSavingsBinding

class AddSavingsFragment : Fragment() {
    private lateinit var binding: FragmentAddSavingsBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAddSavingsBinding.inflate(inflater, container, false)
        return binding.root
    }
}