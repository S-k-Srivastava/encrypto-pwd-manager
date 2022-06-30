package com.skdevstudio.encrypto

import android.annotation.SuppressLint
import android.content.Intent
import android.database.Cursor
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.Menu
import android.widget.SearchView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.skdevstudio.encrypto.databinding.ActivityShowIdsBinding
import java.util.*
import kotlin.collections.ArrayList


class ShowIDs : AppCompatActivity()  {

    private lateinit var binding: ActivityShowIdsBinding
    private lateinit var accountType : ArrayList<String>
    private lateinit var tempData : ArrayList<String>
    private lateinit var tempUsername : ArrayList<String>
    private lateinit var tempPassword : ArrayList<String>
    private lateinit var username : ArrayList<String>
    private lateinit var password : ArrayList<String>
    private lateinit var customAdapter: CustomAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityShowIdsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        accountType = ArrayList()
        username = ArrayList()
        password = ArrayList()
        tempData = ArrayList()
        tempUsername = ArrayList()
        tempPassword= ArrayList()

        readAndStoreDataToArray()

        binding.credentialDataRv.adapter = customAdapter
        binding.credentialDataRv.layoutManager = LinearLayoutManager(this)

        binding.searchAccount.setOnClickListener {
            binding.searchAccount.isIconified = false
        }

        binding.searchAccount.setOnQueryTextListener(object  : SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(p0: String?): Boolean {
                val searchText = p0!!.toLowerCase(Locale.getDefault())
                tempData.clear()
                tempUsername.clear()
                tempPassword.clear()
                if(searchText.isNotEmpty()){
                    for(i in 0..accountType.lastIndex) {
                        if(accountType[i].toLowerCase().contains(searchText)){
                            tempData.add(accountType[i])
                            tempUsername.add(username[i])
                            tempPassword.add(password[i])
                        }
                    }
                }
                if(searchText.isEmpty()){
                    tempData.addAll(accountType)
                    tempUsername.addAll(username)
                    tempPassword.addAll(password)
                }
                customAdapter.notifyDataSetChanged()
                return false
            }
            override fun onQueryTextChange(newText: String?): Boolean {
                val searchText = newText!!.toLowerCase(Locale.getDefault())
                tempData.clear()
                tempUsername.clear()
                tempPassword.clear()
                if(searchText.isNotEmpty()){
                    for(i in 0..accountType.lastIndex) {
                        if(accountType[i].toLowerCase().contains(searchText)){
                            tempData.add(accountType[i])
                            tempUsername.add(username[i])
                            tempPassword.add(password[i])
                        }
                    }
                }
                if(searchText.isEmpty()){
                    tempData.addAll(accountType)
                    tempUsername.addAll(username)
                    tempPassword.addAll(password)
                }
                customAdapter.notifyDataSetChanged()
                return false
            }

        })


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
                    accountType.add(cursor.getString(0))
                    username.add(cursor.getString(1))
                    password.add(cursor.getString(2))
                }
            }
        }
        tempData.addAll(accountType)
        tempUsername.addAll(username)
        tempPassword.addAll(password)
        customAdapter = CustomAdapter(this,tempData,tempUsername,tempPassword)
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
                        try {
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
                            startActivity(intent)
                            finish()
                            cursor?.getString(1)
                        }catch (e : Exception){
                            dialog.dismiss()
                        }
                    }
                    .setNegativeButton("No") { dialog, id ->
                        dialog.dismiss()
                        startActivity(intent)
                        finish()
                    }
                val alert = builder.create()
                alert.show()


            }
        }).attachToRecyclerView(binding.credentialDataRv)

    }

    private fun toast(str : String){
        Toast.makeText(this, "$str", Toast.LENGTH_SHORT).show()
    }

}
