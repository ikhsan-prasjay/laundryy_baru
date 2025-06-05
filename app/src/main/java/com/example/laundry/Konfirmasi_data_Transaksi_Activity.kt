package com.example.laundry

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.laundry.R
import com.example.laundry.Data_model.ModelTambahan
import java.io.Serializable
import java.text.NumberFormat
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

    private var tambahanList = ArrayList<ModelTambahan>()
    private var totalHarga = 0

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_konfirmasi_data_transaksi)

        // Initialize views
        initViews()

        // Get data from intent with safe handling
        val namaPelanggan = intent.getStringExtra("nama_pelanggan") ?: ""
        val nomorHp = intent.getStringExtra("nomor_hp") ?: ""
        val namaLayanan = intent.getStringExtra("nama_layanan") ?: ""
        // Harga layanan utama (String) - Pastikan ini juga dibersihkan jika mungkin ada format "X.XXX"
        val hargaLayananString = intent.getStringExtra("harga_layanan") ?: "0"
        val hargaLayananBersih = hargaLayananString.replace(".", "").replace(",", "")
        val hargaLayananInt = hargaLayananBersih.toIntOrNull() ?: 0

        // Safely retrieve the ArrayList with proper casting
        val serializableExtra = intent.getSerializableExtra("layanan_tambahan")

        // Then try to cast it safely
        @Suppress("UNCHECKED_CAST")
        tambahanList = try {
            serializableExtra as? ArrayList<ModelTambahan> ?: ArrayList()
        } catch (e: Exception) {
            e.printStackTrace()
            ArrayList()
        }

        // Set text views
        tvNamaPelanggan.text = namaPelanggan
        tvNoHP.text = nomorHp
        tvNamaLayanan.text = namaLayanan
        // Gunakan formatCurrency untuk harga layanan utama
        tvHargaLayanan.text = formatCurrency(hargaLayananInt)

        // Create a list of formatted strings for the ListView
        val tambahanStrings = ArrayList<String>()
        for (tambahan in tambahanList) {
            val nama = tambahan.namaTambahan ?: "Unknown"
            // --- Perbaikan di sini: Bersihkan string harga sebelum konversi ---
            val cleanedHargaString = tambahan.hargaTambahan?.replace(".", "")?.replace(",", "")
            val harga = cleanedHargaString?.toIntOrNull() ?: 0
            // --- Akhir perbaikan ---
            tambahanStrings.add("$nama - ${formatCurrency(harga)}")
        }

        // Set the adapter for ListView
        val adapter = ArrayAdapter(
            this,
            android.R.layout.simple_list_item_1, // Layout default Android untuk item list sederhana
            tambahanStrings
        )
        listLayananTambahan.adapter = adapter

        // Calculate total price safely
        try {
            totalHarga = hargaLayananInt // Sudah dibersihkan dan dikonversi di atas
            for (tambahan in tambahanList) {
                // --- Perbaikan di sini: Bersihkan string harga sebelum konversi untuk perhitungan total ---
                val cleanedHargaTambahanString = tambahan.hargaTambahan?.replace(".", "")?.replace(",", "")
                val hargaTambahan = cleanedHargaTambahanString?.toIntOrNull() ?: 0
                // --- Akhir perbaikan ---
                totalHarga += hargaTambahan
            }
        } catch (e: Exception) {
            e.printStackTrace()
            totalHarga = 0
            Toast.makeText(this, "Error calculating total price", Toast.LENGTH_SHORT).show()
        }

        // Gunakan formatCurrency untuk total harga yang akan dibayar
        tvTotalBayar.text = formatCurrency(totalHarga)

        // Set click listeners
        btnBatal.setOnClickListener {
            finish() // Menutup activity ini
        }

        btnPembayaran.setOnClickListener {
            val dialogView = layoutInflater.inflate(R.layout.dialogmod_pembayaran, null)

            val dialog = android.app.AlertDialog.Builder(this)
                .setView(dialogView)
                .setCancelable(true)
                .create()

            // Daftar metode pembayaran dan ID card-nya
            val metodeList = listOf(
                Pair("Bayar Nanti", R.id.card_bayarnanti),
                Pair("Tunai", R.id.cardTunai),
                Pair("QRIS", R.id.cardQris),
                Pair("DANA", R.id.CardDana),
                Pair("GoPay", R.id.CardGopay),
                Pair("OVO", R.id.CardOvo)
            )

            // Tangani klik masing-masing metode pembayaran
            for ((namaMetode, idCard) in metodeList) {
                val card = dialogView.findViewById<androidx.cardview.widget.CardView>(idCard)
                card?.setOnClickListener {
                    Toast.makeText(this, "Metode dipilih: $namaMetode", Toast.LENGTH_SHORT).show()

                    // Pindah ke InvoiceActivity dengan membawa data transaksi
                    val invoiceIntent = Intent(this, invoice_Activity::class.java)
                    invoiceIntent.putExtra("nama_pelanggan", namaPelanggan)
                    invoiceIntent.putExtra("nomor_hp", nomorHp)
                    invoiceIntent.putExtra("nama_layanan", namaLayanan)
                    invoiceIntent.putExtra("harga_layanan", hargaLayananString) // Kirim harga asli (String)
                    invoiceIntent.putExtra("total_harga", totalHarga) // Kirim total yang sudah dihitung (Int)
                    invoiceIntent.putExtra("metode_pembayaran", namaMetode)
                    invoiceIntent.putExtra("layanan_tambahan", tambahanList as Serializable)

                    startActivity(invoiceIntent)
                    dialog.dismiss() // Tutup dialog pembayaran
                    finish() // Menutup activity konfirmasi setelah menuju invoice
                }
            }

            // Tombol batal pada dialog pembayaran
            val btnBatalDialog = dialogView.findViewById<TextView>(R.id.tvbatal)
            btnBatalDialog?.setOnClickListener {
                dialog.dismiss()
            }

            dialog.show()
        }
    }

    // Fungsi untuk inisialisasi semua View
    private fun initViews() {
        tvNamaPelanggan = findViewById(R.id.tvNamaPelangganKonfirmasi)
        tvNoHP = findViewById(R.id.tvNoHPKonfimasi)
        tvNamaLayanan = findViewById(R.id.tvNamaLayananKonfimasi)
        tvHargaLayanan = findViewById(R.id.tvHargaLayananKonfimasi)
        listLayananTambahan = findViewById(R.id.listLayananTambahanKonfimasi)
        tvTotalBayar = findViewById(R.id.tvTotalHarga)
        btnBatal = findViewById(R.id.btnBatal)
        btnPembayaran = findViewById(R.id.btnPembayaran)
    }

    /**
     * Fungsi helper untuk memformat angka menjadi mata uang Rupiah.
     * Digunakan untuk konsistensi format di seluruh aplikasi.
     */
    private fun formatCurrency(amount: Int): String {
        val format = NumberFormat.getCurrencyInstance(Locale("id", "ID"))
        return format.format(amount).replace("IDR", "Rp")
    }
}