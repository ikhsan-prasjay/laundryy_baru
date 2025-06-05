// app/src/main/java/com/example/laundry/MainActivity.kt
package com.example.laundry

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.laundry.Pelanggan.DataPelangganActivity

class MainActivity : AppCompatActivity() {

    // properti lateinit untuk view
    private lateinit var ivpelanggan: ImageView
    private lateinit var pegawai: CardView
    lateinit var cvLayanan: CardView
    lateinit var cvtambahan: CardView
    lateinit var Ivtransaksi: ImageView
    lateinit var laporan: ImageView
    lateinit var cvCabang2: CardView // Tambahkan ini


    @SuppressLint("MissingInflatedId", "WrongViewCast")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        // inisialisasi view
        ivpelanggan = findViewById(R.id.ivpelanggan)
        pegawai = findViewById(R.id.pegawai)
        cvLayanan = findViewById(R.id.cvLayanan)
        cvtambahan = findViewById(R.id.cvtambahan)
        Ivtransaksi = findViewById(R.id.Ivtransaksi)
        laporan = findViewById(R.id.laporan)
        cvCabang2 = findViewById(R.id.cvCabang2) // Inisialisasi ini


        // event klik
        ivpelanggan.setOnClickListener {
            val intent = Intent(this, DataPelangganActivity::class.java)
            startActivity(intent)
        }

        pegawai.setOnClickListener {
            val intent = Intent(this, Data_Pegawai_Activity::class.java)
            startActivity(intent)
        }
        cvLayanan.setOnClickListener {
            val intent = Intent(this, DataLayananActivity2::class.java)
            startActivity(intent)
        }
        cvtambahan.setOnClickListener {
            val intent = Intent(this, Data_Tambahan_Activity2::class.java)
            startActivity(intent)
        }
        Ivtransaksi.setOnClickListener {
            val intent = Intent(this, Data_Transaksi_Activity::class.java)
            startActivity(intent)
        }
        laporan.setOnClickListener {
            val intent = Intent(this, Data_laporan_Activity::class.java)
            startActivity(intent)
        }
        cvCabang2.setOnClickListener { // Tambahkan listener ini
            val intent = Intent(this, Data_Cabang_Activity::class.java)
            startActivity(intent)
        }

        // handle insets
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
}