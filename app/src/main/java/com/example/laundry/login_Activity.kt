package com.example.laundry // This is your actual package name

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.text.InputType
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.laundry.Data_model.ModelUser // Correct import for your data model
import com.example.laundry.MainActivity
import com.example.laundry.RegisterActivity
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase


class login_Activity : AppCompatActivity() {

    private lateinit var edtPhone: EditText
    private lateinit var edtPassword: EditText
    private lateinit var btnLogin: Button
    private lateinit var tvRegister: TextView
    private lateinit var ivTogglePassword: ImageView
    private lateinit var database: DatabaseReference
    private lateinit var sharedPreferences: SharedPreferences
    private val TAG = "LoginActivity"
    private var isPasswordVisible = false

    // Constants
    private val PREF_NAME = "LoginPrefs"
    private val KEY_IS_LOGGED_IN = "isLoggedIn"
    private val KEY_LOGIN_TIME = "loginTime"
    private val KEY_USER_NAME = "userName"
    private val KEY_USER_PHONE = "userPhone"

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize SharedPreferences
        sharedPreferences = getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)

        // Set the correct layout file. No need for full package path if imported correctly.
        setContentView(R.layout.activity_login)

        // Directly use R.id without the full package path as 'R' is already resolved.
        edtPhone = findViewById(R.id.LoginPhone)
        edtPassword = findViewById(R.id.LoginPassword)
        btnLogin = findViewById(R.id.buttonLogin)
        tvRegister = findViewById(R.id.tvRegister)
        ivTogglePassword = findViewById(R.id.ivTogglePassword)

        // Initialize Firebase
        database = FirebaseDatabase.getInstance().getReference("users")

        // Setup toggle password visibility
        setupPasswordToggle()

        btnLogin.setOnClickListener {
            Log.d(TAG, "Login button clicked")
            val phone = edtPhone.text.toString().trim()
            val password = edtPassword.text.toString().trim()

            Log.d(TAG, "Phone: $phone, Password length: ${password.length}")

            if (phone.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Isi No. Telp dan Password!", Toast.LENGTH_SHORT).show()
                Log.w(TAG, "Empty phone or password")
            } else {
                loginUser(phone, password)
            }
        }

        tvRegister.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
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
            // Move cursor to end of text
            edtPassword.setSelection(edtPassword.text.length)
        }
    }

    private fun redirectToMainActivity() {
        val userName = sharedPreferences.getString(KEY_USER_NAME, "")
        val userPhone = sharedPreferences.getString(KEY_USER_PHONE, "")

        val intent = Intent(this, MainActivity::class.java)
        intent.putExtra("nama", userName)
        intent.putExtra("phone", userPhone)
        startActivity(intent)
        finish()
    }

    private fun saveLoginSession(userName: String, userPhone: String) {
        val editor = sharedPreferences.edit()
        editor.putBoolean(KEY_IS_LOGGED_IN, true)
        editor.putLong(KEY_LOGIN_TIME, System.currentTimeMillis())
        editor.putString(KEY_USER_NAME, userName)
        editor.putString(KEY_USER_PHONE, userPhone)
        editor.apply()

        Log.d(TAG, "Login session saved")
    }

    private fun clearLoginSession() {
        val editor = sharedPreferences.edit()
        editor.clear()
        editor.apply()

        Log.d(TAG, "Login session cleared")
    }

    private fun loginUser(phone: String, password: String) {
        Log.d(TAG, "Attempting to login user with phone: $phone")

        database.child(phone).get().addOnSuccessListener { snapshot ->
            Log.d(TAG, "Database query successful")
            Log.d(TAG, "Snapshot exists: ${snapshot.exists()}")

            if (snapshot.exists()) {
                val user = snapshot.getValue(ModelUser::class.java)

                if (user != null) {
                    Log.d(TAG, "User object: $user")

                    if (user.password == password) {
                        Log.d(TAG, "Password match - Login successful")
                        // Use R.string directly after ensuring strings are defined in strings.xml
                        Toast.makeText(this, getString(R.string.LoginBerhasil), Toast.LENGTH_SHORT).show()

                        // Simpan session login
                        saveLoginSession(user.username ?: "", phone)

                        val intent = Intent(this, MainActivity::class.java)
                        intent.putExtra("nama", user.username)
                        intent.putExtra("phone", phone)
                        startActivity(intent)
                        finish()
                    } else {
                        Log.w(TAG, "Password mismatch")
                        Toast.makeText(this, getString(R.string.Passwordsalah), Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Log.e(TAG, "User object is null or failed to parse")
                    Toast.makeText(this, getString(R.string.Datapenggunatidakvalid), Toast.LENGTH_SHORT).show()
                }
            } else {
                Log.w(TAG, "User with phone $phone not found")
                Toast.makeText(this, getString(R.string.Nomorbelumterdaftar), Toast.LENGTH_SHORT).show()
            }
        }.addOnFailureListener { exception ->
            Log.e(TAG, "Database error: ${exception.message}")
            Toast.makeText(this, "Terjadi kesalahan: ${exception.message}", Toast.LENGTH_SHORT).show()
        }
    }

    fun logout() {
        clearLoginSession()
        val intent = Intent(this, login_Activity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }
}