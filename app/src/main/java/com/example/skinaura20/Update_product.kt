package com.example.skinaura20.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.example.skinaura20.R
import com.google.firebase.firestore.FirebaseFirestore

class Update_product : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_update_product, container, false)

        // Initialize Views
        val etName = view.findViewById<EditText>(R.id.editTextName)
        val etPrice = view.findViewById<EditText>(R.id.editTextPrice)
        val etDesc = view.findViewById<EditText>(R.id.editTextDescription)
        val etStock = view.findViewById<EditText>(R.id.editTextStock) // Naya Quantity Field
        val spinnerSkin = view.findViewById<Spinner>(R.id.spinnerSkinType)
        val spinnerProduct = view.findViewById<Spinner>(R.id.spinnerProductType)
        val btnUpdate = view.findViewById<Button>(R.id.buttonInsert)
        val imgPrev = view.findViewById<ImageView>(R.id.imagePreview)

        // 1. Data Receive karna (Jo Adapter se pass kiya gaya hai)
        val pId = arguments?.getString("pId")
        val pName = arguments?.getString("pName")
        val pPrice = arguments?.getString("pPrice")
        val pDesc = arguments?.getString("pDesc")
        val pImg = arguments?.getString("pImg")
        val pStock = arguments?.getString("pStock") // Stock receive kiya
        val pSkinType = arguments?.getString("pSkinType")
        val pProdType = arguments?.getString("pProdType")

        // 2. UI par Data Set karna
        etName.setText(pName)
        etPrice.setText(pPrice)
        etDesc.setText(pDesc)
        etStock.setText(pStock)
        Glide.with(requireContext()).load(pImg).placeholder(R.drawable.placeholder_image).into(imgPrev)

        // 3. Spinners Setup (Example List)
        val skinTypes = arrayOf("Oily", "Dry", "Sensitive", "Combination")
        val productTypes = arrayOf("Face Wash", "Moisturizer", "Serum", "Sunscreen")

        val skinAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, skinTypes)
        val prodAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, productTypes)

        spinnerSkin.adapter = skinAdapter
        spinnerProduct.adapter = prodAdapter

        // Pre-select existing spinner values
        pSkinType?.let { spinnerSkin.setSelection(skinTypes.indexOf(it)) }
        pProdType?.let { spinnerProduct.setSelection(productTypes.indexOf(it)) }

        // 4. Update Logic
        btnUpdate.setOnClickListener {
            val name = etName.text.toString().trim()
            val price = etPrice.text.toString().trim()
            val desc = etDesc.text.toString().trim()
            val stock = etStock.text.toString().trim()
            val selectedSkin = spinnerSkin.selectedItem.toString()
            val selectedProd = spinnerProduct.selectedItem.toString()

            if (name.isEmpty() || price.isEmpty() || stock.isEmpty()) {
                Toast.makeText(requireContext(), "Please fill important fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val updatedData = mapOf(
                "name" to name,
                "price" to price,
                "description" to desc,
                "productStock" to stock, // Firebase mein 'productStock' update hoga
                "skinType" to selectedSkin,
                "productType" to selectedProd
            )

            if (pId != null) {
                FirebaseFirestore.getInstance().collection("Products").document(pId)
                    .update(updatedData)
                    .addOnSuccessListener {
                        Toast.makeText(requireContext(), "Product Updated Successfully! ✅", Toast.LENGTH_SHORT).show()
                        parentFragmentManager.popBackStack()
                    }
                    .addOnFailureListener {
                        Toast.makeText(requireContext(), "Error: ${it.message}", Toast.LENGTH_SHORT).show()
                    }
            }
        }

        return view
    }
}