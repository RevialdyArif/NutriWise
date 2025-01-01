package com.example.technopreneur

data class ProductResponse(
    val product: ProductDetails?
)

data class ProductDetails(
    val product_name: String?,
    val brands: String?,
    val categories: String?,
    val nutriments: Nutriments?,
    val nutriscore_grade: String?
)

data class Nutriments(
    val energy: Float?,
    val fat: Float?,
    val carbohydrates: Float?,
    val sugars: Float?,
    val fiber: Float?,
    val proteins: Float?,
    val salt: Float?,
    val sodium: Float?,
)


