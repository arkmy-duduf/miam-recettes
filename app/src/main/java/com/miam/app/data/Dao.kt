package com.miam.app.data

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface ProductDao {
    @Query(\"SELECT * FROM Product ORDER BY name\")
    fun all(): LiveData<List<Product>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(p: Product): Long

    @Delete suspend fun delete(p: Product)

    @Query(\"UPDATE Product SET qty = qty + :delta WHERE id = :id\")
    suspend fun addQty(id: Long, delta: Double)
}

@Dao
interface RecipeDao {
    @Query(\"SELECT * FROM Recipe ORDER BY title\")
    suspend fun all(): List<Recipe>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(r: Recipe)
}