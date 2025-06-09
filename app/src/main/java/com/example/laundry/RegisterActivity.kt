package com.example.laundry

import android.annotation.SuppressLint
import android.os.Bundle
import android.text.InputType
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity // Correct import for AppCompatActivity
import com.example.laundry.Data_model.ModelUser
import com.google.firebase.database.FirebaseDatabase


class RegisterActivity : AppCompatActivity() {

    private lateinit var edtNama: EditText
    private lateinit var edtPhone: EditText
    private lateinit var edtPassword: EditText
    private lateinit var btnRegister: Button
    private lateinit var ivTogglePassword: ImageView
    private var isPasswordVisible = false

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        // Initialize UI elements with the correct IDs from XML
        edtNama = findViewById(R.id.RegisterUsername)
        edtPhone = findViewById(R.id.RegisterPhone)
        edtPassword = findViewById(R.id.RegisterPassword)
        btnRegister = findViewById(R.id.buttonRegister) // Matches XML's buttonRegister
        ivTogglePassword = findViewById(R.id.ivTogglePassword) // Matches XML's ivTogglePassword

        setupPasswordToggle()

        btnRegister.setOnClickListener {
            val nama = edtNama.text.toString().trim()
            val phone = edtPhone.text.toString().trim()
            val password = edtPassword.text.toString().trim()

            if (nama.isEmpty() || phone.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Isi semua field!", Toast.LENGTH_SHORT).show()
            } else {
                // Ensure model_user constructor matches your model_user class definition.
                // Assuming model_user takes password and username.
                val user = ModelUser(password = password, username = nama)

                val db = FirebaseDatabase.getInstance().getReference("users")
                db.child(phone).setValue(user)
                    .addOnSuccessListener {
                        // Use R.string directly as com.example.laundry.R is imported
                        Toast.makeText(this, getString(R.string.Registerberhasil), Toast.LENGTH_SHORT).show()
                        finish() // Go back to the previous activity (likely LoginActivity)
                    }
                    .addOnFailureListener {
                        Toast.makeText(this, "Gagal: ${it.message}", Toast.LENGTH_SHORT).show()
                    }
            }
        }
    }

    private fun setupPasswordToggle() {
        ivTogglePassword.setOnClickListener {
            if (isPasswordVisible) {
                // Hide password
                edtPassword.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
                ivTogglePassword.setImageResource(android.R.drawable.ic_menu_view)
                isPasswordVisible = false
            } else {
                // Show password
                edtPassword.inputType = InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
                ivTogglePassword.setImageResource(android.R.drawable.ic_menu_close_clear_cancel)
                isPasswordVisible = true
            }
            // Move cursor to end of text to maintain user experience
            edtPassword.setSelection(edtPassword.text.length)
        }
    }
}