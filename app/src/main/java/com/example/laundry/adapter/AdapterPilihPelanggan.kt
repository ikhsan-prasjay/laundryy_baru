package com.example.laundry.adapter

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.example.laundry.Data_Transaksi_Activity
import com.example.laundry.R
import com.example.laundry.Data_model.ModelPelanggan
import com.google.firebase.database.DatabaseReference


class AdapterPilihPelanggan(private val listPelanggan: ArrayList<ModelPelanggan>) :
    RecyclerView.Adapter<AdapterPilihPelanggan.ViewHolder>() {

    private val TAG = "AdapterPilihPelanggan"
    lateinit var appContext: Context
    lateinit var databaseReference: DatabaseReference

    override fun onCreateViewHolder(
        parent: ViewGroup, viewType: Int
    ): ViewHolder {
        Log.d(TAG, "onCreateViewHolder called")
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.card_pilih_pelanggan, parent, false)
        appContext = parent.context
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        Log.d(TAG, "onBindViewHolder for position: $position")
        val nomor = position + 1
        val item = listPelanggan[position]

        try {
            holder.tvID.text = "[$nomor]"
            holder.tvNama.text = item.namaPelanggan ?: "Nama tidak tersedia"
            holder.tvAlamat.text = "Alamat : ${item.alamatPelanggan ?: "Tidak tersedia"}"
            holder.tvNoHP.text = "No HP : ${item.noHPPelanggan ?: "Tidak tersedia"}"

            Log.d(TAG, "Binding data: ${item.namaPelanggan} at position $position")

            holder.cvCARD.setOnClickListener {
                try {
                    val intent = Intent(appContext, Data_Transaksi_Activity::class.java)
                    intent.putExtra("idPelanggan", item.idPelanggan)
                    intent.putExtra("nama", item.namaPelanggan)
                    intent.putExtra("noHP", item.noHPPelanggan)
                    (appContext as Activity).setResult(Activity.RESULT_OK, intent)
                    (appContext as Activity).finish()
                } catch (e: Exception) {
                    Log.e(TAG, "Error in click listener: ${e.message}")
                    Toast.makeText(appContext, "Terjadi kesalahan", Toast.LENGTH_SHORT).show()
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error binding view holder: ${e.message}")
        }
    }

    override fun getItemCount(): Int {
        val count = listPelanggan.size
        Log.d(TAG, "getItemCount: $count")
        return count
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvID: TextView = itemView.findViewById(R.id.ID_pilih_pelanggan)
        val tvNama: TextView = itemView.findViewById(R.id.tvCARD_Pilih_PELANGGAN_nama)
        val tvAlamat: TextView = itemView.findViewById(R.id.tvCARD_PIlih_PELANGGAN_alamat)
        val tvNoHP: TextView = itemView.findViewById(R.id.tvCARD_Pilih_PELANGGAN_nohp)
        val cvCARD: CardView = itemView.findViewById(R.id.card_pilih_pelanggan)
    }
}