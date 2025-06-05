package com.example.laundry

import android.annotation.SuppressLint
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.laundry.Data_model.ModelTambahan
import com.example.laundry.adapter.AdapterPilihTambahan
import com.google.firebase.FirebaseApp
import com.google.android.material.floatingactionbutton.FloatingActionButton // Import FloatingActionButton

import java.io.Serializable

class Data_Transaksi_Activity : AppCompatActivity() {
    private lateinit var tvNamaPelanggan: TextView
    private lateinit var tvNoHp: TextView
    private lateinit var tvNamaLayanan: TextView
    private lateinit var tvHargaLayanan: TextView
    private lateinit var rvLayananTambahan: RecyclerView
    private lateinit var btnPilihPelanggan: Button
    private lateinit var btnPilihLayanan: Button
    private lateinit var btnProses: Button
    private lateinit var fabTambahan: FloatingActionButton // Changed to FloatingActionButton

    private val dataList = mutableListOf<ModelTambahan>()
    private lateinit var additionalServiceAdapter: AdapterPilihTambahan // Declare adapter here

    private val pilihPelanggan = 1
    private val pilihLayanan = 2
    private val pilihLayananTambahan = 3

    private var idPelanggan = ""
    private var idCabang = ""
    private var namaPelanggan = ""
    private var noHP = ""
    private var idLayanan = ""
    private var namaLayanan = ""
    private var hargaLayanan = ""
    private var idPegawai = ""

    private lateinit var sharedPref: SharedPreferences

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_data_transaksi2)

        FirebaseApp.initializeApp(this)

        sharedPref = getSharedPreferences("user_data", MODE_PRIVATE)
        idCabang = sharedPref.getString("idCabang", "") ?: ""
        idPegawai = sharedPref.getString("idPegawai", "") ?: ""

        initViews()

        // Initialize the adapter for rvLayananTambahan
        // Menggunakan ArrayList(dataList) untuk memastikan tipe yang benar saat inisialisasi
        additionalServiceAdapter = AdapterPilihTambahan(ArrayList(dataList)) { item, isSelected ->
            // Logika ini akan dipicu ketika item dipilih/dibatalkan di AdapterPilihTambahan
            // Namun, pada Activity ini, kita akan memperbarui seluruh list saat kembali dari PilihLayananTambahanActivity
        }
        rvLayananTambahan.adapter = additionalServiceAdapter
        rvLayananTambahan.layoutManager = LinearLayoutManager(this).apply {
            reverseLayout = true
        }
        rvLayananTambahan.setHasFixedSize(true)

        btnPilihPelanggan.setOnClickListener {
            val intent = Intent(this, Pilih_pelanggan_Activity::class.java)
            startActivityForResult(intent, pilihPelanggan)
        }

        btnPilihLayanan.setOnClickListener {
            val intent = Intent(this, PilihLayananActivity::class.java)
            startActivityForResult(intent, pilihLayanan)
        }

        fabTambahan.setOnClickListener { // Changed to fabTambahan
            val intent = Intent(this, PilihLayananTambahanActivity::class.java)
            // Pass the currently selected additional services to the selection activity
            intent.putExtra("previously_selected_tambahan", ArrayList(dataList) as Serializable)
            startActivityForResult(intent, pilihLayananTambahan)
        }

        btnProses.setOnClickListener {
            if (validateData()) {
                val intent = Intent(this@Data_Transaksi_Activity, Konfirmasi_data_Transaksi_Activity::class.java)
                intent.putExtra("nama_pelanggan", namaPelanggan)
                intent.putExtra("nomor_hp", noHP)
                intent.putExtra("nama_layanan", namaLayanan)
                intent.putExtra("harga_layanan", hargaLayanan)
                intent.putExtra("layanan_tambahan", ArrayList(dataList) as Serializable) // Ensure it's Serializable
                startActivity(intent)
            }
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun validateData(): Boolean {
        if (namaPelanggan.isEmpty()) {
            Toast.makeText(this, "Pilih pelanggan terlebih dahulu", Toast.LENGTH_SHORT).show()
            return false
        }

        if (namaLayanan.isEmpty()) {
            Toast.makeText(this, "Pilih layanan utama terlebih dahulu", Toast.LENGTH_SHORT).show()
            return false
        }

        if (hargaLayanan.isEmpty() || hargaLayanan == "0") {
            Toast.makeText(this, "Harga layanan utama tidak valid", Toast.LENGTH_SHORT).show()
            return false
        }
        return true
    }

    private fun initViews() {
        tvNamaPelanggan = findViewById(R.id.tvNamaPelanggan)
        tvNoHp = findViewById(R.id.tvPelangganNoHP)
        tvNamaLayanan = findViewById(R.id.tvNamaLayanan)
        tvHargaLayanan = findViewById(R.id.tvLayananHarga)
        rvLayananTambahan = findViewById(R.id.rvLayananTambahan)
        btnPilihPelanggan = findViewById(R.id.btnPilihPelanggan)
        btnPilihLayanan = findViewById(R.id.btnPilihLayanan)
        fabTambahan = findViewById(R.id.fabTambahan) // Corrected ID for FloatingActionButton
        btnProses = findViewById(R.id.btnProses)
    }

    @Deprecated("This method has been deprecated in favor of using the Activity Result API")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == RESULT_OK && data != null) {
            when (requestCode) {
                pilihPelanggan -> {
                    idPelanggan = data.getStringExtra("idPelanggan").orEmpty()
                    namaPelanggan = data.getStringExtra("nama").orEmpty()
                    noHP = data.getStringExtra("noHP").orEmpty()

                    tvNamaPelanggan.text = "Nama Pelanggan : $namaPelanggan"
                    tvNoHp.text = "No HP : $noHP"
                }

                pilihLayanan -> {
                    idLayanan = data.getStringExtra("idLayanan").orEmpty()
                    namaLayanan = data.getStringExtra("nama").orEmpty()
                    hargaLayanan = data.getStringExtra("harga").orEmpty()

                    tvNamaLayanan.text = "Nama Layanan : $namaLayanan"
                    tvHargaLayanan.text = "Harga : $hargaLayanan"
                }

                pilihLayananTambahan -> {
                    // Directly get the list of selected additional services, yang sudah pasti ArrayList
                    val selectedList = data.getSerializableExtra("selected_tambahan_list") as? ArrayList<ModelTambahan>
                    selectedList?.let {
                        dataList.clear()
                        dataList.addAll(it)
                        // Perbarui data adapter menggunakan metode updateData yang sudah ada
                        additionalServiceAdapter.updateData(it) // Menggunakan 'it' yang sudah pasti ArrayList
                    }
                }
            }
        } else if (resultCode == RESULT_CANCELED) {
            val msg = when (requestCode) {
                pilihPelanggan -> "Batal Memilih Pelanggan"
                pilihLayanan -> "Batal Memilih Layanan"
                pilihLayananTambahan -> "Batal Memilih Layanan Tambahan"
                else -> "Aksi Dibatalkan"
            }
            Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
        }
    }
}