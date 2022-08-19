package com.skdevstudio.encrypto

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.ContentValues
import android.content.DialogInterface
import android.content.Intent
import android.database.Cursor
import android.graphics.drawable.ColorDrawable
import android.opengl.Visibility
import android.os.Build
import android.os.Bundle
import android.text.InputType.*
import android.view.View
import android.view.WindowManager
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import com.google.android.material.textfield.TextInputLayout
import com.skdevstudio.encrypto.databinding.ActivityAddIdsBinding
import java.security.MessageDigest
import java.util.*
import javax.crypto.Cipher
import javax.crypto.spec.SecretKeySpec
import kotlin.Exception

class AddIds : AppCompatActivity() {
    
    private lateinit var binding: ActivityAddIdsBinding
    private var AES : String= "AES"
    private var pvtKey : String = ""
    private var toUpdate : String = "default"
    private lateinit var CHECK_KEY : String
    private lateinit var mydialog : Dialog
    private var count : Int = 123677777
    private var devKey : String = "4LOQWCIPY5eQfTmf51P6LA=="


    @SuppressLint("SetTextI18n")
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddIdsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        var intent = intent
        if(intent.getStringExtra("AccountType") != null){
            toUpdate = intent.getStringExtra("AccountType")!!
            binding.accountType.setText(toUpdate)
            binding.addCredentialsHeader.text = "Update $toUpdate"
            binding.accountType.isEnabled = false
        }
        binding.addData.setOnClickListener {
            getKeyFromUser()
        }
    }
    
    @RequiresApi(Build.VERSION_CODES.O)
    private fun addData(){

        val helper = DBHelper(applicationContext)
        val db = helper.readableDatabase
        val cv = ContentValues()

        cv.put("ACCOUNT_TYPE",binding.accountType.text.toString().trim())
        cv.put("USERNAME",binding.username.text.toString().trim())
        cv.put("USER_PASSWORD",encrypt(binding.password.text.toString(),pvtKey).trim())
        cv.put("CHECK_KEY",CHECK_KEY)
        val result : Int

        if(intent.getStringExtra("AccountType") != null){
            toUpdate = intent.getStringExtra("AccountType")!!
            binding.accountType.setText(toUpdate.trim())
            binding.accountType.isEnabled = false
            result = db.update("USERDATA",cv, "ACCOUNT_TYPE=?", arrayOf(toUpdate))
            Toast.makeText(this, "Updated Successfully!", Toast.LENGTH_SHORT).show()
            startActivity(Intent(this, ShowIDs::class.java))
            finish()
            db.close()
        }else{
            result = db.insert("USERDATA",null,cv).toInt()
        }

        if(result == -1){
            Toast.makeText(this, "Account Already Exists!!", Toast.LENGTH_SHORT).show()
        }else{
            Toast.makeText(this, "Stored Credentials Securely!", Toast.LENGTH_SHORT).show()
            binding.accountType.text?.clear()
            binding.username.text?.clear()
            binding.password.text?.clear()
        }
    }

    //AES

    @RequiresApi(Build.VERSION_CODES.O)
    private fun encrypt(Data : String, pwd : String): String {
        var key : SecretKeySpec = generateKey(pwd+devKey)
        var c : Cipher = Cipher.getInstance(AES)
        c.init(Cipher.ENCRYPT_MODE,key)
        var encVal = c.doFinal(Data.toByteArray())
        var encValueStr = Base64.getEncoder().encodeToString(encVal)
        return encValueStr
    }

    private fun generateKey(pwd: String): SecretKeySpec {
        val messageDigest : MessageDigest = MessageDigest.getInstance("SHA-256")
        messageDigest.update(pwd.toByteArray())
        var secretKeySpec : SecretKeySpec = SecretKeySpec(messageDigest.digest(),"AES")
        return  secretKeySpec
    }

    @SuppressLint("ResourceAsColor", "SetTextI18n")
    @RequiresApi(Build.VERSION_CODES.O)
    fun getKeyFromUser() {
        if(binding.accountType.text.toString().isEmpty()){
            binding.accountType.error = "Must not be Blank!"
            binding.accountType.requestFocus()
            return
        }else if(binding.username.text.toString().isEmpty()){
            binding.username.error = "Must not be Blank!"
            binding.username.requestFocus()
            return
        }else if(binding.password.text.toString().isEmpty()){
            binding.password.error = "Must not be Blank!"
            binding.password.requestFocus()
            return
        }
        mydialog = Dialog(this)
        mydialog.setContentView(R.layout.key_dailog)
        mydialog.show()
        mydialog.window?.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        var title : TextView = mydialog.findViewById<TextView>(R.id.dailog_text)
        var posBtn : TextView = mydialog.findViewById(R.id.positiveBtn)
        var negBtn : TextView = mydialog.findViewById(R.id.cancelBtn)
        var key : EditText = mydialog.findViewById(R.id.key)
        var cnfKey : EditText = mydialog.findViewById(R.id.cnfkey)
        var noteText : TextView = mydialog.findViewById(R.id.note_text)

        //checking if the entry is new

        val query = "SELECT * FROM USERDATA"
        val helper = DBHelper(applicationContext)
        val db = helper.readableDatabase

        var cursor : Cursor? = null
        if(db != null){
            cursor = db.rawQuery(query,null)
        }
        if(cursor?.count != 0){
            if (cursor != null) {
                while(cursor.moveToNext()){
                    CHECK_KEY = cursor.getString(3)
                }
            }
            cnfKey.visibility = View.INVISIBLE
            mydialog.findViewById<TextInputLayout>(R.id.cnfKeyRoot).visibility = View.GONE
            title.text = "Enter the Secret Key!"
            noteText.text = "Note : It is the key you used before!"
        }else{
            count = 0
            title.text = "Create a new secret key that will be used to encrypt your credentials!\n\n"
            noteText.text = "Note : This will not be stored anywhere for security purpose so,you must remember it and it can't be changed!"
        }

        posBtn.setOnClickListener {
            if(key.text.toString().isEmpty()){
                key.error = "Must not be Blank!"
                key.requestFocus()
            }else if(cnfKey.isVisible && !(cnfKey.text.toString().equals(key.text.toString()))){
                cnfKey.error = "Confirm key must be same"
                cnfKey.requestFocus()
            }else {
                if (cursor?.count != 0) {
                    try {
                        decrypt(CHECK_KEY, key.text.toString().trim())
                        pvtKey = key.text.toString().trim()
                        addData()
                    } catch (e: Exception) {
                        mydialog.dismiss()
                        Toast.makeText(
                            this,
                            "Enter the correct key used before!",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                } else {
                    pvtKey = key.text.toString().trim()
                    CHECK_KEY = encrypt("Skdevelopersstudio", pvtKey)
                    addData()
                }
                mydialog.dismiss()
            }
        }
        negBtn.setOnClickListener {
            mydialog.dismiss()
        }

    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun decrypt(Data: String, pwd: String): String {
        var key : SecretKeySpec = generateKey(pwd+devKey)
        var c : Cipher = Cipher.getInstance(AES)
        c.init(Cipher.DECRYPT_MODE,key)
        var decodedValue : ByteArray = Base64.getDecoder().decode(Data)
        var decValue : ByteArray = c.doFinal(decodedValue)
        var decValStr = String(decValue)
        return decValStr

    }

}