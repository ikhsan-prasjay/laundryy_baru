package com.example.laundry.Pelanggan

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.laundry.Data_model.ModelPelanggan
import com.example.laundry.R
import com.google.firebase.database.FirebaseDatabase

class Tambah_pelangganActivity : AppCompatActivity() {
    val database = FirebaseDatabase.getInstance()
    val myRef = database.getReference("pelanggan")

    // Deklarasi Variabel UI
    private lateinit var tvJudul: TextView
    private lateinit var etNama: EditText
    private lateinit var etAlamat: EditText
    private lateinit var etNoHP: EditText
    private lateinit var btSimpan: Button

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tambah_pelanggan)
        enableEdgeToEdge()

        initViews()  // Pastikan inisialisasi dulu
        setupListeners() // Pasang listener setelah UI siap
        btSimpan.setOnClickListener{
            setupListeners()
        }

        // Mengatur Inset untuk StatusBar & NavigationBar
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.tambah_pelanggan)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    // Fungsi untuk inisialisasi View dari XML
    private fun initViews() {
        tvJudul = findViewById(R.id.tv_title)
        etNama = findViewById(R.id.et_nama)
        etAlamat = findViewById(R.id.et_alamat)
        etNoHP = findViewById(R.id.et_no_hp)
        btSimpan = findViewById(R.id.btn_simpan)

        Log.d("DEBUG", "View berhasil diinisialisasi")
    }

    // Fungsi untuk menangani klik tombol Simpan
    private fun setupListeners() {
        btSimpan.setOnClickListener {
            Log.d("DEBUG", "Tombol Simpan ditekan")

            val nama = etNama.text.toString().trim()
            val alamat = etAlamat.text.toString().trim()
            val noHP = etNoHP.text.toString().trim()

            // Validasi Input
            if (nama.isEmpty()) {
                etNama.error = getString(R.string.validasi_nama_pelanggan)
                Toast.makeText(this, getString(R.string.validasi_nama_pelanggan), Toast.LENGTH_SHORT).show()
                etNama.requestFocus()
                return@setOnClickListener
            }
            if (alamat.isEmpty()) {
                etAlamat.error = getString(R.string.validasi_alamat_pelanggan)
                Toast.makeText(this, getString(R.string.validasi_alamat_pelanggan), Toast.LENGTH_SHORT).show()
                etAlamat.requestFocus()
                return@setOnClickListener
            }
            if (noHP.isEmpty()) {
                etNoHP.error = getString(R.string.validasi_nohp_pelanggan)
                Toast.makeText(this, getString(R.string.validasi_nohp_pelanggan), Toast.LENGTH_SHORT).show()
                etNoHP.requestFocus()
                return@setOnClickListener
            }

            // Simpan ke Firebase
            val pelangganBaru = myRef.push()
            val pelangganId = pelangganBaru.key ?: "Unknown"
            val timestamp = System.currentTimeMillis().toString()
            val data = ModelPelanggan(pelangganId, nama, alamat, noHP, timestamp)

            pelangganBaru.setValue(data)
                .addOnSuccessListener {
                    Log.d("DEBUG", "Data pelanggan berhasil disimpan")
                    Toast.makeText(
                        this,
                        getString(R.string.berhasil_tambah_pelanggan),
                        Toast.LENGTH_SHORT
                    ).show()
                    finish()
                }
                .addOnFailureListener { e ->
                    Log.e("ERROR", "Gagal menyimpan data: ${e.message}")
                    Toast.makeText(
                        this,
                        getString(R.string.pelanggan_tambah_gagal),
                        Toast.LENGTH_SHORT
                    ).show()
                }
        }
    }
}
