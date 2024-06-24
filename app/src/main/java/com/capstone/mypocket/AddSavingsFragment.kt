package com.capstone.mypocket

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.capstone.mypocket.databinding.FragmentAddSavingsBinding
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.io.IOException

class AddSavingsFragment : Fragment() {

    private var _binding: FragmentAddSavingsBinding? = null
    private val binding get() = _binding!!
    private val client = OkHttpClient()
    private lateinit var loadingDialog: AlertDialog

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentAddSavingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnTambah.setOnClickListener {
            val savingName = binding.namaTabungan.text.toString().trim()
            val savingTarget = binding.targetTabungan.text.toString().trim()
            val monthlyIncome = binding.pemasukkanBulanan.text.toString().trim()
            val monthlyExpense = binding.pengeluaranBulanan.text.toString().trim()

            if (savingName.isEmpty() || savingTarget.isEmpty() || monthlyIncome.isEmpty() || monthlyExpense.isEmpty()) {
                Toast.makeText(context, "Please fill out all fields", Toast.LENGTH_SHORT).show()
            } else {
                val income = monthlyIncome.toInt()
                val expense = monthlyExpense.toInt()
                val saving = savingTarget.toInt()
                showLoadingDialog()
                performPrediction(income, expense, saving, savingName)
            }
        }
    }

    private fun performPrediction(income: Int, expense: Int, saving: Int, savingName: String) {
        val url = "http://34.128.92.241:3000/predict"
        val json = JSONObject()
        json.put("income", income)
        json.put("expense", expense)
        json.put("saving", saving)
        json.put("savingName", savingName)


        val requestBody = json.toString().toRequestBody("application/json".toMediaTypeOrNull())

        val request = Request.Builder()
            .url(url)
            .post(requestBody)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                activity?.runOnUiThread {
                    loadingDialog.dismiss()
                    Toast.makeText(context, "Prediction request failed: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onResponse(call: Call, response: Response) {
                activity?.runOnUiThread {
                    loadingDialog.dismiss()
                    if (response.isSuccessful) {
                        val responseBody = response.body?.string()
                        if (responseBody != null) {
                            // Handle the response here
                            showSuccessDialog()
                        }
                    } else {
                        Toast.makeText(context, "Prediction failed: ${response.message}", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        })
    }

    private fun showSuccessDialog() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_success, null)
        val successText = dialogView.findViewById<TextView>(R.id.successText)
        val okButton = dialogView.findViewById<Button>(R.id.okButton)

        successText.text = "Success!"

        val dialog = AlertDialog.Builder(requireContext())
            .setView(dialogView)
            .create()

        okButton.setOnClickListener {
            dialog.dismiss()
            parentFragmentManager.beginTransaction()
                .replace(R.id.activity_main_nav_host_fragment, SavingsFragment())
                .commit()
        }

        dialog.show()
    }

    private fun showLoadingDialog() {
        val loaderView = layoutInflater.inflate(R.layout.custom_loader, null)
        loadingDialog = AlertDialog.Builder(requireContext())
            .setView(loaderView)
            .setCancelable(false)
            .create()
        loadingDialog.show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}
