package com.example.laundry.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.view.menu.MenuView.ItemView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.example.laundry.R
import com.example.laundry.Data_model.ModelPelanggan
import org.w3c.dom.Text



class AdapterDataPelanggan(private val listPelanggan: ArrayList<ModelPelanggan>) :
    RecyclerView.Adapter<AdapterDataPelanggan.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.card_data_pelanggan, parent, false)
        return ViewHolder(view)
    }
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = listPelanggan[position]
        holder.tvID.text = item.tv_title
        holder.tvNama.text = item.et_nama
        holder.tvAlamat.text = item.et_alamat
        holder.tvNoHP.text = item.et_no_hp
        holder.tvTerdaftar.text = item.et_terdaftar
        holder.cvCARD.setOnClickListener(){
        }
        holder.btHubungi.setOnClickListener{
        }
        holder.btLihat.setOnClickListener{
        }
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val cvCARD = itemView.findViewById<CardView>(R.id.Card_pelanggan)
        val tvID = itemView.findViewById<TextView>(R.id.tvCARD_PELANGGAN_ID)
        val tvNama = itemView.findViewById<TextView>(R.id.tvCARD_PELANGGAN_nama)
        val tvAlamat = itemView.findViewById<TextView>(R.id.tvCARD_PELANGGAN_alamat)
        val tvNoHP = itemView.findViewById<TextView>(R.id.tvCARD_PELANGGAN_nohp)
        val tvTerdaftar = itemView.findViewById<TextView>(R.id.tvCARD_PELANGGAN_terdaftar)
        val btHubungi = itemView.findViewById<Button>(R.id.btn_hubungi)
        val btLihat = itemView.findViewById<Button>(R.id.btn_lihat)
    }

    override fun getItemCount(): Int {
        return listPelanggan.size
    }
}