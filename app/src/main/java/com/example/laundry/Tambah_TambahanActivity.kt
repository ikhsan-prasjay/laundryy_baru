package com.example.laundry

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.laundry.Data_model.ModelTambahan
import com.google.firebase.database.FirebaseDatabase


class Tambah_TambahanActivity : AppCompatActivity() {
    val database = FirebaseDatabase.getInstance()
    val myRef = database.getReference("tambahan")
    lateinit var tvJudul: TextView
    lateinit var etNama: EditText
    lateinit var etHarga: EditText
    lateinit var etCabang: EditText
    lateinit var btSimpan: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_tambah_tambahan)
        init()
        btSimpan.setOnClickListener{
            cekValidasi()
        }
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }


    fun init(){
        tvJudul = findViewById(R.id.tvtambahanjudul)
        etNama = findViewById(R.id.ettambahannama)
        etHarga = findViewById(R.id.ettambahanharga)
        etCabang = findViewById(R.id.ettambahancabang)
        btSimpan = findViewById(R.id.bttambahansimpan)
    }

    fun cekValidasi() {
        val nama = etNama.text.toString().trim()
        val harga = etHarga.text.toString().trim()
        val cabang = etCabang.text.toString().trim()

        if (nama.isEmpty()) {
            etNama.error = this.getString(R.string.validasi_nama_tambahan)
            Toast.makeText(this, this.getString(R.string.validasi_nama_tambahan), Toast.LENGTH_SHORT).show()
            etNama.requestFocus()
            return
        }
        // Perbaikan: Validasi harga harus numerik dan tidak kosong
        if (harga.isEmpty() || harga.toIntOrNull() == null) {
            etHarga.error = "Harga layanan tambahan tidak valid" // Pesan error yang lebih spesifik
            Toast.makeText(this, "Harga layanan tambahan harus berupa angka", Toast.LENGTH_SHORT).show()
            etHarga.requestFocus()
            return
        }
        if (cabang.isEmpty()) {
            etCabang.error = this.getString(R.string.validasi_cabang_tambahan)
            Toast.makeText(this, this.getString(R.string.validasi_cabang_tambahan), Toast.LENGTH_SHORT).show()
            etCabang.requestFocus()
            return
        }
        simpan()
    }

    // Di dalam fungsi simpan() pada Tambah_TambahanActivity.kt
    fun simpan() {
        val tambahanBaru = myRef.push()
        val tambahanid = tambahanBaru.key

        // Bersihkan string harga sebelum disimpan ke Firebase
        val cleanedHargaToSave = etHarga.text.toString().trim().replace(".", "").replace(",", "")

        val data = ModelTambahan(
            tambahanid.toString(),
            etNama.text.toString().trim(),
            cleanedHargaToSave, // Simpan harga yang sudah dibersihkan
            etCabang.text.toString().trim(),
        )
        tambahanBaru.setValue(data)
            .addOnSuccessListener {
                // ...
            }
            .addOnFailureListener {
                // ...
            }
    }
}