package com.example.skinaura20.fragment

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import com.example.skinaura20.R
import com.google.android.material.button.MaterialButton
import com.google.firebase.firestore.FirebaseFirestore
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.io.IOException

class Uploadproduct : Fragment() {

    private var imageUri: Uri? = null
    private lateinit var imgPreview: ImageView
    private lateinit var etName: EditText
    private lateinit var etPrice: EditText
    private lateinit var etDesc: EditText
    private lateinit var etQty: EditText
    private lateinit var spinSkin: Spinner
    private lateinit var spinProduct: Spinner
    private lateinit var cbPremium: CheckBox
    private lateinit var txtSkinLabel: TextView
    private lateinit var txtProductLabel: TextView

    private val cloudName = "dbuvtdrux"
    private val uploadPreset = "vplgfwap"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_uploadproduct, container, false)

        // Bind Views
        imgPreview = view.findViewById(R.id.imagePreview)
        etName = view.findViewById(R.id.editTextName)
        etPrice = view.findViewById(R.id.editTextPrice)
        etDesc = view.findViewById(R.id.editTextDescription)
        etQty = view.findViewById(R.id.editTextQuantity)
        spinSkin = view.findViewById(R.id.spinnerSkinType)
        spinProduct = view.findViewById(R.id.spinnerProductType)
        cbPremium = view.findViewById(R.id.isPremiumCheckBox)

        // Labels bind karna taaki hide kar sakein
        txtSkinLabel = view.findViewById(R.id.textViewSkinLabel)
        txtProductLabel = view.findViewById(R.id.textViewProductLabel)

        val btnSelect = view.findViewById<MaterialButton>(R.id.buttonSelectImage)
        val btnUpload = view.findViewById<MaterialButton>(R.id.buttonInsert)

        setupSpinners(spinSkin, spinProduct)

        // ⭐ UI Toggle Logic
        cbPremium.setOnCheckedChangeListener { _, isChecked ->
            val visibility = if (isChecked) View.GONE else View.VISIBLE
            spinSkin.visibility = visibility
            spinProduct.visibility = visibility
            txtSkinLabel.visibility = visibility
            txtProductLabel.visibility = visibility
        }

        val pickImage = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            if (uri != null) {
                imageUri = uri
                imgPreview.setImageURI(uri)
            }
        }

        btnSelect.setOnClickListener { pickImage.launch("image/*") }

        btnUpload.setOnClickListener {
            val name = etName.text.toString()
            val price = etPrice.text.toString()
            val desc = etDesc.text.toString()
            val qty = etQty.text.toString()
            val isPremium = cbPremium.isChecked

            // Logic: Agar premium hai toh fixed values, warna spinner values
            val skinType = if (isPremium) "Premium" else spinSkin.selectedItem.toString()
            val productType = if (isPremium) "Combo" else spinProduct.selectedItem.toString()

            if (name.isEmpty() || price.isEmpty() || qty.isEmpty() || imageUri == null) {
                Toast.makeText(requireContext(), "Complete all fields", Toast.LENGTH_SHORT).show()
            } else {
                uploadToCloudinary(name, price, desc, qty, skinType, productType, isPremium)
            }
        }

        return view
    }

    private fun setupSpinners(skinSpin: Spinner, prodSpin: Spinner) {
        ArrayAdapter.createFromResource(requireContext(), R.array.skin_types, android.R.layout.simple_spinner_item).also {
            it.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            skinSpin.adapter = it
        }
        ArrayAdapter.createFromResource(requireContext(), R.array.product_types, android.R.layout.simple_spinner_item).also {
            it.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            prodSpin.adapter = it
        }
    }

    private fun uploadToCloudinary(name: String, price: String, desc: String, qty: String, skin: String, type: String, isPremium: Boolean) {
        Toast.makeText(requireContext(), "Uploading...", Toast.LENGTH_SHORT).show()

        val client = OkHttpClient()
        val inputStream = requireContext().contentResolver.openInputStream(imageUri!!)
        val bytes = inputStream?.readBytes() ?: return

        val requestBody = MultipartBody.Builder()
            .setType(MultipartBody.FORM)
            .addFormDataPart("file", "img_${System.currentTimeMillis()}", bytes.toRequestBody("image/*".toMediaTypeOrNull()))
            .addFormDataPart("upload_preset", uploadPreset)
            .build()

        val request = Request.Builder().url("https://api.cloudinary.com/v1_1/$cloudName/image/upload").post(requestBody).build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {}
            override fun onResponse(call: Call, response: Response) {
                val url = JSONObject(response.body?.string() ?: "").getString("secure_url")
                saveDataToFirestore(name, price, desc, qty, url, skin, type, isPremium)
            }
        })
    }

    private fun saveDataToFirestore(name: String, price: String, desc: String, qty: String, url: String, skin: String, type: String, isPremium: Boolean) {
        val db = FirebaseFirestore.getInstance()
        val docRef = db.collection("Products").document()

        val productMap = hashMapOf(
            "id" to docRef.id,
            "name" to name,
            "price" to price,
            "description" to desc,
            "imageUrl" to url,
            "skinType" to skin,
            "productType" to type,
            "productStock" to qty,
            "isPremium" to isPremium // ⭐ Yeh filter ke liye zaroori hai
        )

        docRef.set(productMap).addOnSuccessListener {
            activity?.runOnUiThread {
                Toast.makeText(requireContext(), "Published!", Toast.LENGTH_SHORT).show()
                clearForm()
            }
        }
    }

    private fun clearForm() {
        etName.text.clear()
        etPrice.text.clear()
        etDesc.text.clear()
        etQty.text.clear()
        cbPremium.isChecked = false
        imgPreview.setImageResource(R.drawable.placeholder_image)
        imageUri = null
    }
}