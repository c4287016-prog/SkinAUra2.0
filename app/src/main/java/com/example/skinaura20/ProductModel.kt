package com.example.skinaura20.model

import java.io.Serializable

// Serializable add karna zaroori hai taaki cartList ko Bundle mein pass kiya ja sake
data class ProductModel(
    var id: String? = null,        // Firestore document ID
    val name: String? = null,
    val price: String? = null,
    val description: String? = null,
    val imageUrl: String? = null,
    val skinType: String? = null,
    val productType: String? = null,
    var productStock: String? = null,
    val isPremium: Boolean = false,// Admin side se total stock
    var quantity: Int = 1            // User ne cart mein kitni qty select ki hai
) : Serializable