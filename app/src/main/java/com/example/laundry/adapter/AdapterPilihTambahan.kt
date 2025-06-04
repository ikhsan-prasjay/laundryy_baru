package com.example.laundry.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.laundry.Data_model.ModelTambahan
import com.example.laundry.R
import java.text.NumberFormat
import java.util.*

class AdapterPilihTambahan(
    private val listTambahan: ArrayList<ModelTambahan>,
    private val onItemSelected: (ModelTambahan, Boolean) -> Unit // Lambda untuk callback ke Activity
) : RecyclerView.Adapter<AdapterPilihTambahan.ViewHolder>() {

    // Menyimpan daftar item yang saat ini dipilih
    private val selectedItems = hashSetOf<ModelTambahan>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.card_pilih_tambahan, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = listTambahan[position]
        holder.tvNamaTambahan.text = item.namaTambahan
        holder.tvHargaTambahan.text = formatCurrency(item.hargaTambahan?.toIntOrNull() ?: 0)
        holder.tvCabangTambahan.text = item.cabang

        // Set status CheckBox berdasarkan apakah item ada di selectedItems
        holder.cbPilihTambahan.isChecked = selectedItems.contains(item)

        // Set listener untuk CheckBox
        holder.cbPilihTambahan.setOnClickListener {
            if (holder.cbPilihTambahan.isChecked) {
                selectedItems.add(item)
            } else {
                selectedItems.remove(item)
            }
            onItemSelected(item, holder.cbPilihTambahan.isChecked) // Beri tahu Activity tentang perubahan pilihan
        }

        // Juga memungkinkan klik pada seluruh card untuk mengubah CheckBox
        holder.itemView.setOnClickListener {
            holder.cbPilihTambahan.isChecked = !holder.cbPilihTambahan.isChecked // Toggle status CheckBox
            holder.cbPilihTambahan.callOnClick() // Panggil listener CheckBox untuk memicu logika pemilihan
        }
    }

    override fun getItemCount(): Int {
        return listTambahan.size
    }

    // Metode untuk mendapatkan daftar item yang dipilih (dipanggil dari Activity)
    fun getSelectedItems(): ArrayList<ModelTambahan> {
        return ArrayList(selectedItems)
    }

    // Metode untuk mengatur item yang sudah dipilih sebelumnya (misal saat edit transaksi)
    fun setSelectedItems(items: List<ModelTambahan>) {
        selectedItems.clear()
        selectedItems.addAll(items)
        notifyDataSetChanged() // Perbarui tampilan RecyclerView
    }

    // Fungsi helper untuk format mata uang
    private fun formatCurrency(amount: Int): String {
        val format = NumberFormat.getCurrencyInstance(Locale("id", "ID"))
        return format.format(amount).replace("IDR", "Rp")
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val cbPilihTambahan: CheckBox = itemView.findViewById(R.id.cbPilihTambahan)
        val tvNamaTambahan: TextView = itemView.findViewById(R.id.tvNamaTambahan)
        val tvHargaTambahan: TextView = itemView.findViewById(R.id.tvHargaTambahan)
        val tvCabangTambahan: TextView = itemView.findViewById(R.id.tvCabangTambahan)
    }
}