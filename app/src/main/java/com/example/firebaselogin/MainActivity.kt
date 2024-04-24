package com.example.firebaselogin

import android.content.Intent
import android.media.MediaPlayer.OnCompletionListener
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.Toast
import com.example.firebaselogin.LoginActivity
import com.example.firebaselogin.MyListData
import com.example.firebaselogin.R
import com.example.firebaselogin.data_teman
import com.example.firebaselogin.databinding.ActivityMainBinding
import com.example.firebaselogin.databinding.ActivityLoginBinding
import com.firebase.ui.auth.AuthUI
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class MainActivity : AppCompatActivity(), View.OnClickListener {
    private var auth:FirebaseAuth? = null
    private val RC_SIGN_IN = 1
    private lateinit var binding : ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.logout.setOnClickListener(this)
        binding.save.setOnClickListener(this)
        binding.showData.setOnClickListener(this)

        auth = FirebaseAuth.getInstance()
    }

    private fun isEmpty(s: String) : Boolean {
        return TextUtils.isEmpty(s)
    }

    override fun onClick(p0: View?) {
        when (p0?.getId()) {
            R.id.save -> {
                //Mendapatkan userID dari pengguna yang terautententikasi
                val getUserID = auth!!.currentUser!!.uid

                //mendapatkan Instance dari data base
                val database = FirebaseDatabase.getInstance()

                //Menyimpan data yang diimputkan user dalam variabel
                val getNama: String = binding.nama.getText().toString()
                val getAlamat: String = binding.alamat.getText().toString()
                val getNoHP: String = binding.noHp.getText().toString()

                //Mendapatkan Refrensi dari database
                val getReference: DatabaseReference
                getReference = database.reference

                //mengecek apakah ada data yg kosong
                if (isEmpty(getNama) || isEmpty(getAlamat) || isEmpty(getNoHP)) {
                    Toast.makeText(this@MainActivity, "Data tidak boleh ada yang kosong",
                        Toast.LENGTH_SHORT).show()
                } else {
                    //Jika teidak ada maka menyimpan ke database sesuai ID masing masing akun
                    getReference.child("Admin").child(getUserID).child("DataTeman").push()
                        .setValue(data_teman(getNama, getAlamat, getNoHP))
                        .addOnCompleteListener(this){
                            //bagian yg terjadi ketika user berhasil menyimpan data
                            binding.nama.setText("")
                            binding.alamat.setText("")
                            binding.noHp.setText("")
                            Toast.makeText(this@MainActivity, "Data Tersimpan",
                                Toast.LENGTH_SHORT).show()
                        }


                }


            }
            R.id.logout -> {
                AuthUI.getInstance().signOut(this)
                    .addOnCompleteListener(object : OnCompleteListener<Void> {
                        override fun onComplete(p0: Task<Void>) {
                            Toast.makeText(this@MainActivity, "Logout Berhasil",Toast.LENGTH_SHORT).show()
                            intent = Intent(applicationContext, LoginActivity::class.java)
                            startActivity(intent)
                            finish()
                        }
                    })
            }
            R.id.show_data -> {
                startActivity(Intent(this@MainActivity, MyListData::class.java))
            }
        }
    }
}