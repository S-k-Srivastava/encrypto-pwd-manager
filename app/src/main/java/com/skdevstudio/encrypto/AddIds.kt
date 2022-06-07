package com.skdevstudio.encrypto

import android.content.ContentValues
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.skdevstudio.encrypto.databinding.ActivityAddIdsBinding
import java.security.MessageDigest
import java.util.*
import javax.crypto.Cipher
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec

class AddIds : AppCompatActivity() {
    
    private lateinit var binding: ActivityAddIdsBinding
    private var AES : String= "AES"
    private var pvt_key : String = "3xh2B0l6v6VpCWr5T4CHBA=="
    private var toUpdate : String = "default" 

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddIdsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        var intent = intent
        if(intent.getStringExtra("AccountType") != null){
            toUpdate = intent.getStringExtra("AccountType")!!
            binding.accountType.setText(toUpdate)
            binding.accountType.isEnabled = false
        }
        binding.addData.setOnClickListener {
            addData()
        }
    }
    
    @RequiresApi(Build.VERSION_CODES.O)
    private fun addData(){
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
        }else{
            var helper = DBHelper(applicationContext)
            var db = helper.readableDatabase
            var cv = ContentValues()
            cv.put("ACCOUNT_TYPE",binding.accountType.text.toString())
            cv.put("USERNAME",binding.username.text.toString())
            cv.put("USER_PASSWORD",encrypt(binding.password.text.toString(),pvt_key))
            var result : Int

            if(intent.getStringExtra("AccountType") != null){

                toUpdate = intent.getStringExtra("AccountType")!!
                binding.accountType.setText(toUpdate)
                binding.accountType.isEnabled = false
                result = db.update("USERDATA",cv, "ACCOUNT_TYPE=?", arrayOf(toUpdate))
                Toast.makeText(this, "Updated Successfully!", Toast.LENGTH_SHORT).show()
                startActivity(Intent(this,ShowIDs::class.java))
                finish()
                db.close()

            }else{

                result = db.insert("USERDATA",null,cv).toInt()
                Toast.makeText(this, "Stored Credentials Securely!", Toast.LENGTH_SHORT).show()
            }

            if(result == -1){
                Toast.makeText(this, "Failed!", Toast.LENGTH_SHORT).show()
            }else{
                binding.accountType.text?.clear()
                binding.username.text?.clear()
                binding.password.text?.clear()
            }
        }
    }

    //AES

    @RequiresApi(Build.VERSION_CODES.O)
    private fun encrypt(Data : String, pwd : String): String {
        var key : SecretKeySpec = generateKey(pwd)
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

}