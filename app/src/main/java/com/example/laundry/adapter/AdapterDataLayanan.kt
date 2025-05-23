package com.example.laundry.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.laundry.Data_model.ModelLayanan
import com.example.laundry.R

class AdapterDataLayanan(
    private val listLayanan: ArrayList<ModelLayanan>) :
    RecyclerView.Adapter<AdapterDataLayanan.ViewHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.activity_card_data_layanan2, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = listLayanan[position]
        holder.tvCARD_LAYANAN_ID.text = item.idlayanan
        holder.tvCARD_LAYANAN_NAMA.text = item.namalayanan
        holder.tvCARD_LAYANAN_HARGA.text = item.hargalayanan
        holder.tvCARD_LAYANAN_cabang.text = item.cabanglayanan
        holder.cvCARD_LAYANAN.setOnClickListener {
        }
    }

    override fun getItemCount(): Int {
        return listLayanan.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val cvCARD_LAYANAN= itemView.findViewById<View>(R.id.cvCARD_LAYANAN)
        val tvCARD_LAYANAN_ID = itemView.findViewById<TextView>(R.id.tvCARD_LAYANAN_ID)
        val tvCARD_LAYANAN_NAMA = itemView.findViewById<TextView>(R.id.tvCARD_LAYANAN_nama)
        val tvCARD_LAYANAN_HARGA = itemView.findViewById<TextView>(R.id.tvCARD_LAYANAN_harga)
        val tvCARD_LAYANAN_cabang = itemView.findViewById<TextView>(R.id.tvCARD_LAYANAN_cabang)
    }
}