package com.skdevstudio.encrypto

import android.content.Intent
import android.database.Cursor
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.skdevstudio.encrypto.databinding.ActivityShowIdsBinding


class ShowIDs : AppCompatActivity() {

    private lateinit var binding: ActivityShowIdsBinding
    private lateinit var accountType : ArrayList<String>
    private lateinit var username : ArrayList<String>
    private lateinit var password : ArrayList<String>
    private lateinit var customAdapter: CustomAdapter
    private lateinit var handler: Handler

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityShowIdsBinding.inflate(layoutInflater)
        setContentView(binding.root)

//        autoExit()

        accountType = ArrayList()
        username = ArrayList()
        password = ArrayList()

        readAndStoreDataToArray()
        binding.credentialDataRv.adapter = customAdapter
        binding.credentialDataRv.layoutManager = LinearLayoutManager(this)


    }

    private fun readAndStoreDataToArray(){
        var query = "SELECT * FROM USERDATA"
        var helper = DBHelper(applicationContext)
        var db = helper.readableDatabase


        var cursor : Cursor? = null
        if(db != null){
            cursor = db.rawQuery(query,null)
        }

        if(cursor?.count == 0){
            Toast.makeText(this, "No Data Available!", Toast.LENGTH_SHORT).show()
        }else{
            if (cursor != null) {
                while(cursor.moveToNext()){
                    accountType.add(cursor.getString(1))
                    username.add(cursor.getString(2))
                    password.add(cursor.getString(3))
                }
            }
        }
        customAdapter = CustomAdapter(this,accountType,username,password)
        customAdapter.setOnItemClickListener(object : CustomAdapter.onItemClickListner{
            override fun onItemClick(position: Int) {

            }
        })

        ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(4, ItemTouchHelper.RIGHT) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {


                val builder = AlertDialog.Builder(this@ShowIDs)
                builder.setMessage("Are you sure you want to Delete?")
                    .setCancelable(false)
                    .setPositiveButton("Yes") { dialog, id ->
                        toast("Deleted")
                        var accountTypeToDel : String= accountType[viewHolder.adapterPosition]
                        var str : () -> String = {accountTypeToDel}
                        if(db != null){
                            cursor = db.rawQuery("DELETE FROM USERDATA WHERE ACCOUNT_TYPE = ?",
                                arrayOf(
                                    accountTypeToDel
                                ))
                        }
                        dialog.dismiss()
                        val intent = intent
                        finish()
                        startActivity(intent)
                        cursor?.getString(1)
                    }
                    .setNegativeButton("No") { dialog, id ->
                        val intent = intent
                        finish()
                        startActivity(intent)
                        dialog.dismiss()
                    }
                val alert = builder.create()
                alert.show()


            }
        }).attachToRecyclerView(binding.credentialDataRv)

    }

    private fun toast(str : String){
        Toast.makeText(this, "$str", Toast.LENGTH_SHORT).show()
    }

    private fun autoExit(){
        handler = Handler(Looper.getMainLooper())
        handler.postDelayed({
            startActivity(Intent(this,BiometricActivity::class.java))
            finish()
        },5000)
    }
}

//avoid fake data
//update option