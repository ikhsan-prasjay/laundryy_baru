package com.example.laundry.adapter

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

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.card_data_pelanggan, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val pelanggan = listPelanggan[position]

        holder.tvID.text = pelanggan.tv_title
        holder.tvNama.text = pelanggan.et_nama
        holder.tvAlamat.text = pelanggan.et_alamat
        holder.tvNoHP.text = pelanggan.et_no_hp

        holder.btnLihat.setOnClickListener {
            showDetailDialog(holder.itemView.context, pelanggan, position)
        }
    }

    private fun showDetailDialog(context: Context, pelanggan: ModelPelanggan, position: Int) {
        val dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_mod_pelanggan, null)
        val alertDialog = AlertDialog.Builder(context)
            .setView(dialogView)
            .setCancelable(true)
            .create()

        dialogView.findViewById<TextView>(R.id.tvDIALOG_PELANGGAN_ID)?.text = pelanggan.tv_title ?: "-"
        dialogView.findViewById<TextView>(R.id.tvDIALOG_PELANGGAN_NAMA)?.text = pelanggan.et_nama ?: "-"
        dialogView.findViewById<TextView>(R.id.tvDIALOG_PELANGGAN_ALAMAT)?.text = pelanggan.et_alamat ?: "-"
        dialogView.findViewById<TextView>(R.id.tvDIALOG_PELANGGAN_NOHP)?.text = pelanggan.et_no_hp ?: "-"
        dialogView.findViewById<TextView>(R.id.tvDIALOG_PELANGGAN_CABANG)?.text = pelanggan.etCabang ?: "-"

        val btnEdit = dialogView.findViewById<Button>(R.id.btDIALOG_MOD_PELANGGAN_Edit)
        val btnHapus = dialogView.findViewById<Button>(R.id.btDIALOG_MOD_PELANGGAN_Hapus)

        btnEdit?.setOnClickListener {
            val intent = Intent(context, Tambah_pelangganActivity::class.java).apply {
                putExtra("tv_title", pelanggan.tv_title)
                putExtra("et_nama", pelanggan.et_nama)
                putExtra("et_alamat", pelanggan.et_alamat)
                putExtra("et_no_hp", pelanggan.et_no_hp)
                putExtra("idCabang", pelanggan.etCabang)
            }
            context.startActivity(intent)
            alertDialog.dismiss()
        }

        btnHapus?.setOnClickListener {
            AlertDialog.Builder(context)
                .setTitle("Konfirmasi Hapus")
                .setMessage("Yakin ingin menghapus data ini?")
                .setPositiveButton("Ya") { _, _ ->
                    val dbRef = FirebaseDatabase.getInstance()
                        .getReference("Pelanggan")
                        .child(pelanggan.tv_title ?: "")

                    dbRef.removeValue().addOnSuccessListener {
                        listPelanggan.removeAt(position)
                        notifyItemRemoved(position)
                        Toast.makeText(context, "Data berhasil dihapus", Toast.LENGTH_SHORT).show()
                        alertDialog.dismiss()
                    }.addOnFailureListener {
                        Toast.makeText(context, "Gagal menghapus data: ${it.message}", Toast.LENGTH_SHORT).show()
                    }
                }
                .setNegativeButton("Tidak") { dialog, _ ->
                    dialog.dismiss()
                }
                .show()
        }

        alertDialog.show()
    }

    override fun getItemCount(): Int = listPelanggan.size

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvID: TextView = itemView.findViewById(R.id.tvCARD_PELANGGAN_ID)
        val tvNama: TextView = itemView.findViewById(R.id.tvCARD_PELANGGAN_nama)
        val tvAlamat: TextView = itemView.findViewById(R.id.tvCARD_PELANGGAN_alamat)
        val tvNoHP: TextView = itemView.findViewById(R.id.tvCARD_PELANGGAN_nohp)
        val cvCard: CardView = itemView.findViewById(R.id.Card_pelanggan)
        val btnHubungi: Button = itemView.findViewById(R.id.btn_hubungi)
        val btnLihat: Button = itemView.findViewById(R.id.btn_lihat)
    }
}
