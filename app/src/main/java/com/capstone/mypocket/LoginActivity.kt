package com.capstone.mypocket

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.capstone.mypocket.databinding.ActivityLoginBinding
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.io.IOException

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private val sharedPreferences: SharedPreferences by lazy {
        getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.signInButton.setOnClickListener {
            // Handle sign in logic here
            val email = binding.emailEditText.text.toString().trim()
            val password = binding.passwordEditText.text.toString().trim()

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please enter email and password", Toast.LENGTH_SHORT).show()
            } else {
                performLogin(email, password)
            }
        }

        binding.signUpTextView.setOnClickListener {
            // Navigate to RegisterActivity
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }
    }

    private fun performLogin(email: String, password: String) {
        showLoadingDialog()
        val client = OkHttpClient()

        val jsonObject = JSONObject()
        jsonObject.put("email", email)
        jsonObject.put("password", password)

        val requestBody = jsonObject.toString().toRequestBody("application/json".toMediaTypeOrNull())

        val request = Request.Builder()
            .url("http://34.128.92.241:3000/login")
            .post(requestBody)
            .build()

        client.newCall(request).enqueue(object : okhttp3.Callback {
            override fun onFailure(call: okhttp3.Call, e: IOException) {
                runOnUiThread {
                    Toast.makeText(this@LoginActivity, "Login failed: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onResponse(call: okhttp3.Call, response: okhttp3.Response) {
                if (response.isSuccessful) {
                    runOnUiThread {
                        val responseBody = response.body?.string()
                        if (responseBody != null) {
                            // Extract email from the response
                            val email = responseBody.substringAfter("Hello, ").trim()

                            // Save email to SharedPreferences
                            with(sharedPreferences.edit()) {
                                putString("user_email", email)
                                apply()
                            }
                            showSuccessDialog(email)
                        }
                    }
                } else {
                    runOnUiThread {
                        Toast.makeText(this@LoginActivity, "Login failed", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        })
    }


    private fun showSuccessDialog(email: String?) {
        val dialogView = layoutInflater.inflate(R.layout.dialog_success, null)
        val checkIcon = dialogView.findViewById<ImageView>(R.id.checkIcon)
        val successText = dialogView.findViewById<TextView>(R.id.successText)
        val userNameText = dialogView.findViewById<TextView>(R.id.userNameText)
        val okButton = dialogView.findViewById<Button>(R.id.okButton)

        userNameText.text = "Welcome, ${email ?: "User"}!"

        val dialog = AlertDialog.Builder(this)
            .setView(dialogView)
            .create()

        okButton.setOnClickListener {
            dialog.dismiss()
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }

        dialog.show()
    }

    private fun showLoadingDialog() {
        val loaderView = layoutInflater.inflate(R.layout.custom_loader, null)
        val loaderDialog = AlertDialog.Builder(this)
            .setView(loaderView)
            .setCancelable(false)
            .create()
        loaderDialog.show()
    }
}
