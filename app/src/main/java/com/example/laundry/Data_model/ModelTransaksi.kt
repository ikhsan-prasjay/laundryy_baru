package com.example.laundry.Data_model

class  ModelTransaksi(
    val idTransaksi: String = "",
    val idPelanggan: String = "",
    val namaPelanggan: String = "",
    val idLayanan: String = "",
    val namaLayanan: String = "",
    val hargaLayanan: String = "",
    val tambahan: List<ModelTambahan> = emptyList(),
    val tanggal: String = "",
    val idPegawai: String = "",
    val idCabang: String = ""
)