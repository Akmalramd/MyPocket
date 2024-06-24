package com.capstone.mypocket


import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.capstone.mypocket.R

class HomeFragment : Fragment() {

    private lateinit var greetingText: TextView
    private lateinit var sharedPreferences: SharedPreferences
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        // Initialize views
        greetingText = view.findViewById(R.id.greetingText)

        // Initialize SharedPreferences
        sharedPreferences = requireActivity().getSharedPreferences("user_prefs", Context.MODE_PRIVATE)

        // Retrieve the user's email from SharedPreferences
        val userEmail = sharedPreferences.getString("user_email", "User")

        // Set the greeting text
        greetingText.text = "Hi, $userEmail"

        return view
    }
}