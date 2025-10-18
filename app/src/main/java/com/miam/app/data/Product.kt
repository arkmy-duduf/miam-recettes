package com.miam.app.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Product(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val barcode: String? = null,
    val name: String,
    val qty: Double,
    val unit: String,
    val location: String,
    val expiryDate: String? = null, // YYYY-MM-DD
    val imageUrl: String? = null
)