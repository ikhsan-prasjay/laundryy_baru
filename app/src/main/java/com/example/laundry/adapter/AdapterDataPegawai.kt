package com.example.laundry.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.example.laundry.R
import com.example.laundry.Data_model.ModelPegawai
import com.example.laundry.Tambah_PegawaiActivity2

class AdapterDataPegawai(
    private val listPegawai: ArrayList<ModelPegawai>
) : RecyclerView.Adapter<AdapterDataPegawai.ViewHolder>() {

    private lateinit var appContext: Context

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        appContext = parent.context
        val view = LayoutInflater.from(appContext)
            .inflate(R.layout.card_data_pegawai, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val pegawai = listPegawai[position]

        holder.tvCARD_PEGAWAI_ID.text = pegawai.idPegawai
        holder.tvCARD_PEGAWAI_NAMA.text = pegawai.namaPegawai
        holder.tvCARD_PEGAWAI_ALAMAT.text = pegawai.alamatPegawai
        holder.tvCARD_PEGAWAI_NOHP.text = pegawai.noHPPegawai

        holder.cvCARD_PEGAWAI.setOnClickListener {
            val intent = Intent(appContext, Tambah_PegawaiActivity2::class.java)
            intent.putExtra("judul", "Edit Pegawai")
            intent.putExtra("idPegawai", pegawai.idPegawai)
            intent.putExtra("namaPegawai", pegawai.namaPegawai)
            intent.putExtra("alamatPegawai", pegawai.alamatPegawai)
            intent.putExtra("noHpPegawai", pegawai.noHPPegawai)
            appContext.startActivity(intent)
        }

        holder.btnHubungi.setOnClickListener {
            // Tambahkan logika untuk menghubungi, seperti membuka dialer
            val dialIntent = Intent(Intent.ACTION_DIAL)
            dialIntent.data = android.net.Uri.parse("tel:${pegawai.noHPPegawai}")
            appContext.startActivity(dialIntent)
        }

        holder.btnLihat.setOnClickListener {
            // Contoh: tampilkan detail pegawai di log atau halaman baru
            // Bisa dikembangkan sesuai fitur
        }
    }

    override fun getItemCount(): Int = listPegawai.size

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvCARD_PEGAWAI_ID: TextView = itemView.findViewById(R.id.tvCARD_PEGAWAI_ID)
        val tvCARD_PEGAWAI_NAMA: TextView = itemView.findViewById(R.id.tvCARD_PEGAWAI_nama)
        val tvCARD_PEGAWAI_ALAMAT: TextView = itemView.findViewById(R.id.tvCARD_PEGAWAI_alamat)
        val tvCARD_PEGAWAI_NOHP: TextView = itemView.findViewById(R.id.tvCARD_PEGAWAI_nohp)
        val cvCARD_PEGAWAI: CardView = itemView.findViewById(R.id.CvPegawai) // <- Perhatikan ID di XML
        val btnHubungi: Button = itemView.findViewById(R.id.btn_hubungi)
        val btnLihat: Button = itemView.findViewById(R.id.btn_lihat)
    }
}
