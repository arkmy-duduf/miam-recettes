package com.miam.app.ui

import android.os.Bundle
import android.view.*
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import com.miam.app.R
import com.miam.app.data.Product
import com.miam.app.data.Repo

class AddProductFragment : Fragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val v = inflater.inflate(R.layout.fragment_add_product, container, false)
        val edBarcode = v.findViewById<EditText>(R.id.edBarcode)
        val edName = v.findViewById<EditText>(R.id.edName)
        val edQty = v.findViewById<EditText>(R.id.edQty)
        val edUnit = v.findViewById<EditText>(R.id.edUnit)
        val edLocation = v.findViewById<EditText>(R.id.edLocation)
        val edExpiry = v.findViewById<EditText>(R.id.edExpiry)
        val edImage = v.findViewById<EditText>(R.id.edImage)
        val repo = Repo(requireContext())

        arguments?.getString("barcode")?.let { edBarcode.setText(it) }

        v.findViewById<Button>(R.id.btnFetch).setOnClickListener {
            val code = edBarcode.text.toString()
            if (code.isBlank()) { Toast.makeText(requireContext(), "Code-barres vide", Toast.LENGTH_SHORT).show(); return@setOnClickListener }
            Thread {
                val (name, img) = repo.fetchOff(code)
                requireActivity().runOnUiThread {
                    if (name!=null) edName.setText(name)
                    if (img!=null) edImage.setText(img)
                    if (name==null && img==null) Toast.makeText(requireContext(), "Pas trouvé", Toast.LENGTH_SHORT).show()
                }
            }.start()
        }

        v.findViewById<Button>(R.id.btnSave).setOnClickListener {
            val p = Product(
                barcode = edBarcode.text.toString().ifBlank { null },
                name = edName.text.toString().ifBlank { "Produit" },
                qty = edQty.text.toString().toDoubleOrNull() ?: 1.0,
                unit = edUnit.text.toString().ifBlank { "pcs" },
                location = edLocation.text.toString().ifBlank { "placard" },
                expiryDate = edExpiry.text.toString().ifBlank { null },
                imageUrl = edImage.text.toString().ifBlank { null }
            )
            Thread {
                repo.upsert(p)
                requireActivity().runOnUiThread {
                    parentFragmentManager.popBackStack()
                }
            }.start()
        }

        return v
    }
}