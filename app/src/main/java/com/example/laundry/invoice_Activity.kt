package com.example.laundry

import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothSocket
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.laundry.Data_model.ModelTambahan
import com.example.laundry.R
import kotlinx.coroutines.*
import java.io.IOException
import java.io.OutputStream
import java.net.URLEncoder
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*

class invoice_Activity : AppCompatActivity() {

    // Deklarasi View
    private lateinit var tvBusinessName: TextView
    private lateinit var tvBranch: TextView
    private lateinit var tvTransactionId: TextView
    private lateinit var tvDate: TextView
    private lateinit var tvCustomer: TextView
    private lateinit var tvEmployee: TextView
    private lateinit var tvMainService: TextView
    private lateinit var tvMainServicePrice: TextView
    private lateinit var tvAdditionalServicesHeader: TextView
    private lateinit var rvAdditionalServices: RecyclerView
    private lateinit var tvSubtotalAdditional: TextView
    private lateinit var tvTotal: TextView
    private lateinit var btnWhatsapp: Button
    private lateinit var btnPrint: Button

    // Data yang akan ditampilkan
    private var namaPelanggan: String = ""
    private var nomorHp: String = ""
    private var namaLayanan: String = ""
    private var hargaLayanan: String = "0" // Harga layanan utama (String)
    private var totalHarga: Int = 0
    private var tambahanList: ArrayList<ModelTambahan> = ArrayList() // List untuk layanan tambahan
    private var noTransaksi: String = ""
    private var tanggalTransaksi: String = ""

    private val coroutineScope = CoroutineScope(Dispatchers.Main + SupervisorJob())

    // Konstanta untuk Bluetooth
    companion object {
        private const val REQUEST_BLUETOOTH_PERMISSION = 1001
        private const val REQUEST_ENABLE_BT = 1002
        private const val PRINTER_MAC_ADDRESS = "DC:0D:51:A7:FF:7A" // Ganti dengan MAC Address printer Anda
        private const val SPP_UUID = "00001101-0000-1000-8000-00805f9b34fb" // UUID standar untuk SPP
    }

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_invoice) // Mengatur layout XML

        setupWindowInsets()
        initViews() // Inisialisasi semua View
        extractIntentData() // Mengambil data dari Intent
        setupInvoiceData() // Mengatur data ke View
        setupClickListeners() // Mengatur listener untuk tombol
    }

    // Mengatur insets jendela untuk tampilan edge-to-edge
    private fun setupWindowInsets() {
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    // Inisialisasi semua View dari layout
    private fun initViews() {
        tvBusinessName = findViewById(R.id.tv_business_name)
        tvBranch = findViewById(R.id.tv_branch)
        tvTransactionId = findViewById(R.id.tv_transaction_id)
        tvDate = findViewById(R.id.tv_date)
        tvCustomer = findViewById(R.id.tv_customer)
        tvEmployee = findViewById(R.id.tv_employee)
        tvMainService = findViewById(R.id.tv_main_service)
        tvMainServicePrice = findViewById(R.id.tv_main_service_price)
        tvAdditionalServicesHeader = findViewById(R.id.tv_additional_services_header)
        rvAdditionalServices = findViewById(R.id.rv_additional_services)
        tvSubtotalAdditional = findViewById(R.id.tv_subtotal_additional)
        tvTotal = findViewById(R.id.tv_total)
        btnWhatsapp = findViewById(R.id.btn_whatsapp)
        btnPrint = findViewById(R.id.btn_print)
    }

    // Mengambil data yang dikirim melalui Intent dari Activity sebelumnya
    private fun extractIntentData() {
        namaPelanggan = intent.getStringExtra("nama_pelanggan") ?: ""
        nomorHp = intent.getStringExtra("nomor_hp") ?: ""
        namaLayanan = intent.getStringExtra("nama_layanan") ?: ""
        hargaLayanan = intent.getStringExtra("harga_layanan") ?: "0"
        totalHarga = intent.getIntExtra("total_harga", 0)

        // Mengambil list layanan tambahan (penting untuk RecyclerView)
        val serializableExtra = intent.getSerializableExtra("layanan_tambahan")
        tambahanList = try {
            @Suppress("UNCHECKED_CAST")
            serializableExtra as? ArrayList<ModelTambahan> ?: ArrayList()
        } catch (e: Exception) {
            e.printStackTrace()
            ArrayList()
        }

        noTransaksi = generateNoTransaksi()
        tanggalTransaksi = getCurrentDateTime()
    }

    // Mengatur data ke View yang sudah diinisialisasi
    @SuppressLint("SetTextI18n")
    private fun setupInvoiceData() {
        tvBusinessName.text = "Laundry"
        tvBranch.text = "Solo"
        tvTransactionId.text = noTransaksi
        tvDate.text = tanggalTransaksi
        tvCustomer.text = namaPelanggan
        tvEmployee.text = "Admin" // Atau ambil dari Intent jika ada data karyawan

        // --- Perbaikan di sini: Bersihkan string harga layanan utama sebelum konversi ---
        val cleanedHargaLayanan = hargaLayanan.replace(".", "").replace(",", "")
        val hargaLayananInt = cleanedHargaLayanan.toIntOrNull() ?: 0
        // --- Akhir perbaikan ---

        tvMainService.text = namaLayanan
        tvMainServicePrice.text = formatCurrency(hargaLayananInt)
        setupAdditionalServices() // Mengatur RecyclerView untuk layanan tambahan
        tvTotal.text = formatCurrency(totalHarga)
    }

    // Mengatur RecyclerView untuk menampilkan layanan tambahan
    private fun setupAdditionalServices() {
        if (tambahanList.isEmpty()) {
            // Sembunyikan header dan RecyclerView jika tidak ada layanan tambahan
            tvAdditionalServicesHeader.visibility = View.GONE
            rvAdditionalServices.visibility = View.GONE
            tvSubtotalAdditional.text = formatCurrency(0)
            return
        }

        tvAdditionalServicesHeader.visibility = View.VISIBLE
        rvAdditionalServices.layoutManager = LinearLayoutManager(this) // Mengatur LayoutManager
        val adapter = AdditionalServicesAdapter(tambahanList) // Membuat adapter
        rvAdditionalServices.adapter = adapter // Mengatur adapter ke RecyclerView
        rvAdditionalServices.visibility = View.VISIBLE

        // Menghitung subtotal dari layanan tambahan
        val subtotal = tambahanList.sumOf {
            // --- Perbaikan di sini: Bersihkan string harga tambahan sebelum konversi ---
            val cleanedHargaTambahan = it.hargaTambahan?.replace(".", "")?.replace(",", "")
            cleanedHargaTambahan?.toIntOrNull() ?: 0
            // --- Akhir perbaikan ---
        }
        tvSubtotalAdditional.text = formatCurrency(subtotal)
    }

    // Mengatur listener untuk tombol-tombol
    private fun setupClickListeners() {
        btnWhatsapp.setOnClickListener {
            shareToWhatsApp() // Memanggil fungsi berbagi ke WhatsApp
        }

        btnPrint.setOnClickListener {
            printReceipt() // Memanggil fungsi cetak struk
        }
    }

    // Fungsi helper untuk memformat angka menjadi mata uang Rupiah
    private fun formatCurrency(amount: Int): String {
        val format = NumberFormat.getCurrencyInstance(Locale("id", "ID"))
        return format.format(amount).replace("IDR", "Rp")
    }

    // Membangun pesan WhatsApp dan membukanya melalui Intent
    private fun shareToWhatsApp() {
        val message = """
            Halo $namaPelanggan 👋,

            🔖 * LAUNDRY - SOLO*

            🆔 *ID Transaksi:* $noTransaksi
            📅 *Tanggal:* $tanggalTransaksi
            👤 *Pelanggan:* $namaPelanggan

            🧺 *Layanan Utama:* $namaLayanan
            💰 *Total Bayar:* ${formatCurrency(totalHarga)}

            🙏 Terima kasih telah mempercayakan cucian Anda kepada kami.
            Kami akan memberikan pelayanan terbaik untuk Anda!

            📍  Laundry - Cabang Solo
        """.trimIndent()

        val phoneNumber = if (nomorHp.startsWith("0")) "62${nomorHp.substring(1)}" else nomorHp
        val encodedMessage = URLEncoder.encode(message, "UTF-8")
        val url = "https://wa.me/$phoneNumber?text=$encodedMessage"

        try {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
            startActivity(intent)
            showToast("Pesan WhatsApp berhasil dikirim!")
        } catch (e: Exception) {
            showToast("Gagal membuka WhatsApp")
            e.printStackTrace()
        }
    }

    // Membuat nomor transaksi unik
    private fun generateNoTransaksi(): String {
        val timestamp = System.currentTimeMillis()
        return "TRX${timestamp.toString().takeLast(8)}"
    }

    // Mendapatkan tanggal dan waktu saat ini
    private fun getCurrentDateTime(): String {
        val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        return sdf.format(Date())
    }

    // Memulai proses pencetakan struk
    private fun printReceipt() {
        if (checkBluetoothPermissions()) {
            connectAndPrint(PRINTER_MAC_ADDRESS)
        } else {
            requestBluetoothPermissions()
        }
    }

    // Memeriksa izin Bluetooth
    private fun checkBluetoothPermissions(): Boolean {
        return if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {
            ContextCompat.checkSelfPermission(this, android.Manifest.permission.BLUETOOTH_CONNECT) == PackageManager.PERMISSION_GRANTED
        } else {
            val bluetoothPermission = ContextCompat.checkSelfPermission(this, android.Manifest.permission.BLUETOOTH) == PackageManager.PERMISSION_GRANTED
            val bluetoothAdminPermission = ContextCompat.checkSelfPermission(this, android.Manifest.permission.BLUETOOTH_ADMIN) == PackageManager.PERMISSION_GRANTED
            bluetoothPermission && bluetoothAdminPermission
        }
    }

    // Meminta izin Bluetooth
    private fun requestBluetoothPermissions() {
        val permissions = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {
            arrayOf(android.Manifest.permission.BLUETOOTH_CONNECT, android.Manifest.permission.BLUETOOTH_SCAN)
        } else {
            arrayOf(android.Manifest.permission.BLUETOOTH, android.Manifest.permission.BLUETOOTH_ADMIN, android.Manifest.permission.ACCESS_FINE_LOCATION)
        }
        ActivityCompat.requestPermissions(this, permissions, REQUEST_BLUETOOTH_PERMISSION)
    }

    // Menghubungkan ke printer Bluetooth dan mengirim data
    private fun connectAndPrint(macAddress: String) {
        val bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
        if (bluetoothAdapter == null) {
            showToast("Bluetooth tidak didukung pada perangkat ini")
            return
        }
        if (!bluetoothAdapter.isEnabled) {
            showToast("Bluetooth tidak aktif. Silakan aktifkan Bluetooth")
            return
        }
        if (!checkBluetoothPermissions()) {
            showToast("Permission Bluetooth tidak tersedia")
            return
        }

        coroutineScope.launch {
            try {
                val result = withContext(Dispatchers.IO) {
                    printToBluetoothDevice(bluetoothAdapter, macAddress)
                }
                if (result) {
                    showToast("Struk berhasil dicetak!")
                } else {
                    showToast("Gagal mencetak struk")
                }
            } catch (e: Exception) {
                showToast("Error: ${e.message}")
                e.printStackTrace()
            }
        }
    }

    // Fungsi suspend untuk mencetak ke perangkat Bluetooth (berjalan di background thread)
    private suspend fun printToBluetoothDevice(bluetoothAdapter: BluetoothAdapter, macAddress: String): Boolean {
        var socket: BluetoothSocket? = null
        var outputStream: OutputStream? = null
        return try {
            val device: BluetoothDevice = bluetoothAdapter.getRemoteDevice(macAddress)
            val uuid = UUID.fromString(SPP_UUID)
            socket = device.createRfcommSocketToServiceRecord(uuid)
            socket.connect()
            outputStream = socket.outputStream
            val receiptData = generateReceiptData()
            outputStream.write(receiptData.toByteArray())
            outputStream.flush()
            true
        } catch (e: IOException) {
            e.printStackTrace()
            false
        } catch (e: SecurityException) { // Menangani SecurityException jika izin tidak diberikan
            e.printStackTrace()
            false
        } finally {
            try {
                outputStream?.close()
                socket?.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    // Membuat string data struk untuk dicetak
    private fun generateReceiptData(): String {
        val ESC = "\u001B"
        val INIT = "$ESC@"
        val CENTER = "${ESC}a\u0001"
        val LEFT = "${ESC}a\u0000"
        val BOLD_ON = "${ESC}E\u0001"
        val BOLD_OFF = "${ESC}E\u0000"
        val CUT = "${ESC}i"

        // --- Perbaikan di sini: Bersihkan string harga layanan utama untuk cetak ---
        val cleanedHargaLayananForPrint = hargaLayanan.replace(".", "").replace(",", "")
        val hargaLayananIntForPrint = cleanedHargaLayananForPrint.toIntOrNull() ?: 0
        // --- Akhir perbaikan ---

        return buildString {
            append(INIT)
            append(CENTER).append(BOLD_ON).append("LAUNDRY\n")
            append("SOLO\n").append(BOLD_OFF)
            append("================================\n")
            append(LEFT)
            append("ID Transaksi: $noTransaksi\n")
            append("Tanggal: $tanggalTransaksi\n")
            append("Pelanggan: $namaPelanggan\n")
            append("Kasir: Admin\n")
            append("--------------------------------\n")
            append(BOLD_ON).append("LAYANAN UTAMA:\n").append(BOLD_OFF)
            append("$namaLayanan\n")
            append("${formatCurrency(hargaLayananIntForPrint)}\n") // Menggunakan harga yang sudah dibersihkan
            append("--------------------------------\n")
            if (tambahanList.isNotEmpty()) {
                append(BOLD_ON).append("LAYANAN TAMBAHAN:\n").append(BOLD_OFF)
                tambahanList.forEachIndexed { index, tambahan ->
                    // --- Perbaikan di sini: Bersihkan string harga tambahan untuk cetak ---
                    val cleanedHargaTambahanForPrint = tambahan.hargaTambahan?.replace(".", "")?.replace(",", "")
                    val harga = cleanedHargaTambahanForPrint?.toIntOrNull() ?: 0
                    // --- Akhir perbaikan ---
                    append("${index + 1}. ${tambahan.namaTambahan}\n")
                    append("   ${formatCurrency(harga)}\n")
                }
                append("--------------------------------\n")
            }
            append(BOLD_ON).append("TOTAL: ${formatCurrency(totalHarga)}\n").append(BOLD_OFF)
            append("================================\n").append(CENTER)
            append("Terima kasih atas kepercayaan\n")
            append("Anda kepada kami!\n")
            append("================================\n\n\n\n")
            append(CUT) // Perintah untuk memotong kertas
        }
    }

    // Menampilkan Toast message
    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    // Menangani hasil permintaan izin
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_BLUETOOTH_PERMISSION && grantResults.all { it == PackageManager.PERMISSION_GRANTED }) {
            printReceipt() // Jika izin diberikan, lanjutkan mencetak
        } else {
            showToast("Permission Bluetooth diperlukan untuk mencetak")
        }
    }

    // Membatalkan coroutine scope saat Activity dihancurkan untuk menghindari memory leaks
    override fun onDestroy() {
        super.onDestroy()
        coroutineScope.cancel()
    }

    // Adapter untuk RecyclerView yang menampilkan layanan tambahan
    inner class AdditionalServicesAdapter(private val additionalServices: List<ModelTambahan>) : RecyclerView.Adapter<AdditionalServicesAdapter.ViewHolder>() {

        inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            val tvNumber: TextView = itemView.findViewById(R.id.tv_number)
            val tvServiceName: TextView = itemView.findViewById(R.id.tv_service_name)
            val tvServicePrice: TextView = itemView.findViewById(R.id.tv_service_price)
        }

        override fun onCreateViewHolder(parent: android.view.ViewGroup, viewType: Int): ViewHolder {
            // Inflate layout untuk setiap item layanan tambahan
            val view = layoutInflater.inflate(R.layout.item_additional_service, parent, false)
            return ViewHolder(view)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val service = additionalServices[position]
            holder.tvNumber.text = (position + 1).toString()
            holder.tvServiceName.text = service.namaTambahan ?: "Unknown"
            // --- Perbaikan di sini: Bersihkan string harga tambahan untuk tampilan RecyclerView ---
            val cleanedServicePrice = service.hargaTambahan?.replace(".", "")?.replace(",", "")
            val harga = cleanedServicePrice?.toIntOrNull() ?: 0
            // --- Akhir perbaikan ---
            holder.tvServicePrice.text = formatCurrency(harga)
        }

        override fun getItemCount(): Int = additionalServices.size
    }
}