package com.example.laundry.adapter

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.example.laundry.R
import com.example.laundry.Data_model.ModelPelanggan
import com.example.laundry.Pelanggan.Tambah_pelangganActivity
import com.google.firebase.database.FirebaseDatabase

class AdapterDataPelanggan(
    private val listPelanggan: ArrayList<ModelPelanggan>
) : RecyclerView.Adapter<AdapterDataPelanggan.ViewHolder>() {

    lateinit var appContext: Context

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.card_data_pelanggan, parent, false)
        appContext = parent.context
        return ViewHolder(view)
    }

    @SuppressLint("MissingInflatedId")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val pelanggan = listPelanggan[position]

        // Set data ke tampilan kartu
        holder.tvCARD_PELANGGAN_ID.text = pelanggan.idPelanggan
        holder.tvCARD_PELANGGAN_NAMA.text = pelanggan.namaPelanggan
        holder.tvCARD_PELANGGAN_ALAMAT.text = pelanggan.alamatPelanggan
        holder.tvCARD_PELANGGAN_NOHP.text = pelanggan.noHPPelanggan
        holder.tvCARD_PELANGGAN_CABANG.text = pelanggan.cabangPelanggan

        // Klik tombol lihat
        holder.btnLihat.setOnClickListener {
            val dialogView = LayoutInflater.from(holder.itemView.context)
                .inflate(R.layout.activity_dialog_mod_pelanggan, null)

            val dialogBuilder = AlertDialog.Builder(holder.itemView.context)
                .setView(dialogView)
                .setCancelable(true)

            val alertDialog = dialogBuilder.create()

            // Temukan semua TextView di dialog
            val tvId = dialogView.findViewById<TextView>(R.id.tvDIALOG_PELANGGAN_ID)
            val tvNama = dialogView.findViewById<TextView>(R.id.tvDIALOG_PELANGGAN_NAMA)
            val tvAlamat = dialogView.findViewById<TextView>(R.id.tvDIALOG_PELANGGAN_ALAMAT)
            val tvNoHP = dialogView.findViewById<TextView>(R.id.tvDIALOG_PELANGGAN_NOHP)
            val tvCabang = dialogView.findViewById<TextView>(R.id.tvDIALOG_PELANGGAN_CABANG)

            val btEdit = dialogView.findViewById<Button>(R.id.btDIALOG_MOD_PELANGGAN_Edit)
            val btHapus = dialogView.findViewById<Button>(R.id.btDIALOG_MOD_PELANGGAN_Hapus)

            // Cek null sebelum setText
            tvId?.text = pelanggan.idPelanggan
            tvNama?.text = pelanggan.namaPelanggan
            tvAlamat?.text = pelanggan.alamatPelanggan
            tvNoHP?.text = pelanggan.noHPPelanggan
            tvCabang?.text = pelanggan.cabangPelanggan // opsional

            btEdit?.setOnClickListener {
                val intent = Intent(holder.itemView.context, Tambah_pelangganActivity::class.java)
                intent.putExtra("idPelanggan", pelanggan.idPelanggan)
                intent.putExtra("namaPelanggan", pelanggan.namaPelanggan)
                intent.putExtra("alamatPelanggan", pelanggan.alamatPelanggan)
                intent.putExtra("noHpPelanggan", pelanggan.noHPPelanggan)
                intent.putExtra("idCabang", pelanggan.cabangPelanggan)
                holder.itemView.context.startActivity(intent)
                alertDialog.dismiss()
            }

            btHapus?.setOnClickListener {
                AlertDialog.Builder(holder.itemView.context)
                    .setTitle("Konfirmasi Hapus")
                    .setMessage("Yakin ingin menghapus data ini?")
                    .setPositiveButton("Ya") { _, _ ->
                        val dbRef = FirebaseDatabase.getInstance()
                            .getReference("Pelanggan")
                            .child(pelanggan.idPelanggan ?: "")

                        dbRef.removeValue().addOnSuccessListener {
                            listPelanggan.removeAt(position)
                            notifyItemRemoved(position)
                            notifyItemRangeChanged(position, listPelanggan.size)
                            Toast.makeText(holder.itemView.context, "Data berhasil dihapus", Toast.LENGTH_SHORT).show()
                            alertDialog.dismiss()
                        }.addOnFailureListener {
                            Toast.makeText(holder.itemView.context, "Gagal: ${it.message}", Toast.LENGTH_SHORT).show()
                        }
                    }
                    .setNegativeButton("Tidak") { dialog, _ ->
                        dialog.dismiss()
                    }
                    .show()
            }

            alertDialog.show()
        }
    }

    override fun getItemCount(): Int = listPelanggan.size

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvCARD_PELANGGAN_ID: TextView = itemView.findViewById(R.id.tvCARD_PELANGGAN_ID)
        val tvCARD_PELANGGAN_NAMA: TextView = itemView.findViewById(R.id.tvCARD_PELANGGAN_nama)
        val tvCARD_PELANGGAN_ALAMAT: TextView = itemView.findViewById(R.id.tvCARD_PELANGGAN_alamat)
        val tvCARD_PELANGGAN_NOHP: TextView = itemView.findViewById(R.id.tvCARD_PELANGGAN_nohp)
        val tvCARD_PELANGGAN_CABANG: TextView = itemView.findViewById(R.id.tvCARD_PELANGGAN_cabang)
        val btnHubungi: Button = itemView.findViewById(R.id.btn_hubungi)
        val btnLihat: Button = itemView.findViewById(R.id.btn_lihat)
    }
}