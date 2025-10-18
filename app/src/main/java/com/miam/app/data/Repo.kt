package com.miam.app.data

import android.content.Context
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import kotlinx.serialization.json.*

class Repo(ctx: Context) {
    private val db = AppDb.get(ctx)
    private val client = OkHttpClient()
    private val json = Json { ignoreUnknownKeys = true }

    fun productsLive() = db.productDao().all()
    suspend fun upsert(p: Product) = db.productDao().upsert(p)
    suspend fun delete(p: Product) = db.productDao().delete(p)
    suspend fun addQty(id: Long, delta: Double) = db.productDao().addQty(id, delta)

    suspend fun allRecipes(): List<Recipe> = db.recipeDao().all()
    suspend fun upsertRecipe(r: Recipe) = db.recipeDao().upsert(r)

    // Simple fetch OpenFoodFacts
    suspend fun fetchOff(barcode: String): Pair<String?, String?> = withContext(Dispatchers.IO) {
        try {
            val url = "https://world.openfoodfacts.org/api/v2/product/.json"
            val req = Request.Builder().url(url).build()
            client.newCall(req).execute().use { resp ->
                if (!resp.isSuccessful) return@withContext (null to null)
                val body = resp.body?.string() ?: return@withContext (null to null)
                val obj = json.parseToJsonElement(body).jsonObject
                val prod = obj["product"]?.jsonObject ?: return@withContext (null to null)
                val name = prod["product_name"]?.jsonPrimitive?.contentOrNull
                val img = prod["image_url"]?.jsonPrimitive?.contentOrNull
                return@withContext (name to img)
            }
        } catch (_: Exception) { return@withContext (null to null) }
    }

    // Very simple suggestion: filter recipes craftable with stock (>= needed * people)
    suspend fun suggest(recipes: List<Recipe>, products: List<Product>, people: Int): List<Recipe> {
        fun findQty(name: String, unit: String): Double =
            products.filter { it.name.equals(name, true) && it.unit.equals(unit, true) }.sumOf { it.qty }

        return recipes.filter { r ->
            r.ingredients.all { s ->
                val parts = s.split(":")
                if (parts.size < 3) return@all false
                val (n, q, u) = parts[0] to parts[1].toDoubleOrNull() to parts[2]
                val need = (q ?: 0.0) * people
                findQty(n, u) >= need
            }
        }
    }

    suspend fun cook(recipe: Recipe, products: List<Product>, people: Int) {
        for (s in recipe.ingredients) {
            val parts = s.split(":")
            if (parts.size < 3) continue
            val name = parts[0]
            val needPer = parts[1].toDoubleOrNull() ?: 0.0
            val unit = parts[2]
            var need = needPer * people
            val candidates = products.filter { it.name.equals(name, true) && it.unit.equals(unit, true) }.sortedBy { it.expiryDate ?: "9999-12-31" }
            for (p in candidates) {
                if (need <= 0) break
                val take = Math.min(p.qty, need)
                addQty(p.id, -take)
                need -= take
            }
        }
    }
}