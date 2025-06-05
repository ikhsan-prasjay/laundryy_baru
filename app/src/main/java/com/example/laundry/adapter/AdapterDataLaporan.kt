// app/src/main/java/com/example/laundry/adapter/AdapterDataLaporan.kt
package com.example.laundry.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.laundry.Data_model.ModelTambahan
import com.example.laundry.Data_model.ModelTransaksi
import com.example.laundry.R
import java.text.NumberFormat
import java.util.*

class AdapterDataLaporan(private val listTransaksi: ArrayList<ModelTransaksi>) :
    RecyclerView.Adapter<AdapterDataLaporan.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.card_item_laporan, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = listTransaksi[position]

        holder.tvCustomerName.text = item.namaPelanggan
        holder.tvStatus.text = item.statusTransaksi // Gunakan status dari ModelTransaksi

        // Atur warna latar belakang status berdasarkan nilai statusTransaksi
        when (item.statusTransaksi) {
            "Dibayar" -> {
                holder.tvStatus.setBackgroundTintList(ContextCompat.getColorStateList(holder.itemView.context, R.color.green_status))
                holder.tvStatus.setTextColor(ContextCompat.getColor(holder.itemView.context, android.R.color.white))
            }
            "Belum Dibayar" -> {
                holder.tvStatus.setBackgroundTintList(ContextCompat.getColorStateList(holder.itemView.context, R.color.red_status))
                holder.tvStatus.setTextColor(ContextCompat.getColor(holder.itemView.context, android.R.color.white))
            }
            "Diambil" -> {
                holder.tvStatus.setBackgroundTintList(ContextCompat.getColorStateList(holder.itemView.context, R.color.blue_status))
                holder.tvStatus.setTextColor(ContextCompat.getColor(holder.itemView.context, android.R.color.white))
            }
            else -> {
                // Default color if status is unknown
                holder.tvStatus.setBackgroundTintList(ContextCompat.getColorStateList(holder.itemView.context, R.color.gray_status))
                holder.tvStatus.setTextColor(ContextCompat.getColor(holder.itemView.context, android.R.color.black))
            }
        }


        val entryDate = item.tanggal.substringBefore(" ")
        holder.tvDateEntry.text = entryDate
        holder.tvPickupDate.text = item.tanggal // Akan disesuaikan jika ada tanggal diambil terpisah

        holder.tvServiceName.text = item.namaLayanan

        val additionalServicesCount = item.tambahan.size
        if (additionalServicesCount > 0) {
            holder.tvAdditionalServices.text = "+$additionalServicesCount Layanan Tambahan"
        } else {
            holder.tvAdditionalServices.text = "Tidak ada layanan tambahan"
        }

        holder.tvTotalPay.text = formatCurrency(calculateTotalPrice(item.hargaLayanan, item.tambahan))
    }

    override fun getItemCount(): Int {
        return listTransaksi.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvCustomerName: TextView = itemView.findViewById(R.id.tvReportCustomerName)
        val tvStatus: TextView = itemView.findViewById(R.id.tvReportStatus)
        val tvDateEntry: TextView = itemView.findViewById(R.id.tvReportDateEntry)
        val tvServiceName: TextView = itemView.findViewById(R.id.tvReportServiceName)
        val tvAdditionalServices: TextView = itemView.findViewById(R.id.tvReportAdditionalServices)
        val tvTotalPay: TextView = itemView.findViewById(R.id.tvReportTotalPay)
        val tvPickupDate: TextView = itemView.findViewById(R.id.tvReportPickupDate)
    }

    private fun formatCurrency(amount: Int): String {
        val format = NumberFormat.getCurrencyInstance(Locale("id", "ID"))
        return format.format(amount).replace("IDR", "Rp")
    }

    private fun calculateTotalPrice(mainServicePrice: String, additionalServices: List<ModelTambahan>): Int {
        val cleanedMainPrice = mainServicePrice.replace(".", "").replace(",", "")
        var total = cleanedMainPrice.toIntOrNull() ?: 0

        for (tambahan in additionalServices) {
            val cleanedHargaTambahan = tambahan.hargaTambahan?.replace(".", "")?.replace(",", "")
            total += cleanedHargaTambahan?.toIntOrNull() ?: 0
        }
        return total
    }
}