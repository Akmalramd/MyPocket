package com.capstone.mypocket

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.capstone.mypocket.databinding.FragmentSavingsBinding
import okhttp3.*
import org.json.JSONObject
import java.io.IOException

class SavingsFragment : Fragment() {

    private var _binding: FragmentSavingsBinding? = null
    private val binding get() = _binding!!
    private val client = OkHttpClient()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSavingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.buttonContainer.setOnClickListener {
            findNavController().navigate(R.id.action_savingsFragment_to_newPageFragment)
        }
        fetchSavingsHistory()
    }

    private fun fetchSavingsHistory() {
        val url = "http://34.128.92.241:3000/history"
        val request = Request.Builder()
            .url(url)
            .get()
            .build()

        // Show the ProgressBar
        binding.progressBar.visibility = View.VISIBLE

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                activity?.runOnUiThread {
                    binding.progressBar.visibility = View.GONE
                    Toast.makeText(context, "Failed to load history: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful) {
                    val responseBody = response.body?.string()
                    if (responseBody != null) {
                        val responseJson = JSONObject(responseBody)
                        val dataArray = responseJson.getJSONArray("data")

                        activity?.runOnUiThread {
                            binding.progressBar.visibility = View.GONE
                            for (i in 0 until dataArray.length()) {
                                val dataObject = dataArray.getJSONObject(i)
                                val resultObject = dataObject.getJSONObject("result")
                                val savingName = dataObject.getString("savingName")
                                val monthlySavingPrediction = resultObject.getDouble("monthly_saving_prediction")
                                val totalSavingInstallmentPrediction = resultObject.getInt("total_saving_installment_prediction")

                                addSavingsView(savingName, monthlySavingPrediction, totalSavingInstallmentPrediction)
                            }
                        }
                    }
                } else {
                    activity?.runOnUiThread {
                        binding.progressBar.visibility = View.GONE
                        Toast.makeText(context, "Failed to load history: ${response.message}", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        })
    }

    private fun addSavingsView(savingName: String, monthlySavingPrediction: Double, totalSavingInstallmentPrediction: Int) {
        val savingsView = layoutInflater.inflate(R.layout.savings_item, null)

        val savingsTitle = savingsView.findViewById<TextView>(R.id.savings_title)
        val savingsEstimateDays = savingsView.findViewById<TextView>(R.id.savings_estimate_days)
        val savingsAmountPerDay = savingsView.findViewById<TextView>(R.id.savings_amount_per_day)

        savingsTitle.text = savingName
        savingsEstimateDays.text = "$totalSavingInstallmentPrediction Bulan"
        savingsAmountPerDay.text = "Rp.${String.format("%.0f", monthlySavingPrediction)}/Bulan"

        val params = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        params.setMargins(0, 0, 0, 16)  // Add bottom margin to create gap
        savingsView.layoutParams = params

        binding.savingsContainer.addView(savingsView)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
