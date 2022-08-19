package com.skdevstudio.encrypto

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.*
import android.database.Cursor
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.skdevstudio.encrypto.databinding.ActivityMainBinding
import java.security.SecureRandom

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var database: DatabaseReference
    private lateinit var user : User
    val letters : String = "abcdefghijklmnopqrstuvwxyz"
    val uppercaseLetters : String = "ABCDEFGHIJKLMNOPQRSTUVWXYZ"
    val numbers : String = "0123456789"
    val special : String = ""
    private lateinit  var mydialog : Dialog

    @SuppressLint("ResourceAsColor")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        database = Firebase.database.reference

        restoreFirstTime()

        binding.addDetailsBtn.setOnClickListener {
            startActivity(Intent(this, AddIds::class.java))
        }
        binding.showCredentialsBtn.setOnClickListener {
            startActivity(Intent(this, ShowIDs::class.java))
        }
        binding.backup.setOnClickListener {
            takeBackup()
        }

        binding.restore.setOnClickListener {
            restore()
        }
        binding.resetKey.setOnClickListener {
            resetKey()
        }

        binding.dltBackup.setOnClickListener {
            deleteBackup()
        }



        binding.generatePwd.setOnClickListener {
            val pwd = generatePassword(true,true,true,true,20)
            val builder = AlertDialog.Builder(this)
            builder.setMessage("Password : ${pwd}\n")
                .setCancelable(false)
                .setPositiveButton("Copy") { dialog, id ->
                    copyText(pwd,"Password")
                    toast("Copied to Clipboard!")
                }
                .setNegativeButton("Cancel") { dialog, id ->
                    dialog.dismiss()
                }
            val alert = builder.create()
            alert.show()

        }
    }

    override fun onStart() {
        super.onStart()
        restoreFirstTime()
    }

    private fun signOut(){
        val builder = AlertDialog.Builder(this)
        builder.setMessage("Logout ? ")
            .setCancelable(false)
            .setPositiveButton("Logout") { dialog, id ->
                FirebaseAuth.getInstance().signOut()
                startActivity(Intent(this, SignUp::class.java))
            }
            .setNegativeButton("Cancel") { dialog, id ->
                dialog.dismiss()
            }
        val alert = builder.create()
        alert.show()
    }

    private fun copyText(text:String,label : String){
        val myClipboard = this.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val myClip: ClipData = ClipData.newPlainText(label, text)
        myClipboard.setPrimaryClip(myClip)
    }

    private fun restore(){

        val builder = AlertDialog.Builder(this)
        builder.setMessage("Are you sure you want to Restore?")
            .setCancelable(false)
            .setPositiveButton("Yes") { dialog, id ->
               try {
                   val reference = Firebase.database.getReference("User").child(FirebaseAuth.getInstance().currentUser?.uid.toString())
                   reference.addValueEventListener(object : ValueEventListener {
                       override fun onDataChange(dataSnapshot: DataSnapshot) {
                           for (snapshot in dataSnapshot.children) {
                               val helper = DBHelper(applicationContext)
                               val db = helper.readableDatabase
                               val cv = ContentValues()
                               cv.put("ACCOUNT_TYPE",snapshot.child("accountType").value.toString())
                               cv.put("USERNAME",snapshot.child("username").value.toString())
                               cv.put("USER_PASSWORD",snapshot.child("password").value.toString())
                               cv.put("CHECK_KEY",snapshot.child("checkKey").value.toString())
                               db.insert("USERDATA",null,cv).toInt()
                           }
                       }
                       override fun onCancelled(databaseError: DatabaseError) {
                           toast("error")
                       }
                   })
                   toast("Data Synced Successfully!!")
               }catch (e : Exception){
                   toast("Restore failed!")
               }
            }
            .setNegativeButton("No") { dialog, id ->

                dialog.dismiss()
            }
        val alert = builder.create()
        alert.show()
    }
    private fun toast(str : String){
        Toast.makeText(this, "$str", Toast.LENGTH_SHORT).show()
    }

    private fun generatePassword(isWithLetters: Boolean,
                         isWithUppercase: Boolean,
                         isWithNumbers: Boolean,
                         isWithSpecial: Boolean,
                         length: Int) : String {

        var result = ""
        var i = 0

        if(isWithLetters){ result += this.letters }
        if(isWithUppercase){ result += this.uppercaseLetters }
        if(isWithNumbers){ result += this.numbers }
        if(isWithSpecial){ result += this.special }

        val rnd = SecureRandom.getInstance("SHA1PRNG")
        val sb = StringBuilder(length)

        while (i < length) {
            val randomInt : Int = rnd.nextInt(result.length)
            sb.append(result[randomInt])
            i++
        }

        return sb.toString()
    }

    private fun restoreFirstTime(){

        val helper = DBHelper(applicationContext)
        val db = helper.readableDatabase
        val cv = ContentValues()
        var cursor: Cursor? = null
        val query = "SELECT * FROM USERDATA"
        if (db != null) {
            cursor = db.rawQuery(query, null)
        }
        if(cursor?.count == 0){
            try {
                val reference = Firebase.database.getReference("User").child(FirebaseAuth.getInstance().currentUser?.uid.toString())
                reference.addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        for (snapshot in dataSnapshot.children) {
                            cv.put("ACCOUNT_TYPE",snapshot.child("accountType").value.toString())
                            cv.put("USERNAME",snapshot.child("username").value.toString())
                            cv.put("USER_PASSWORD",snapshot.child("password").value.toString())
                            cv.put("CHECK_KEY",snapshot.child("checkKey").value.toString())
                            db.insert("USERDATA",null,cv).toInt()
                        }
                        toast("Synced Automatically!!")
                    }
                    override fun onCancelled(databaseError: DatabaseError) {
                        toast("error")
                    }
                })
            } catch (e : Exception){
                toast(e.message.toString())
            }
        }

    }

    @SuppressLint("Recycle")
    private fun takeBackup() {

        val query = "SELECT * FROM USERDATA"
        val helper = DBHelper(applicationContext)
        val db = helper.readableDatabase
        var cursor: Cursor? = null
        if (db != null) {
            cursor = db.rawQuery(query, null)
        }

        val builder = AlertDialog.Builder(this)
        builder.setMessage("Are you sure you want to Backup?")
            .setCancelable(false)
            .setPositiveButton("Yes") { dialog, id ->
                try {
                    Firebase.database.getReference("User").child(FirebaseAuth.getInstance().currentUser?.uid.toString()).removeValue().addOnFailureListener {
                        toast("Backup Failed!")
                        return@addOnFailureListener
                    }
                    if (cursor!!.moveToFirst()) {
                        do {
                            val accountType = cursor.getString(0)
                            val username = cursor.getString(1)
                            val password = cursor.getString(2)
                            val check = cursor.getString(3)
                            user = User(accountType,username,password,check)
                            Firebase.database.getReference("User").child(FirebaseAuth.getInstance().currentUser?.uid.toString()).child(accountType).setValue(user).addOnCompleteListener { task ->
                                if(!task.isSuccessful){
                                    toast("Backup failed!")
                                }
                            }

                        } while (cursor.moveToNext())
                        toast("Backup done!")
                    }
                    cursor.close()
                } catch (e : Exception){
                    toast("Backup failed!")
                }
            }
            .setNegativeButton("No") { dialog, id ->

                dialog.dismiss()
            }
        val alert = builder.create()
        alert.show()

    }

    private fun deleteBackup(){

        val builder = AlertDialog.Builder(this)
        builder.setMessage("Are you sure you want to DELETE Backup?")
            .setCancelable(false)
            .setPositiveButton("Yes") { dialog, id ->
                try {
                    Firebase.database.getReference("User").child(FirebaseAuth.getInstance().currentUser?.uid.toString()).removeValue().addOnCompleteListener {
                        toast("Backup Deleted!")
                    }.addOnFailureListener {
                        toast("Failed to delete!")
                    }
                }catch (e : Exception){
                    toast("Failed to delete!")
                }
            }
            .setNegativeButton("No") { dialog, id ->
                dialog.dismiss()
            }
        val alert = builder.create()
        alert.show()
        }


    @SuppressLint("SetTextI18n")
    private fun resetKey(){
        var query = "DELETE FROM USERDATA"
        var helper = DBHelper(applicationContext)
        var db = helper.readableDatabase

        val builder = androidx.appcompat.app.AlertDialog.Builder(this)

        builder.setMessage("This will delete all your data!")
            .setCancelable(false)
            .setPositiveButton("Yes") { dialog, id ->
                val builder = androidx.appcompat.app.AlertDialog.Builder(this)
                builder.setMessage("Are You sure ?")
                    .setCancelable(false)
                    .setPositiveButton("Confirm") { dialog, id ->
                        try {
                            db.execSQL(query)
                            Firebase.database.getReference("User").child(FirebaseAuth.getInstance().currentUser?.uid.toString()).removeValue().addOnFailureListener {
                                toast("Failed!")
                                return@addOnFailureListener
                            }
                            toast("Reset Done!")
                            dialog.dismiss()
                        }catch (e : java.lang.Exception){
                            dialog.dismiss()
                        }
                    }
                    .setNegativeButton("No") { dialog, id ->
                        dialog.dismiss()
                    }
                val alert = builder.create()
                alert.show()
            }
            .setNegativeButton("No") { dialog, id ->
                dialog.dismiss()
            }
        val alert = builder.create()
        alert.show()
    }

}