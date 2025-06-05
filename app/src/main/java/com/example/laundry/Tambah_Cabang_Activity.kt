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
import com.example.laundry.Data_model.ModelCabang
import com.google.firebase.database.FirebaseDatabase

class Tambah_Cabang_Activity : AppCompatActivity() {
    private val database = FirebaseDatabase.getInstance()
    private val myRef = database.getReference("cabang") // Referensi ke node "cabang"

    private lateinit var tvJudul: TextView
    private lateinit var etNamaCabang: EditText
    private lateinit var etAlamatCabang: EditText
    private lateinit var etNoHPCabang: EditText
    private lateinit var btnSimpanCabang: Button

    private var cabangId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_tambah_cabang)

        initViews()
        getIntentData()
        setupClickListener()

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun initViews() {
        tvJudul = findViewById(R.id.tv_title_CABANG)
        etNamaCabang = findViewById(R.id.et_nama_CABANG)
        etAlamatCabang = findViewById(R.id.et_alamat_CABANG)
        etNoHPCabang = findViewById(R.id.et_no_hp_CABANG)
        btnSimpanCabang = findViewById(R.id.btn_simpan_CABANG)
    }

    private fun getIntentData() {
        val dataIntent = intent
        if (dataIntent != null) {
            cabangId = dataIntent.getStringExtra("idCabang")
            val nama = dataIntent.getStringExtra("namaCabang")
            val alamat = dataIntent.getStringExtra("alamatCabang")
            val noHp = dataIntent.getStringExtra("noHPCabang")

            if (cabangId != null) {
                tvJudul.text = "Edit Data Cabang"
                btnSimpanCabang.text = "Update"
                etNamaCabang.setText(nama)
                etAlamatCabang.setText(alamat)
                etNoHPCabang.setText(noHp)
            }
        }
    }

    private fun setupClickListener() {
        btnSimpanCabang.setOnClickListener {
            validateAndSave()
        }
    }

    private fun validateAndSave() {
        val namaCabang = etNamaCabang.text.toString().trim()
        val alamatCabang = etAlamatCabang.text.toString().trim()
        val noHPCabang = etNoHPCabang.text.toString().trim()

        if (namaCabang.isEmpty()) {
            etNamaCabang.error = "Nama cabang tidak boleh kosong"
            Toast.makeText(this, "Nama cabang tidak boleh kosong", Toast.LENGTH_SHORT).show()
            etNamaCabang.requestFocus()
            return
        }
        if (alamatCabang.isEmpty()) {
            etAlamatCabang.error = "Alamat cabang tidak boleh kosong"
            Toast.makeText(this, "Alamat cabang tidak boleh kosong", Toast.LENGTH_SHORT).show()
            etAlamatCabang.requestFocus()
            return
        }
        if (noHPCabang.isEmpty()) {
            etNoHPCabang.error = "Nomor HP tidak boleh kosong"
            Toast.makeText(this, "Nomor HP tidak boleh kosong", Toast.LENGTH_SHORT).show()
            etNoHPCabang.requestFocus()
            return
        }

        if (cabangId == null) {
            saveNewCabang(namaCabang, alamatCabang, noHPCabang)
        } else {
            updateExistingCabang(namaCabang, alamatCabang, noHPCabang)
        }
    }

    private fun saveNewCabang(nama: String, alamat: String, noHp: String) {
        val newCabangRef = myRef.push()
        val id = newCabangRef.key
        val data = ModelCabang(id, nama, alamat, noHp)

        newCabangRef.setValue(data)
            .addOnSuccessListener {
                Toast.makeText(this, "Berhasil menambahkan cabang baru", Toast.LENGTH_SHORT).show()
                finish()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Gagal menambahkan cabang: ${it.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun updateExistingCabang(nama: String, alamat: String, noHp: String) {
        val dataUpdate = mapOf(
            "namaCabang" to nama,
            "alamatCabang" to alamat,
            "noHPCabang" to noHp
        )

        myRef.child(cabangId ?: "").updateChildren(dataUpdate)
            .addOnSuccessListener {
                Toast.makeText(this, "Berhasil memperbarui data cabang", Toast.LENGTH_SHORT).show()
                finish()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Gagal memperbarui data cabang: ${it.message}", Toast.LENGTH_SHORT).show()
            }
    }
}