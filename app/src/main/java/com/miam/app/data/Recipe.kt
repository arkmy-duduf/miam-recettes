package com.miam.app.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters

@Entity
@TypeConverters(Converters::class)
data class Recipe(
    @PrimaryKey val id: String,
    val title: String,
    val imageUrl: String? = null,
    val ingredients: List<String> = emptyList(), // 'name:qty:unit'
    val steps: List<String> = emptyList(),
    val seasons: List<String> = emptyList() // 'ete','hiver', etc.
)