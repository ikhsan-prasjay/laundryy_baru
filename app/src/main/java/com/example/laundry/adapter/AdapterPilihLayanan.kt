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
import com.example.laundry.Data_model.ModelLayanan
import com.example.laundry.R
import com.google.firebase.database.DatabaseReference


class AdapterPilihLayanan(private val listLayanan: ArrayList<ModelLayanan>) :
    RecyclerView.Adapter<AdapterPilihLayanan.ViewHolder>() {

    private val TAG = "AdapterPilihLayanan"
    lateinit var appContext: Context
    lateinit var databaseReference: DatabaseReference

    override fun onCreateViewHolder(
        parent: ViewGroup, viewType: Int
    ): ViewHolder {
        Log.d(TAG, "onCreateViewHolder called")
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.card_pilih_layanan, parent, false)
        appContext = parent.context
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        Log.d(TAG, "onBindViewHolder for position: $position")
        val nomor = position + 1
        val item = listLayanan[position]

        try {
            holder.tvID.text = "[$nomor]"
            holder.tvNama.text = item.namalayanan ?: "Nama tidak tersedia"
            holder.tvHarga.text = "No HP : ${item.hargalayanan ?: "Tidak tersedia"}"

            Log.d(TAG, "Binding data: ${item.namalayanan} at position $position")

            holder.cvCARD.setOnClickListener {
                try {
                    val intent = Intent(appContext, Data_Transaksi_Activity::class.java)
                    intent.putExtra("idPelanggan", item.idlayanan)
                    intent.putExtra("nama", item.namalayanan)
                    intent.putExtra("harga", item.hargalayanan)
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
        val count = listLayanan.size
        Log.d(TAG, "getItemCount: $count")
        return count
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvID: TextView = itemView.findViewById(R.id.tvCARD_PIlih_layanan_ID)
        val tvNama: TextView = itemView.findViewById(R.id.tvCARD_Pilih_Layanan_nama)
        val tvHarga: TextView = itemView.findViewById(R.id.tvCARD_Pilih_Layanan_harga)
        val cvCARD: CardView = itemView.findViewById(R.id.card_pilih_layanan)
    }
}