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
import com.example.laundry.Data_model.ModelTambahan
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.example.laundry.adapter.AdapterDataTambahan

class Data_Tambahan_Activity2 : AppCompatActivity() {
    val database = FirebaseDatabase.getInstance()
    val myRef = database.getReference("tambahan")
    lateinit var rvDATA_TAMBAHAN: RecyclerView
    lateinit var fabDATA_TAMBAHAN_TambahTambahan: FloatingActionButton
    lateinit var tambahanList: ArrayList<ModelTambahan>

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_data_tambahan2)

        init()

        val layoutManager = LinearLayoutManager(this)
        layoutManager.reverseLayout = true
        layoutManager.stackFromEnd = true
        rvDATA_TAMBAHAN.layoutManager = layoutManager
        rvDATA_TAMBAHAN.setHasFixedSize(true)

        tambahanList = arrayListOf<ModelTambahan>()
        getDate()

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        fabDATA_TAMBAHAN_TambahTambahan.setOnClickListener {
            val intent = Intent(this, Tambah_TambahanActivity::class.java)
            startActivity(intent)
        }
    }

    fun init() {
        rvDATA_TAMBAHAN = findViewById(R.id.rvDATA_TAMBAHAN)
        fabDATA_TAMBAHAN_TambahTambahan = findViewById(R.id.fabDATA_PENGGUNA_TambahTambahan)
    }

    fun getDate() {
        val query = myRef.orderByChild("idTambahan").limitToLast(100)
        query.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    tambahanList.clear()
                    for (dataSnapshot in snapshot.children) {
                        val tambahan = dataSnapshot.getValue(ModelTambahan::class.java)
                        if (tambahan != null) {
                            tambahanList.add(tambahan)
                        }
                    }

                    val adapter = AdapterDataTambahan(tambahanList) { selectedTambahan ->
                        val idToDelete = selectedTambahan.idTambahan
                        if (idToDelete != null) {
                            AlertDialog.Builder(this@Data_Tambahan_Activity2)
                                .setTitle("Konfirmasi Hapus")
                                .setMessage("Apakah Anda yakin ingin menghapus tambahan \"${selectedTambahan.namaTambahan}\"?")
                                .setPositiveButton("Hapus") { _, _ ->
                                    myRef.child(idToDelete).removeValue()
                                        .addOnSuccessListener {
                                            Toast.makeText(
                                                this@Data_Tambahan_Activity2,
                                                "Data berhasil dihapus",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        }
                                        .addOnFailureListener {
                                            Toast.makeText(
                                                this@Data_Tambahan_Activity2,
                                                "Gagal menghapus data",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        }
                                }
                                .setNegativeButton("Batal", null)
                                .show()
                        }
                    }


                    rvDATA_TAMBAHAN.adapter = adapter
                    adapter.notifyDataSetChanged()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@Data_Tambahan_Activity2, "Data Gagal Dimuat", Toast.LENGTH_SHORT).show()
            }
        })
    }

}