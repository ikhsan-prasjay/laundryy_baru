package com.example.laundry
import android.os.Build
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.database.FirebaseDatabase
import com.example.laundry.R
import com.example.laundry.Data_model.ModelPegawai

class Tambah_PegawaiActivity2    : AppCompatActivity() {
    val database = FirebaseDatabase.getInstance()
    val myRef = database.getReference("pegawai")
    lateinit var tvJudul: TextView
    lateinit var etNama: EditText
    lateinit var etAlamat: EditText
    lateinit var etNoHP: EditText
    lateinit var etCabang: EditText
    lateinit var btSimpan: Button

    var pegawaiId: String? = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_tambah_pegawai2)
        init()
        val dataIntent = intent
        if (dataIntent != null) {
            pegawaiId = dataIntent.getStringExtra("idPegawai")
            val nama = dataIntent.getStringExtra("namaPegawai")
            val alamat = dataIntent.getStringExtra("alamatPegawai")
            val noHp = dataIntent.getStringExtra("noHpPegawai")
            val cabang = dataIntent.getStringExtra("idCabang")


            if (pegawaiId != null) {
                tvJudul.text = "Edit Data Pegawai"
                btSimpan.text = "Update"
                etNama.setText(nama)
                etAlamat.setText(alamat)
                etNoHP.setText(noHp)
                etCabang.setText(cabang)
            }
        }
        btSimpan.setOnClickListener{
            cekValidasi()
        }
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.tambah_pegawai)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    fun init(){
        tvJudul = findViewById(R.id.tv_title_pegawai)
        etNama = findViewById(R.id.et_nama_pegawai)
        etAlamat = findViewById(R.id.et_alamat_pegawai)
        etNoHP = findViewById(R.id.et_no_hp_pegawai)
        etCabang = findViewById(R.id.et_Cabang_pegawai)
        btSimpan = findViewById(R.id.btn_simpan_pegawai)
    }

    fun cekValidasi() {
        val nama = etNama.text.toString()
        val alamat = etAlamat.text.toString()
        val noHp = etNoHP.text.toString()
        val cabang = etCabang.text.toString()

        if (nama.isEmpty()) {
            etNama.error = this.getString(R.string.validasi_nama_pegawai)
            Toast.makeText(this, this.getString(R.string.validasi_nama_pegawai),Toast.LENGTH_SHORT).show()
            etNama.requestFocus()
            return
        }
        if (alamat.isEmpty()) {
            etAlamat.error = this.getString(R.string.validasi_alamat_pegawai)
            Toast.makeText(this, this.getString(R.string.validasi_alamat_pegawai),Toast.LENGTH_SHORT).show()
            etAlamat.requestFocus()
            return
        }
        if (noHp.isEmpty()) {
            etNoHP.error = this.getString(R.string.validasi_nohp_pegawai)
            Toast.makeText(this, this.getString(R.string.validasi_nohp_pegawai),Toast.LENGTH_SHORT).show()
            etNoHP.requestFocus()
            return
        }
        if (cabang.isEmpty()) {
            etCabang.error = this.getString(R.string.validasi_cabang_pelanggan)
            Toast.makeText(this, this.getString(R.string.validasi_cabang_pelanggan),Toast.LENGTH_SHORT).show()
            etCabang.requestFocus()
            return
        }
        if (pegawaiId == null) {
            simpan()
        } else {
            update()
        }
    }

    fun simpan() {
        val pegawaiBaru = myRef.push()
        val pegawaiid = pegawaiBaru.key
        val data = ModelPegawai(
            pegawaiid.toString(),
            etNama.text.toString(),
            etAlamat.text.toString(),
            etNoHP.text.toString(),
            etCabang.text.toString(),
        )
        pegawaiBaru.setValue(data)
            .addOnSuccessListener {
                Toast.makeText(
                    this,
                    this.getString(R.string.berhasil_tambah_pegawai),
                    Toast.LENGTH_SHORT
                )
                finish()
            }
            .addOnFailureListener {
                Toast.makeText(
                    this,
                    this.getString(R.string.pegawai_tambah_gagal),
                    Toast.LENGTH_SHORT
                )
            }
    }

    fun update() {
        val dataUpdate = mapOf(
            "namaPegawai" to etNama.text.toString(),
            "alamatPegawai" to etAlamat.text.toString(),
            "noHPPegawai" to etNoHP.text.toString(),
            "cabangPegawai" to etCabang.text.toString()
        )

        myRef.child(pegawaiId ?: "").updateChildren(dataUpdate)
            .addOnSuccessListener {
                Toast.makeText(this, "Berhasil update data pelanggan", Toast.LENGTH_SHORT).show()
                finish()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Gagal update data pelanggan", Toast.LENGTH_SHORT).show()
            }
    }


}