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
import androidx.recyclerview.widget.RecyclerView
import com.example.laundry.Data_model.ModelCabang
import com.example.laundry.R
import com.example.laundry.Tambah_Cabang_Activity
import com.google.firebase.database.FirebaseDatabase

class AdapterDataCabang(
    private val listCabang: ArrayList<ModelCabang>,
    private val onDeleteClick: (ModelCabang) -> Unit // Callback untuk menghapus
) : RecyclerView.Adapter<AdapterDataCabang.ViewHolder>() {

    private lateinit var appContext: Context

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.card_data_cabang, parent, false)
        appContext = parent.context
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = listCabang[position]
        holder.tvCARD_CABANG_ID.text = item.idCabang
        holder.tvCARD_CABANG_NAMA.text = item.namaCabang
        holder.tvCARD_CABANG_ALAMAT.text = item.alamatCabang
        holder.tvCARD_CABANG_NOHP.text = item.noHPCabang

        // Menambahkan OnClickListener untuk CardView (opsional, bisa untuk detail/edit)
        holder.cvCARD_CABANG.setOnClickListener {
            // Contoh: Intent ke Tambah_Cabang_Activity untuk mengedit data
            val intent = Intent(appContext, Tambah_Cabang_Activity::class.java)
            intent.putExtra("idCabang", item.idCabang)
            intent.putExtra("namaCabang", item.namaCabang)
            intent.putExtra("alamatCabang", item.alamatCabang)
            intent.putExtra("noHPCabang", item.noHPCabang)
            appContext.startActivity(intent)
        }

        // Menambahkan OnLongClickListener untuk menghapus data
        holder.cvCARD_CABANG.setOnLongClickListener {
            onDeleteClick(item)
            true
        }
    }

    override fun getItemCount(): Int {
        return listCabang.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val cvCARD_CABANG = itemView.findViewById<View>(R.id.Cvcabang)
        val tvCARD_CABANG_ID = itemView.findViewById<TextView>(R.id.tvCARD_CABANG_ID)
        val tvCARD_CABANG_NAMA = itemView.findViewById<TextView>(R.id.tvCARD_CABANG_nama)
        val tvCARD_CABANG_ALAMAT = itemView.findViewById<TextView>(R.id.tvCARD_CABANG_alamat)
        val tvCARD_CABANG_NOHP = itemView.findViewById<TextView>(R.id.tvCARD_CABANG_nohp)
    }
}