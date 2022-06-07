package com.skdevstudio.encrypto

import android.content.ContentValues
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.annotation.RequiresApi
import com.skdevstudio.encrypto.databinding.ActivitySetUserKeyBinding
import java.security.MessageDigest
import java.util.*
import javax.crypto.Cipher
import javax.crypto.spec.SecretKeySpec

class SetUserKey : AppCompatActivity() {

    private lateinit var binding: ActivitySetUserKeyBinding
    private var AES  : String = "AES"
    private var pvt_key : String = "3xh2B0l6v6VpCWr5T4CHBA=="


    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySetUserKeyBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.userKey.requestFocus()

        binding.setKey.setOnClickListener {
            setUserPin()
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun setUserPin() {
        if(binding.userKey.text.toString().isEmpty()){
            binding.userKey.error = "Must not be Blank!"
            binding.userKey.requestFocus()
            return
        }else if(!binding.confirmUserKey.text.toString().equals(binding.userKey.text.toString())){
            binding.confirmUserKey.error = "Confirm Password must match!"
            binding.confirmUserKey.requestFocus()
            return
        }else if(binding.userKey.text.toString().length != 6){
            binding.confirmUserKey.error = "Must be of 6 Digits Only!"
            binding.confirmUserKey.requestFocus()
            return
        }
        else {
            var helper = DBHelper(applicationContext)
            var db = helper.readableDatabase
            var cv = ContentValues()
            cv.put("USER_PIN", encrypt(binding.userKey.text.toString(),pvt_key))
            cv.put("ID","1")
            Toast.makeText(this, "done", Toast.LENGTH_SHORT).show()
            startActivity(Intent(this,PinVerify::class.java))
            finishAffinity()
            var result: Int
            result = db.insert("PIN",null,cv).toInt()
        }
    }


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