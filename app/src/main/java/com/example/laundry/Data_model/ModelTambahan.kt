package com.example.laundry.Data_model
import java.io.Serializable

class ModelTambahan (
    val idTambahan: String? = null,
    val namaTambahan: String? = null,
    val hargaTambahan: String? = null, // Pastikan ini String untuk konsistensi input
    val cabang: String? = null,
) : Serializable // Penting agar bisa dikirim melalui Intent