package com.capstone.mypocket

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.capstone.mypocket.databinding.FragmentTransactionBinding

class TransactionFragment : Fragment() {

    private lateinit var leftToggle: TextView
    private lateinit var rightToggle: TextView
    private lateinit var toggleContent: FrameLayout

    private lateinit var binding: FragmentTransactionBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentTransactionBinding.inflate(inflater, container, false)
        val view = binding.root

        leftToggle = binding.leftToggle
        rightToggle = binding.rightToggle
        toggleContent = binding.toggleContent

        leftToggle.isSelected = true
        replaceFragment(ExpenditureFragment())

        leftToggle.setOnClickListener {
            if (!it.isSelected) {
                it.isSelected = true
                rightToggle.isSelected = false
                replaceFragment(ExpenditureFragment())
            }
        }

        rightToggle.setOnClickListener {
            if (!it.isSelected) {
                it.isSelected = true
                leftToggle.isSelected = false
                replaceFragment(IncomeFragment())
            }
        }

        return view
    }

    private fun replaceFragment(fragment: Fragment) {
        childFragmentManager.beginTransaction()
            .replace(R.id.toggleContent, fragment)
            .commit()
    }
}
