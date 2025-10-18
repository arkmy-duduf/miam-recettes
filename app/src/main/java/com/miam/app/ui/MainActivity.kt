package com.miam.app.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.commit
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.miam.app.R

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (savedInstanceState == null) {
            supportFragmentManager.commit {
                replace(R.id.fragmentContainer, StockFragment())
            }
        }

        val bottom = findViewById<BottomNavigationView>(R.id.bottomNav)
        bottom.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.nav_stock -> supportFragmentManager.commit { replace(R.id.fragmentContainer, StockFragment()) }
                R.id.nav_scan -> supportFragmentManager.commit { replace(R.id.fragmentContainer, ScanFragment()) }
                R.id.nav_recipes -> supportFragmentManager.commit { replace(R.id.fragmentContainer, RecipesFragment()) }
            }
            true
        }
    }
}