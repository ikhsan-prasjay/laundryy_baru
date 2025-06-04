package com.example.laundry

import android.annotation.SuppressLint
import android.app.Activity // Import Activity untuk setResult
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.laundry.Data_model.ModelTambahan
import com.example.laundry.adapter.AdapterPilihTambahan
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.io.Serializable // Penting untuk mengirim ArrayList

class PilihLayananTambahanActivity : AppCompatActivity() {

    private val TAG = "PilihLayananTambahan"
    private val database = FirebaseDatabase.getInstance()
    private val myRef = database.getReference("tambahan")

    private lateinit var rvPilihLayananTambahan: RecyclerView
    private lateinit var searchView: SearchView
    private lateinit var tvKosong: TextView
    private lateinit var fabPilihLayananTambahanSelesai: FloatingActionButton

    private var idCabang: String = ""
    private lateinit var listLayananTambahan: ArrayList<ModelTambahan>
    private lateinit var filteredList: ArrayList<ModelTambahan>
    private lateinit var adapter: AdapterPilihTambahan
    private val selectedTambahanItems = ArrayList<ModelTambahan>() // Daftar layanan tambahan yang dipilih pengguna

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_pilih_layanan_tambahan)

        Log.d(TAG, "Activity created")

        tvKosong = findViewById(R.id.tvKosong)
        rvPilihLayananTambahan = findViewById(R.id.rvPILIH_TAMBAHAN)
        searchView = findViewById(R.id.searchView)
        fabPilihLayananTambahanSelesai = findViewById(R.id.fabPilihLayananTambahanSelesai)

        listLayananTambahan = ArrayList()
        filteredList = ArrayList()

        rvPilihLayananTambahan.layoutManager = LinearLayoutManager(this)

        idCabang = intent.getStringExtra("idCabang") ?: ""
        // Ambil daftar layanan tambahan yang mungkin sudah dipilih sebelumnya dari Intent
        val previouslySelected = intent.getSerializableExtra("previously_selected_tambahan") as? ArrayList<ModelTambahan>
        previouslySelected?.let {
            selectedTambahanItems.addAll(it) // Isi selectedTambahanItems dengan data sebelumnya
            Log.d(TAG, "Previously selected items loaded: ${it.size}")
        }


        Log.d(TAG, "idCabang: $idCabang")

        setupSearchView()
        setupFinishButton()
        getData() // Ambil data dari Firebase

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun setupSearchView() {
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean = false

            override fun onQueryTextChange(newText: String?): Boolean {
                filterList(newText)
                return true
            }
        })
    }

    private fun filterList(query: String?) {
        Log.d(TAG, "Filtering list with query: $query")
        filteredList.clear()

        if (query.isNullOrEmpty()) {
            filteredList.addAll(listLayananTambahan)
        } else {
            val searchText = query.lowercase().trim()
            for (tambahan in listLayananTambahan) {
                if (tambahan.namaTambahan?.lowercase()?.contains(searchText) == true ||
                    tambahan.hargaTambahan?.lowercase()?.contains(searchText) == true) {
                    filteredList.add(tambahan)
                }
            }
        }
        updateRecyclerView()
    }

    private fun updateRecyclerView() {
        Log.d(TAG, "Updating RecyclerView with ${filteredList.size} items")

        if (filteredList.isEmpty() && searchView.query.isNullOrEmpty()) {
            tvKosong.visibility = View.VISIBLE
            tvKosong.text = "Tidak ada layanan tambahan tersedia."
        } else if (filteredList.isEmpty() && !searchView.query.isNullOrEmpty()) {
            tvKosong.visibility = View.VISIBLE
            tvKosong.text = "Tidak ada tambahan yang cocok dengan pencarian."
        }
        else {
            tvKosong.visibility = View.GONE
        }

        // Inisialisasi adapter dengan listener untuk menangani perubahan pilihan
        adapter = AdapterPilihTambahan(filteredList) { item, isSelected ->
            if (isSelected) {
                if (!selectedTambahanItems.contains(item)) { // Pastikan tidak ada duplikat
                    selectedTambahanItems.add(item)
                }
            } else {
                selectedTambahanItems.remove(item)
            }
            Log.d(TAG, "Selected items count: ${selectedTambahanItems.size}")
        }
        // Set item yang sudah dipilih sebelumnya ke adapter agar CheckBox sesuai
        adapter.setSelectedItems(selectedTambahanItems)
        rvPilihLayananTambahan.adapter = adapter
    }

    private fun getData() {
        Log.d(TAG, "getData() called")

        // Query berdasarkan idCabang jika ada, atau ambil 100 data terakhir
        val query = if (idCabang.isEmpty()) {
            myRef.limitToLast(100)
        } else {
            // Perhatikan: query orderByChild("cabang") dan equalTo(idCabang)
            // memerlukan indeks di Firebase Database Rules jika belum ada.
            // Contoh Rule: "tambahan": { ".indexOn": ["cabang"] }
            myRef.orderByChild("cabang").equalTo(idCabang).limitToLast(100)
        }

        query.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                Log.d(TAG, "onDataChange: data exists: ${snapshot.exists()}, count: ${snapshot.childrenCount}")

                if (snapshot.exists()) {
                    listLayananTambahan.clear()
                    for (snap in snapshot.children) {
                        try {
                            val tambahan = snap.getValue(ModelTambahan::class.java)
                            tambahan?.let {
                                listLayananTambahan.add(it)
                                Log.d(TAG, "Added layanan tambahan: ${it.namaTambahan}")
                            }
                        } catch (e: Exception) {
                            Log.e(TAG, "Error parsing layanan tambahan: ${e.message}")
                        }
                    }

                    filteredList.clear()
                    filteredList.addAll(listLayananTambahan) // Tampilkan semua saat pertama kali dimuat

                    updateRecyclerView()
                } else {
                    Log.d(TAG, "No data found")
                    tvKosong.visibility = View.VISIBLE
                    tvKosong.text = "Data layanan tambahan tidak ditemukan."
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e(TAG, "Database error: ${error.message}")
                tvKosong.visibility = View.VISIBLE
                tvKosong.text = "Error: ${error.message}"
                Toast.makeText(this@Pilih_Layanan_Tambahan_Activity, "Error: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    // Menyiapkan tombol "Selesai" untuk mengembalikan data ke Activity pemanggil
    private fun setupFinishButton() {
        fabPilihLayananTambahanSelesai.setOnClickListener {
            val resultIntent = Intent()
            // Kirim kembali daftar layanan tambahan yang dipilih
            resultIntent.putExtra("selected_tambahan_list", selectedTambahanItems as Serializable)
            setResult(Activity.RESULT_OK, resultIntent) // Set result OK
            finish() // Tutup Activity ini
        }
    }
}