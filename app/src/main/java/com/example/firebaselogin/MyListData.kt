package com.example.firebaselogin

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.firebaselogin.databinding.ActivityMyListDataBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class MyListData : AppCompatActivity() {
    private var recyclerView: RecyclerView? = null
    private var adapter: RecyclerView.Adapter<*>? = null
    private var layoutManager: RecyclerView.LayoutManager? = null

    val database = FirebaseDatabase.getInstance()
    private var dataTeman = ArrayList<data_teman>()
    private var auth: FirebaseAuth? = null

    private lateinit var binding: ActivityMyListDataBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMyListDataBinding.inflate(layoutInflater)
        setContentView(binding.root)
        recyclerView = findViewById(R.id.datalist)
        setSupportActionBar(findViewById(R.id.my_toolbar))
        supportActionBar!!.title = "Data Teman"
        auth = FirebaseAuth.getInstance()
        MyRecyclerView()
        GetData()
    }

    private fun GetData(){
        Toast.makeText(applicationContext, "Mohon Tunggu Sebentar...", Toast.LENGTH_LONG).show()
        val getUserID: String = auth?.getCurrentUser()?.getUid().toString()
        val getReference = database.getReference()
        getReference.child("Admin").child(getUserID).child("DataTeman")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    if (dataSnapshot.exists()) {
                        for (snapshot in dataSnapshot.children) {
                            val teman = snapshot.getValue(data_teman::class.java)
                            teman?.key = snapshot.key
                            dataTeman.add(teman!!)
                        }

                        adapter = RecyclerViewAdapter(dataTeman, this@MyListData)
                        recyclerView?.adapter = adapter
                        (adapter as RecyclerViewAdapter).notifyDataSetChanged()
                        Toast.makeText(applicationContext, "Data Berhasil Dimuat", Toast.LENGTH_LONG)
                            .show()
                    }
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    Toast.makeText(applicationContext, "Data gagal dimuat", Toast.LENGTH_LONG).show()
                    Log.e("MylistActivity", databaseError.details + " " + databaseError.message)
                }
            })
    }

    private fun MyRecyclerView(){
        layoutManager = LinearLayoutManager(this)
        recyclerView?.layoutManager = layoutManager
        recyclerView?.setHasFixedSize(true)

        val itemDecoration = DividerItemDecoration(applicationContext, DividerItemDecoration.VERTICAL)
        itemDecoration.setDrawable(ContextCompat.getDrawable(applicationContext, R.drawable.line)!!)
        recyclerView?.addItemDecoration(itemDecoration)
    }

}