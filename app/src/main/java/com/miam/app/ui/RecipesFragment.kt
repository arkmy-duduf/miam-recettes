package com.miam.app.ui

import android.os.Bundle
import android.view.*
import android.widget.*
import androidx.fragment.app.Fragment
import com.miam.app.R
import com.miam.app.data.Recipe
import com.miam.app.data.Repo
import kotlinx.coroutines.*
import androidx.lifecycle.Observer

class RecipesFragment : Fragment() {
    private lateinit var repo: Repo
    private lateinit var edPeople: EditText
    private lateinit var list: ListView
    private lateinit var adapter: ArrayAdapter<String>

    private var products = emptyList<com.miam.app.data.Product>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val v = inflater.inflate(R.layout.fragment_recipes, container, false)
        repo = Repo(requireContext())
        edPeople = v.findViewById(R.id.edPeople)
        list = v.findViewById(R.id.listRecipes)
        adapter = ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, mutableListOf())
        list.adapter = adapter

        // seed quelques recettes
        Thread {
            repo.upsertRecipe(Recipe("pates_tomate", "Pâtes sauce tomate", null,
                listOf("pâtes:100:g", "sauce tomate:150:g"), listOf("Cuire pâtes", "Chauffer sauce")))
            repo.upsertRecipe(Recipe("omelette", "Omelette", null,
                listOf("oeufs:2:pcs", "beurre:10:g"), listOf("Battre oeufs", "Cuire")))
        }.start()

        v.findViewById<Button>(R.id.btnSuggest).setOnClickListener {
            val people = edPeople.text.toString().toIntOrNull() ?: 2
            GlobalScope.launch(Dispatchers.Main) {
                val recipes = withContext(Dispatchers.IO) { repo.allRecipes() }
                val sugg = withContext(Dispatchers.IO) { repo.suggest(recipes, products, people) }
                adapter.clear()
                adapter.addAll(sugg.map { it.title })
                list.setOnItemClickListener { _,_,pos,_ ->
                    val r = sugg[pos]
                    AlertDialog.Builder(requireContext())
                        .setTitle(r.title)
                        .setMessage("Cuisiner pour  ? Cela décrémente le stock.")
                        .setPositiveButton(getString(R.string.cook)) { _,_ ->
                            GlobalScope.launch(Dispatchers.IO) {
                                repo.cook(r, products, people)
                            }
                        }.setNegativeButton("Annuler", null).show()
                }
            }
        }

        repo.productsLive().observe(viewLifecycleOwner, Observer { products = it })
        return v
    }
}