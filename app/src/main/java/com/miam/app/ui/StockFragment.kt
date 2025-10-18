package com.miam.app.ui

import android.app.AlertDialog
import android.os.Bundle
import android.view.*
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import androidx.lifecycle.Observer
import com.miam.app.R
import com.miam.app.data.Product
import com.miam.app.data.Repo

class StockFragment : Fragment() {
    private lateinit var repo: Repo
    private lateinit var list: ListView
    private lateinit var adapter: ArrayAdapter<String>
    private var items: List<Product> = emptyList()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val v = inflater.inflate(R.layout.fragment_stock, container, false)
        repo = Repo(requireContext())
        list = v.findViewById(R.id.list)
        adapter = ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, mutableListOf())
        list.adapter = adapter

        list.setOnItemLongClickListener { _, _, pos, _ ->
            val p = items[pos]
            AlertDialog.Builder(requireContext())
                .setTitle(p.name)
                .setItems(arrayOf("Supprimer", " +1", " -1")) { _, which ->
                    when (which) {
                        0 -> Thread { repo.delete(p) }.start()
                        1 -> Thread { repo.addQty(p.id, +1.0) }.start()
                        2 -> Thread { repo.addQty(p.id, -1.0) }.start()
                    }
                }.show()
            true
        }

        v.findViewById<Button>(R.id.btnAdd).setOnClickListener {
            parentFragmentManager.commit { replace(R.id.fragmentContainer, AddProductFragment()).addToBackStack(null) }
        }

        repo.productsLive().observe(viewLifecycleOwner, Observer { listProd ->
            items = listProd.sortedWith(compareBy<Product> { it.expiryDate ?: "9999-12-31" }.thenBy { it.name })
            adapter.clear()
            adapter.addAll(items.map {
                val exp = it.expiryDate?.let { d -> " (DLC )" } ?: ""
                val alert = it.expiryDate != null && it.expiryDate <= java.time.LocalDate.now().plusDays(2).toString()
                (if (alert) "⚠️ " else "") + " :   @"
            })
        })
        return v
    }
}