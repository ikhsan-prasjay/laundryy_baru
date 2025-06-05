// app/src/main/java/com/example/laundry/Data_Laporan_Activity.kt
package com.example.laundry

import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.laundry.Data_model.ModelTransaksi // Assuming ModelTransaksi is suitable
import com.example.laundry.adapter.AdapterDataLaporan // We will create this adapter
import com.google.firebase.database.*

class Data_laporan_Activity : AppCompatActivity() {

    private lateinit var rvLaporanTransaksi: RecyclerView
    private lateinit var laporanList: ArrayList<ModelTransaksi>
    private lateinit var adapter: AdapterDataLaporan // Declare the adapter
    private val database = FirebaseDatabase.getInstance()
    private val myRef = database.getReference("transaksi") // Reference to your transactions in Firebase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_data_laporan) // Create this layout file

        initViews()
        setupRecyclerView()
        fetchTransactionReports()

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun initViews() {
        rvLaporanTransaksi = findViewById(R.id.rvLaporanTransaksi)
        laporanList = ArrayList()
    }

    private fun setupRecyclerView() {
        val layoutManager = LinearLayoutManager(this)
        layoutManager.reverseLayout = true // Show latest transactions first
        layoutManager.stackFromEnd = true
        rvLaporanTransaksi.layoutManager = layoutManager
        rvLaporanTransaksi.setHasFixedSize(true)

        adapter = AdapterDataLaporan(laporanList) // Initialize adapter
        rvLaporanTransaksi.adapter = adapter
    }

    private fun fetchTransactionReports() {
        // Order by a relevant field, e.g., 'tanggal' to get recent transactions
        // Or filter by 'status' if you add a status field to ModelTransaksi
        val query = myRef.orderByChild("tanggal").limitToLast(100) // Fetch last 100 transactions

        query.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                laporanList.clear()
                if (snapshot.exists()) {
                    for (dataSnapshot in snapshot.children) {
                        val transaksi = dataSnapshot.getValue(ModelTransaksi::class.java)
                        transaksi?.let {
                            laporanList.add(it)
                        }
                    }
                }
                adapter.notifyDataSetChanged()
                if (laporanList.isEmpty()) {
                    Toast.makeText(this@Data_laporan_Activity, "Tidak ada data laporan.", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@Data_laporan_Activity, "Gagal memuat data laporan: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
}