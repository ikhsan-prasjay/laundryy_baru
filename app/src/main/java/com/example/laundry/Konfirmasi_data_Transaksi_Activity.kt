package com.example.laundry

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.laundry.Data_model.ModelTambahan
import com.example.laundry.Data_model.ModelTransaksi // Import ModelTransaksi
import com.google.firebase.database.FirebaseDatabase // Import FirebaseDatabase
import java.io.Serializable
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*

class Konfirmasi_data_Transaksi_Activity : AppCompatActivity() {

    private lateinit var tvNamaPelanggan: TextView
    private lateinit var tvNoHP: TextView
    private lateinit var tvNamaLayanan: TextView
    private lateinit var tvHargaLayanan: TextView
    private lateinit var listLayananTambahan: ListView
    private lateinit var tvTotalBayar: TextView
    private lateinit var btnBatal: Button
    private lateinit var btnPembayaran: Button

    private var idPelanggan: String = ""
    private var namaPelanggan: String = ""
    private var nomorHp: String = ""
    private var idLayanan: String = ""
    private var namaLayanan: String = ""
    private var hargaLayanan: String = "0"
    private var totalHarga: Int = 0
    private var tambahanList = ArrayList<ModelTambahan>()
    private var idPegawai: String = ""
    private var idCabang: String = ""

    private val database = FirebaseDatabase.getInstance()
    private val transactionsRef = database.getReference("transaksi")

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_konfirmasi_data_transaksi)

        initViews()
        extractIntentData()
        setupDisplayData()
        setupClickListeners()
    }

    private fun initViews() {
        // Explicitly cast to TextView to match the variable type
        tvNamaPelanggan = findViewById(R.id.tvNamaPelangganKonfirmasi)
        tvNoHP = findViewById(R.id.tvNoHPKonfimasi)
        tvNamaLayanan = findViewById(R.id.tvNamaLayananKonfimasi)
        tvHargaLayanan = findViewById(R.id.tvHargaLayananKonfimasi)
        listLayananTambahan = findViewById(R.id.listLayananTambahanKonfimasi)
        tvTotalBayar = findViewById(R.id.tvTotalHarga)
        btnBatal = findViewById(R.id.btnBatal)
        btnPembayaran = findViewById(R.id.btnPembayaran)
    }

    private fun extractIntentData() {
        namaPelanggan = intent.getStringExtra("nama_pelanggan") ?: ""
        nomorHp = intent.getStringExtra("nomor_hp") ?: ""
        namaLayanan = intent.getStringExtra("nama_layanan") ?: ""
        hargaLayanan = intent.getStringExtra("harga_layanan") ?: "0"

        idPelanggan = intent.getStringExtra("idPelanggan") ?: ""
        idLayanan = intent.getStringExtra("idLayanan") ?: ""
        idPegawai = intent.getStringExtra("idPegawai") ?: ""
        idCabang = intent.getStringExtra("idCabang") ?: ""

        val serializableExtra = intent.getSerializableExtra("layanan_tambahan")
        @Suppress("UNCHECKED_CAST")
        tambahanList = try {
            serializableExtra as? ArrayList<ModelTambahan> ?: ArrayList()
        } catch (e: Exception) {
            e.printStackTrace()
            ArrayList()
        }

        val hargaLayananInt = hargaLayanan.replace(".", "").replace(",", "").toIntOrNull() ?: 0
        totalHarga = hargaLayananInt
        for (tambahan in tambahanList) {
            val cleanedHargaTambahanString = tambahan.hargaTambahan?.replace(".", "")?.replace(",", "")
            val hargaTambahan = cleanedHargaTambahanString?.toIntOrNull() ?: 0
            totalHarga += hargaTambahan
        }
    }

    @SuppressLint("SetTextI18n")
    private fun setupDisplayData() {
        tvNamaPelanggan.text = "Nama Pelanggan : $namaPelanggan"
        tvNoHP.text = "No HP : $nomorHp"
        tvNamaLayanan.text = "Nama Layanan : $namaLayanan"
        tvHargaLayanan.text = "Harga : ${formatCurrency(hargaLayanan.replace(".", "").replace(",", "").toIntOrNull() ?: 0)}"

        val tambahanStrings = ArrayList<String>()
        for (tambahan in tambahanList) {
            val nama = tambahan.namaTambahan ?: "Unknown"
            val cleanedHargaString = tambahan.hargaTambahan?.replace(".", "")?.replace(",", "")
            val harga = cleanedHargaString?.toIntOrNull() ?: 0
            tambahanStrings.add("$nama - ${formatCurrency(harga)}")
        }

        val adapter = ArrayAdapter(
            this,
            android.R.layout.simple_list_item_1,
            tambahanStrings
        )
        listLayananTambahan.adapter = adapter
        tvTotalBayar.text = formatCurrency(totalHarga)
    }

    private fun setupClickListeners() {
        btnBatal.setOnClickListener {
            finish()
        }

        btnPembayaran.setOnClickListener {
            showPaymentMethodDialog()
        }
    }

    private fun showPaymentMethodDialog() {
        val dialogView = layoutInflater.inflate(R.layout.dialogmod_pembayaran, null)

        val dialog = android.app.AlertDialog.Builder(this)
            .setView(dialogView)
            .setCancelable(true)
            .create()

        val metodeList = listOf(
            Pair("Bayar Nanti", R.id.card_bayarnanti),
            Pair("Tunai", R.id.cardTunai),
            Pair("QRIS", R.id.cardQris),
            Pair("DANA", R.id.CardDana),
            Pair("GoPay", R.id.CardGopay),
            Pair("OVO", R.id.CardOvo)
        )

        for ((namaMetode, idCard) in metodeList) {
            val card = dialogView.findViewById<androidx.cardview.widget.CardView>(idCard)
            card?.setOnClickListener {
                Toast.makeText(this, "Metode dipilih: $namaMetode", Toast.LENGTH_SHORT).show()

                val status: String = if (namaMetode == "Bayar Nanti") {
                    "Belum Dibayar"
                } else {
                    "Dibayar"
                }

                saveTransactionToFirebase(namaMetode, status)
                navigateToInvoiceActivity(namaMetode)
                dialog.dismiss()
                finish()
            }
        }

        val btnBatalDialog = dialogView.findViewById<TextView>(R.id.tvbatal)
        btnBatalDialog?.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }

    private fun saveTransactionToFirebase(metodePembayaran: String, statusTransaksi: String) {
        val transactionId = transactionsRef.push().key ?: UUID.randomUUID().toString()
        val currentDateTime = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date())

        val transaction = ModelTransaksi(
            idTransaksi = transactionId,
            idPelanggan = idPelanggan,
            namaPelanggan = namaPelanggan,
            idLayanan = idLayanan,
            namaLayanan = namaLayanan,
            hargaLayanan = hargaLayanan.replace(".", "").replace(",", ""),
            tambahan = tambahanList,
            tanggal = currentDateTime,
            idPegawai = idPegawai,
            idCabang = idCabang,
            statusTransaksi = statusTransaksi
        )

        transactionsRef.child(transactionId).setValue(transaction)
            .addOnSuccessListener {
                Toast.makeText(this, "Transaksi berhasil disimpan!", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Gagal menyimpan transaksi: ${it.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun navigateToInvoiceActivity(metodePembayaran: String) {
        val invoiceIntent = Intent(this, invoice_Activity::class.java)
        invoiceIntent.putExtra("nama_pelanggan", namaPelanggan)
        invoiceIntent.putExtra("nomor_hp", nomorHp)
        invoiceIntent.putExtra("nama_layanan", namaLayanan)
        invoiceIntent.putExtra("harga_layanan", hargaLayanan)
        invoiceIntent.putExtra("total_harga", totalHarga)
        invoiceIntent.putExtra("metode_pembayaran", metodePembayaran)
        invoiceIntent.putExtra("layanan_tambahan", tambahanList as Serializable)
        startActivity(invoiceIntent)
    }

    private fun formatCurrency(amount: Int): String {
        val format = NumberFormat.getCurrencyInstance(Locale("id", "ID"))
        return format.format(amount).replace("IDR", "Rp")
    }
}