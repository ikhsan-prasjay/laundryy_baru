package com.example.laundry

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.laundry.Data_model.ModelCabang
import com.example.laundry.adapter.AdapterDataCabang
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class Data_Cabang_Activity : AppCompatActivity() {

    private val database = FirebaseDatabase.getInstance()
    private val myRef = database.getReference("cabang") // Referensi ke node "cabang" di Firebase

    private lateinit var rvDATA_CABANG: RecyclerView
    private lateinit var fabDATA_CABANG_Tambah: FloatingActionButton
    private lateinit var cabangList: ArrayList<ModelCabang>
    private lateinit var adapter: AdapterDataCabang

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_data_cabang)

        init()
        initRecyclerView()
        getData()

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        fabDATA_CABANG_Tambah.setOnClickListener {
            val intent = Intent(this, Tambah_Cabang_Activity::class.java)
            startActivity(intent)
        }
    }

    private fun init() {
        rvDATA_CABANG = findViewById(R.id.rvDATA_CABANG)
        fabDATA_CABANG_Tambah = findViewById(R.id.fabDATA_CABANG_Tambah)
        cabangList = ArrayList()
    }

    private fun initRecyclerView() {
        val layoutManager = LinearLayoutManager(this)
        layoutManager.reverseLayout = true // Menampilkan data terbaru di bagian bawah
        layoutManager.stackFromEnd = true
        rvDATA_CABANG.layoutManager = layoutManager
        rvDATA_CABANG.setHasFixedSize(true)

        // Inisialisasi adapter dengan lambda untuk onDeleteClick
        adapter = AdapterDataCabang(cabangList) { selectedCabang ->
            val idToDelete = selectedCabang.idCabang
            if (idToDelete != null) {
                AlertDialog.Builder(this)
                    .setTitle("Konfirmasi Hapus")
                    .setMessage("Apakah Anda yakin ingin menghapus cabang \"${selectedCabang.namaCabang}\"?")
                    .setPositiveButton("Hapus") { _, _ ->
                        myRef.child(idToDelete).removeValue()
                            .addOnSuccessListener {
                                Toast.makeText(
                                    this,
                                    "Data cabang berhasil dihapus",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                            .addOnFailureListener {
                                Toast.makeText(
                                    this,
                                    "Gagal menghapus data cabang",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                    }
                    .setNegativeButton("Batal", null)
                    .show()
            }
        }
        rvDATA_CABANG.adapter = adapter
    }

    private fun getData() {
        val query = myRef.orderByChild("idCabang").limitToLast(100) // Ambil 100 data terakhir
        query.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    cabangList.clear()
                    for (dataSnapshot in snapshot.children) {
                        val cabang = dataSnapshot.getValue(ModelCabang::class.java)
                        cabang?.let { cabangList.add(it) }
                    }
                    adapter.notifyDataSetChanged()
                } else {
                    Toast.makeText(this@Data_Cabang_Activity, "Tidak ada data cabang", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@Data_Cabang_Activity, "Data Gagal Dimuat: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
}