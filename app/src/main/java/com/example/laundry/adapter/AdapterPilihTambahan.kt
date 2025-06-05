package com.example.laundry.adapter

import android.annotation.SuppressLint // Import yang ditambahkan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.example.laundry.Data_model.ModelTambahan
import com.example.laundry.R

class AdapterPilihTambahan(
    private var listTambahan: ArrayList<ModelTambahan>,
    private val onItemSelected: (ModelTambahan, Boolean) -> Unit // Callback untuk item yang dipilih
) : RecyclerView.Adapter<AdapterPilihTambahan.ViewHolder>() {

    private val selectedItems = mutableListOf<ModelTambahan>() // Daftar item yang dipilih

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.card_pilih_tambahan, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = listTambahan[position]
        val nomor = position + 1 // Nomor urut dimulai dari 1
        holder.tvID.text = "[$nomor]"
        holder.tvNama.text = item.namaTambahan ?: "Nama tidak tersedia"
        holder.tvHarga.text = "Harga : Rp. ${item.hargaTambahan ?: "0"}"

        // Set status CheckBox berdasarkan apakah item sudah dipilih sebelumnya
        holder.checkBox.isChecked = selectedItems.contains(item)

        // Listener untuk CheckBox
        holder.checkBox.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                if (!selectedItems.contains(item)) {
                    selectedItems.add(item)
                }
            } else {
                selectedItems.remove(item)
            }
            onItemSelected(item, isChecked) // Beri tahu Activity tentang perubahan pilihan
        }

        // Listener untuk CardView (jika ingin klik di card juga memilih/membatalkan pilihan)
        holder.cvCARD.setOnClickListener {
            holder.checkBox.isChecked = !holder.checkBox.isChecked
        }
    }

    override fun getItemCount(): Int {
        return listTambahan.size
    }

    // Fungsi untuk mengatur item yang sudah dipilih sebelumnya (saat membuka kembali activity)
    fun setSelectedItems(items: ArrayList<ModelTambahan>) {
        selectedItems.clear()
        selectedItems.addAll(items)
        notifyDataSetChanged() // Perbarui tampilan RecyclerView
    }

    @SuppressLint("NotifyDataSetChanged") // Anotasi yang diperbaiki
    fun updateData(newData: ArrayList<ModelTambahan>) { // This function expects ArrayList
        listTambahan.clear()
        listTambahan.addAll(newData)
        notifyDataSetChanged()
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val cvCARD: CardView = itemView.findViewById(R.id.cvCARD_PILIHTAMBAHAN)
        val tvID: TextView = itemView.findViewById(R.id.tvCARD_PILIHTAMBAHAN_ID)
        val tvNama: TextView = itemView.findViewById(R.id.tvCARD_PILIHTAMBAHAN_nama)
        val tvHarga: TextView = itemView.findViewById(R.id.tvCARD_PILIHTAMBAHAN_harga)
        val checkBox: CheckBox = itemView.findViewById(R.id.checkBox_pilih_tambahan) // ID baru untuk CheckBox
    }
}