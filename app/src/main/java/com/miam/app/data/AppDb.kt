package com.miam.app.data

import android.content.Context
import androidx.room.*

@Database(entities = [Product::class, Recipe::class], version = 1)
@TypeConverters(Converters::class)
abstract class AppDb : RoomDatabase() {
    abstract fun productDao(): ProductDao
    abstract fun recipeDao(): RecipeDao

    companion object {
        @Volatile private var inst: AppDb? = null
        fun get(ctx: Context): AppDb =
            inst ?: synchronized(this) {
                inst ?: Room.databaseBuilder(ctx, AppDb::class.java, \"miam.db\").build().also { inst = it }
            }
    }
}