package com.example.laundry

import android.annotation.SuppressLint
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
import com.example.laundry.Data_model.ModelLayanan
import com.example.laundry.adapter.AdapterPilihLayanan
import com.google.firebase.database.*

class PilihLayananActivity : AppCompatActivity() {

    private val TAG = "PilihLayanan"
    private val database = FirebaseDatabase.getInstance()
    private val myRef = database.getReference("layanan")

    private lateinit var rvPilihLayanan: RecyclerView
    private lateinit var searchView: SearchView
    private lateinit var tvKosong: TextView

    private var idCabang: String = ""
    private lateinit var listLayanan: ArrayList<ModelLayanan>
    private lateinit var filteredList: ArrayList<ModelLayanan>
    private lateinit var adapter: AdapterPilihLayanan

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_pilih_layanan)

        Log.d(TAG, "Activity created")

        tvKosong = findViewById(R.id.tvKosong)
        rvPilihLayanan = findViewById(R.id.rvPILIH_LAYANAN)
        searchView = findViewById(R.id.searchView)

        listLayanan = ArrayList()
        filteredList = ArrayList()

        rvPilihLayanan.layoutManager = LinearLayoutManager(this)

        idCabang = intent.getStringExtra("idCabang") ?: ""
        Log.d(TAG, "idCabang: $idCabang")

        setupSearchView()
        getData()

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
            filteredList.addAll(listLayanan)
        } else {
            val searchText = query.lowercase().trim()
            for (layanan in listLayanan) {
                if (layanan.namalayanan?.lowercase()?.contains(searchText) == true ||
                    layanan.hargalayanan?.lowercase()?.contains(searchText) == true) {
                    filteredList.add(layanan)
                }
            }
        }

        updateRecyclerView()
    }

    private fun updateRecyclerView() {
        Log.d(TAG, "Updating RecyclerView with ${filteredList.size} items")

        if (filteredList.isEmpty()) {
            tvKosong.visibility = View.VISIBLE
            tvKosong.text = "Tidak ada layanan yang cocok"
        } else {
            tvKosong.visibility = View.GONE
        }

        adapter = AdapterPilihLayanan(filteredList)
        rvPilihLayanan.adapter = adapter
    }

    private fun getData() {
        Log.d(TAG, "getData() called")

        val query = if (idCabang.isEmpty()) {
            myRef.limitToLast(100)
        } else {
            myRef.orderByChild("idCabang").equalTo(idCabang).limitToLast(100)
        }

        query.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                Log.d(TAG, "onDataChange: data exists: ${snapshot.exists()}, count: ${snapshot.childrenCount}")

                if (snapshot.exists()) {
                    listLayanan.clear()
                    for (snap in snapshot.children) {
                        try {
                            val layanan = snap.getValue(ModelLayanan::class.java)
                            layanan?.let {
                                listLayanan.add(it)
                                Log.d(TAG, "Added layanan: ${it.namalayanan}")
                            }
                        } catch (e: Exception) {
                            Log.e(TAG, "Error parsing layanan: ${e.message}")
                        }
                    }

                    filteredList.clear()
                    filteredList.addAll(listLayanan)

                    updateRecyclerView()
                } else {
                    Log.d(TAG, "No data found")
                    tvKosong.visibility = View.VISIBLE
                    tvKosong.text = "Data layanan tidak ditemukan"
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e(TAG, "Database error: ${error.message}")
                tvKosong.visibility = View.VISIBLE
                tvKosong.text = "Error: ${error.message}"
                Toast.makeText(this@PilihLayananActivity, "Error: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
}