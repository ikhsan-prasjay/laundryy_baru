package com.example.laundry

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.laundry.Pelanggan.DataPelangganActivity

class MainActivity : AppCompatActivity() {

    // properti lateinit untuk view
    private lateinit var ivpelanggan: ImageView
    private lateinit var pegawai: CardView

    @SuppressLint("MissingInflatedId", "WrongViewCast")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        // inisialisasi view
        ivpelanggan = findViewById(R.id.ivpelanggan)
        pegawai = findViewById(R.id.pegawai)

        // event klik
        ivpelanggan.setOnClickListener {
            val intent = Intent(this, DataPelangganActivity::class.java)
            startActivity(intent)
        }

        pegawai.setOnClickListener {
            val intent = Intent(this, Data_Pegawai_Activity::class.java)
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
