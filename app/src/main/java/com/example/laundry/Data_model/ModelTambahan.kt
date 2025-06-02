package com.example.laundry.Data_model
import java.io.Serializable

class ModelTambahan (
    val idTambahan: String? = null,
    val namaTambahan: String? = null,
    val hargaTambahan: String? = null,
    val cabang: String? = null,
) : Serializable