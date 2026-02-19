package com.example.skinaura20.fragment

import android.app.ProgressDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import com.example.skinaura20.R
import com.example.skinaura20.model.OrderModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.*

class Address : Fragment() {

    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()
    private lateinit var progressDialog: ProgressDialog

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_address, container, false)

        progressDialog = ProgressDialog(requireContext())
        progressDialog.setMessage("Placing your order...")
        progressDialog.setCancelable(false)

        val etName = view.findViewById<EditText>(R.id.etName)
        val etNumber = view.findViewById<EditText>(R.id.etNumber)
        val etAddress = view.findViewById<EditText>(R.id.etAddress)
        val etPincode = view.findViewById<EditText>(R.id.etPincode)
        val etCity = view.findViewById<EditText>(R.id.etCity)
        val paymentGroup = view.findViewById<RadioGroup>(R.id.paymentGroup)
        val displayPrice = view.findViewById<TextView>(R.id.orderTotalTv)
        val btnPlaceOrder = view.findViewById<Button>(R.id.btnPlaceOrder)

        // 🔥 KEY MATCHED: "totalPrice"
        val totalAmount = arguments?.getString("totalPrice") ?: "0"
        displayPrice.text = "Payable Amount: ₹$totalAmount"

        btnPlaceOrder.setOnClickListener {
            val name = etName.text.toString().trim()
            val number = etNumber.text.toString().trim()
            val addr = etAddress.text.toString().trim()
            val pin = etPincode.text.toString().trim()
            val city = etCity.text.toString().trim()
            val isUpi = paymentGroup.checkedRadioButtonId == R.id.rbUpi

            if (name.isEmpty() || number.isEmpty() || addr.isEmpty() || pin.isEmpty() || city.isEmpty()) {
                Toast.makeText(requireContext(), "Please fill all details", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val fullAddress = "$addr, $city - $pin"
            startCheckoutProcess(name, number, fullAddress, totalAmount, isUpi)
        }
        return view
    }

    private fun startCheckoutProcess(name: String, num: String, addr: String, price: String, isUpi: Boolean) {
        val userId = auth.currentUser?.uid ?: return
        progressDialog.show()

        db.collection("Cart").document(userId).collection("Items").get().addOnSuccessListener { cartSnapshot ->
            if (cartSnapshot.isEmpty) {
                progressDialog.dismiss()
                Toast.makeText(requireContext(), "Cart is empty!", Toast.LENGTH_SHORT).show()
                return@addOnSuccessListener
            }

            db.runTransaction { transaction ->
                for (itemDoc in cartSnapshot) {
                    val productId = itemDoc.getString("productId") ?: ""
                    val orderQty = itemDoc.getLong("quantity")?.toInt() ?: 1

                    val productRef = db.collection("Products").document(productId)
                    val productSnapshot = transaction.get(productRef)

                    if (!productSnapshot.exists()) throw Exception("Product not found!")

                    // 🔥 FIX: Check both 'productStock' and 'productQty' to be safe
                    // Aur value ko string se int mein convert karein
                    val stockString = productSnapshot.getString("productStock")
                        ?: productSnapshot.getString("productQty")
                        ?: "0"

                    val currentStock = stockString.toInt()

                    if (currentStock < orderQty) {
                        val pName = productSnapshot.getString("name") ?: "Product"
                        throw Exception("Limited stock for $pName (Available: $currentStock)")
                    } else {
                        // 🔥 Stock update karein
                        val newStockValue = (currentStock - orderQty).toString()

                        // Firestore mein wahi field update karein jo aapne use ki hai
                        if (productSnapshot.contains("productStock")) {
                            transaction.update(productRef, "productStock", newStockValue)
                        } else {
                            transaction.update(productRef, "productQty", newStockValue)
                        }
                    }
                }

                // Order Create Logic (Same as before)
                val orderId = db.collection("Orders").document().id
                val orderData = hashMapOf(
                    "orderId" to orderId,
                    "userId" to userId,
                    "userName" to name,
                    "userNumber" to num,
                    "userAddress" to addr,
                    "totalPrice" to price,
                    "paymentMethod" to (if(isUpi) "UPI" else "COD"),
                    "date" to SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(Date()),
                    "time" to SimpleDateFormat("HH:mm a", Locale.getDefault()).format(Date()),
                    "status" to "Confirmed",
                    "timestamp" to System.currentTimeMillis()
                )

                transaction.set(db.collection("Orders").document(orderId), orderData)
                null
            }.addOnSuccessListener {
                clearCart(userId, isUpi, price)
            }.addOnFailureListener { e ->
                progressDialog.dismiss()
                // Isse aapko pata chal jayega ki actual error kya hai
                Toast.makeText(requireContext(), "${e.message}", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun clearCart(userId: String, isUpi: Boolean, price: String) {
        db.collection("Cart").document(userId).collection("Items").get().addOnSuccessListener { snapshot ->
            val batch = db.batch()
            for (doc in snapshot) batch.delete(doc.reference)
            batch.commit().addOnSuccessListener {
                progressDialog.dismiss()
                if (isUpi) {
                    moveToPayment(price)
                } else {
                    Toast.makeText(requireContext(), "Order Placed Successfully! 🎉", Toast.LENGTH_SHORT).show()
                    parentFragmentManager.beginTransaction()
                        .replace(R.id.fragmentContainer, Orderconfirm())
                        .commit()
                }
            }
        }
    }

    private fun moveToPayment(amount: String) {
        val frag = Payment()
        // 🔥 KEY MATCHED: "totalPrice"
        frag.arguments = Bundle().apply { putString("totalPrice", amount) }
        parentFragmentManager.beginTransaction().replace(R.id.fragmentContainer, frag).commit()
    }
}