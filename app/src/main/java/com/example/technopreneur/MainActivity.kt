package com.example.technopreneur

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.ComponentActivity
import com.google.zxing.integration.android.IntentIntegrator
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val titleText = findViewById<TextView>(R.id.titleText)
        val scanButton = findViewById<Button>(R.id.scanButton)

        titleText.text = "NutriWise" // Atur teks tampilan awal
        scanButton.setOnClickListener {
            startBarcodeScanner() // Pindai barcode saat tombol ditekan
        }
    }

    private fun startBarcodeScanner() {
        val integrator = IntentIntegrator(this)
        integrator.setDesiredBarcodeFormats(IntentIntegrator.ALL_CODE_TYPES)
        integrator.setPrompt("Scan a barcode")
        integrator.setCameraId(0) // Gunakan kamera belakang
        integrator.setBeepEnabled(true) // Suara beep saat scan berhasil
        integrator.setBarcodeImageEnabled(true) // Simpan gambar hasil scan
        integrator.initiateScan()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        val result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data)
        if (result != null) {
            if (result.contents == null) {
                Toast.makeText(this, "Scan cancelled", Toast.LENGTH_SHORT).show()
            } else {
                val barcode = result.contents
                fetchProductDetails(barcode)
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }

    private fun fetchProductDetails(barcode: String) {
        val apiService = RetrofitClient.instance
        apiService.getProductDetails(barcode).enqueue(object : Callback<ProductResponse> {
            override fun onResponse(
                call: Call<ProductResponse>,
                response: Response<ProductResponse>
            ) {
                if (response.isSuccessful) {
                    val product = response.body()?.product
                    if (product != null) {
                        val productName = product.product_name ?: "Unknown"
                        val brand = product.brands ?: "Unknown"
                        val category = product.categories ?: "Unknown"
                        val nutriments = product.nutriments
                        val nutriScore = product.nutriscore_grade?.uppercase() ?: "N/A"

                        // Tampilkan informasi produk
                        findViewById<TextView>(R.id.tv_product_name).text = "Product Name: $productName"
                        findViewById<TextView>(R.id.tv_brand).text = "Brand: $brand"
                        findViewById<TextView>(R.id.tv_category).text = "Category: $category"

                        // NutriScore - Bold dan berwarna
                        val nutritionInfo = """
                            NutriScore: ${getNutriScoreWithColor(nutriScore)}
                            Energy: ${nutriments?.energy ?: "N/A"} kj
                            Fat: ${nutriments?.fat ?: "N/A"} g
                            Carbohydrates: ${nutriments?.carbohydrates ?: "N/A"} g
                            Sugars: ${nutriments?.sugars ?: "N/A"} g
                            Fiber: ${nutriments?.fiber ?: "N/A"} g
                            Proteins: ${nutriments?.proteins ?: "N/A"} g
                            Salt: ${nutriments?.salt ?: "N/A"} g
                            Sodium: ${nutriments?.sodium ?: "N/A"} g
                        """.trimIndent()

                        findViewById<TextView>(R.id.tv_nutrition).text = nutritionInfo
                    } else {
                        Toast.makeText(this@MainActivity, "No product details found!", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(this@MainActivity, "Failed to fetch product details!", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<ProductResponse>, t: Throwable) {
                Toast.makeText(this@MainActivity, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    // Function to return NutriScore with color and bold for grade
    private fun getNutriScoreWithColor(nutriScore: String): Spannable {
        val spannableString = SpannableString(nutriScore)

        val color = when (nutriScore) {
            "A" -> Color.parseColor("#2E7D32") // Green
            "B" -> Color.parseColor("#FFEB3B") // Yellow
            "C" -> Color.parseColor("#FFC107") // Amber
            "D" -> Color.parseColor("#FF5722") // Orange
            "E" -> Color.parseColor("#D32F2F") // Red
            else -> Color.BLACK
        }

        // Set color and bold for the NutriScore grade
        spannableString.setSpan(ForegroundColorSpan(color), 0, nutriScore.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        return spannableString
    }
}
