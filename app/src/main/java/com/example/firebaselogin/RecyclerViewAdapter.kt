package com.example.firebaselogin

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.LauncherActivity.ListItem
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class RecyclerViewAdapter (private val dataTeman: ArrayList<data_teman>, context: Context) :
    RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder>() {
    private val context: Context

    val database = FirebaseDatabase.getInstance()
    private var auth: FirebaseAuth? = null

    //view holder digunakan untuk menyimpan referensi dari view-view
    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val  Nama: TextView
        val  Alamat: TextView
        val  NoHP: TextView
        val ListItem: LinearLayout

        //menginisialisasi view yang terpasang pada layout Recycler View
        init {
            Nama = itemView.findViewById(R.id.namax)
            Alamat = itemView.findViewById(R.id.alamatx)
            NoHP = itemView.findViewById(R.id.no_hpx)
            ListItem = itemView.findViewById(R.id.list_item)
        }
    }

    //membuat view untuk menyimpan dan memasang layout yang digunakan pada Recycler View
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val V: View = LayoutInflater.from(parent.getContext()).inflate(
            R.layout.view_design, parent, false)
        return ViewHolder(V)
    }

    @SuppressLint("SetTextI18n")
    //mengambil nilai atau value pada Recycler View berdasarkan posisi tertentu
    override fun onBindViewHolder(holder: ViewHolder, @SuppressLint("RecyclerView") position: Int) {
        val Nama: String? = dataTeman.get(position).nama
        val Alamat: String? = dataTeman.get(position).alamat
        val NoHP: String? = dataTeman.get(position).no_hp

        //masukkan nilai atau value kedalam view
        holder.Nama.text = "Nama: $Nama"
        holder.Alamat.text = "Alamat: $Alamat"
        holder.NoHP.text = "NoHP: $NoHP"
        holder.ListItem.setOnLongClickListener { view ->
            val action = arrayOf("Update", "Delete")
            val alert: AlertDialog.Builder = AlertDialog.Builder(view.context)
            alert.setItems(action) { dialog, i ->
                when (i) {
                    0 -> {
                        //berpindah ke hal update data untuk ambil data pada listdata teman
                        val bundle = Bundle()
                        bundle.putString("dataNama", dataTeman[position].nama)
                        bundle.putString("dataAlamat", dataTeman[position].alamat)
                        bundle.putString("dataNoHP", dataTeman[position].no_hp)
                        bundle.putString("getPrimaryKey", dataTeman[position].key)
                        Log.e("RecyclerViewAdapter", "Navigating to UpdateData activity")
                        val intent = Intent(view.context.applicationContext, UpdateData::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                        intent.putExtras(bundle)
                        context.startActivity(intent)
                    }
                    1 -> {
                        deleteItem(position)
                    }

                }
            }
            alert.create()
            alert.show()
            true
        }
    }

    //menghitung ukuran atau jumlah data yang akan ditampilkan pdaa Recycler View
    override fun getItemCount(): Int {
        return dataTeman.size
    }

    fun deleteItem(position: Int) {
        val getUserID: String = auth?.getCurrentUser()?.getUid().toString()
        val getReference = database.getReference().child("Admin").child(getUserID).child("DataTeman")

        val deletedItem = dataTeman[position]
        getReference.child(deletedItem.key!!).removeValue()
        dataTeman.removeAt(position)
        notifyItemRemoved(position)
        notifyDataSetChanged()

        Toast.makeText(context, "Data ${deletedItem.nama} berhasil dihapus", Toast.LENGTH_SHORT).show()
    }


    init {
        this.context = context
        auth = FirebaseAuth.getInstance()
    }
}