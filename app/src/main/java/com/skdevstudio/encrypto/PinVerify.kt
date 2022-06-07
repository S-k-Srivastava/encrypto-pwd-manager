package com.skdevstudio.encrypto

import android.annotation.SuppressLint
import android.content.Intent
import android.database.Cursor
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import com.skdevstudio.encrypto.databinding.ActivityPinVerifyBinding
import java.security.MessageDigest
import java.util.*
import javax.crypto.Cipher
import javax.crypto.spec.SecretKeySpec
import kotlin.collections.ArrayList

class PinVerify : AppCompatActivity() {

    private lateinit var binding: ActivityPinVerifyBinding
    private lateinit var currentPin : String
    private var AES  : String = "AES"
    private var pvt_key : String = "3xh2B0l6v6VpCWr5T4CHBA=="
    private var pin : String = ""
    private lateinit var images : Array<ImageView>
    private lateinit var numbers : Array<TextView>
    private var count : Int = -1


    @SuppressLint("UseCompatLoadingForDrawables")
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPinVerifyBinding.inflate(layoutInflater)
        setContentView(binding.root)

        images = arrayOf(binding.img1,binding.img2,binding.img3,binding.img4,binding.img5,binding.img6)

        checkPinSet()
        typePin()
        binding.verifyPin.setOnClickListener {
            if (pin.equals(currentPin)){
                startActivity(Intent(this,MainActivity::class.java))
                finish()
            }else{
                binding.headerText.setTextColor(getColor(R.color.red))
                binding.headerText.text = "Invalid Pin!,Try Again!"
                pin = ""
                count = -1
                var i =0
                while (i<6){
                    images[i].setImageDrawable(getDrawable(R.drawable.ic_baseline_blur_circular_24))
                    i++
                }
            }
        }

    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun checkPinSet(){
        var query = "SELECT * FROM PIN"
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
                    currentPin = decrypt(cursor.getString(1),pvt_key)
                }
            }
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

    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("UseCompatLoadingForDrawables")
    private fun typePin(){
        binding.one.setOnClickListener {
            if(pin.length == 6){
                if(pin == currentPin){
                    startActivity(Intent(this,MainActivity::class.java))
                    finish()
                }else{
                    binding.headerText.setTextColor(getColor(R.color.red))
                    binding.headerText.text = "Invalid Pin!,Try Again!"
                    pin = ""
                    count = -1
                    var i =0
                    while (i<6){
                        images[i].setImageDrawable(getDrawable(R.drawable.ic_baseline_blur_circular_24))
                        i++
                    }
                }
            }else{
                pin += "1"
                count++
                images[count].setImageDrawable(getDrawable(R.drawable.ic_baseline_add_circle_24))
            }
        }
        binding.two.setOnClickListener {
            if(pin.length == 6){
                if(pin == currentPin){
                    startActivity(Intent(this,MainActivity::class.java))
                    finish()
                }else{
                    binding.headerText.setTextColor(getColor(R.color.red))
                    binding.headerText.text = "Invalid Pin!,Try Again!"
                    pin = ""
                    count = -1
                    var i =0
                    while (i<6){
                        images[i].setImageDrawable(getDrawable(R.drawable.ic_baseline_blur_circular_24))
                        i++
                    }
                }
            }else{
                pin += "2"
                count++
                images[count].setImageDrawable(getDrawable(R.drawable.ic_baseline_add_circle_24))
            }
        }
        binding.three.setOnClickListener {
            if(pin.length == 6){
                if(pin == currentPin){
                    startActivity(Intent(this,MainActivity::class.java))
                    finish()
                }else{
                    binding.headerText.setTextColor(getColor(R.color.red))
                    binding.headerText.text = "Invalid Pin!,Try Again!"
                    pin = ""
                    count = -1
                    var i =0
                    while (i<6){
                        images[i].setImageDrawable(getDrawable(R.drawable.ic_baseline_blur_circular_24))
                        i++
                    }
                }
            }else{
                pin += "3"
                count++
                images[count].setImageDrawable(getDrawable(R.drawable.ic_baseline_add_circle_24))
            }
        }
        binding.four.setOnClickListener {
            if(pin.length == 6){
                if(pin == currentPin){
                    startActivity(Intent(this,MainActivity::class.java))
                    finish()
                }else{
                    binding.headerText.setTextColor(getColor(R.color.red))
                    binding.headerText.text = "Invalid Pin!,Try Again!"
                    pin = ""
                    count = -1
                    var i =0
                    while (i<6){
                        images[i].setImageDrawable(getDrawable(R.drawable.ic_baseline_blur_circular_24))
                        i++
                    }
                }
            }else{
                pin += "4"
                count++
                images[count].setImageDrawable(getDrawable(R.drawable.ic_baseline_add_circle_24))
            }
        }
        binding.five.setOnClickListener {
            if(pin.length == 6){
                if(pin == currentPin){
                    startActivity(Intent(this,MainActivity::class.java))
                    finish()
                }else{
                    binding.headerText.setTextColor(getColor(R.color.red))
                    binding.headerText.text = "Invalid Pin!,Try Again!"
                    pin = ""
                    count = -1
                    var i =0
                    while (i<6){
                        images[i].setImageDrawable(getDrawable(R.drawable.ic_baseline_blur_circular_24))
                        i++
                    }
                }
            }else{
                pin += "5"
                count++
                images[count].setImageDrawable(getDrawable(R.drawable.ic_baseline_add_circle_24))
            }
        }
        binding.six.setOnClickListener {
            if(pin.length == 6){
                if(pin == currentPin){
                    startActivity(Intent(this,MainActivity::class.java))
                    finish()
                }else{
                    binding.headerText.setTextColor(getColor(R.color.red))
                    binding.headerText.text = "Invalid Pin!,Try Again!"
                    pin = ""
                    count = -1
                    var i =0
                    while (i<6){
                        images[i].setImageDrawable(getDrawable(R.drawable.ic_baseline_blur_circular_24))
                        i++
                    }
                }
            }else{
                pin += "6"
                count++
                images[count].setImageDrawable(getDrawable(R.drawable.ic_baseline_add_circle_24))
            }
        }
        binding.seven.setOnClickListener {
            if(pin.length == 6){
                if(pin == currentPin){
                    startActivity(Intent(this,MainActivity::class.java))
                    finish()
                }else{
                    binding.headerText.setTextColor(getColor(R.color.red))
                    binding.headerText.text = "Invalid Pin!,Try Again!"
                    pin = ""
                    count = -1
                    var i =0
                    while (i<6){
                        images[i].setImageDrawable(getDrawable(R.drawable.ic_baseline_blur_circular_24))
                        i++
                    }
                }
            }else{
                pin += "7"
                count++
                images[count].setImageDrawable(getDrawable(R.drawable.ic_baseline_add_circle_24))
            }
        }
        binding.eight.setOnClickListener {
            if(pin.length == 6){
                if(pin == currentPin){
                    startActivity(Intent(this,MainActivity::class.java))
                    finish()
                }else{
                    binding.headerText.setTextColor(getColor(R.color.red))
                    binding.headerText.text = "Invalid Pin!,Try Again!"
                    pin = ""
                    count = -1
                    var i =0
                    while (i<6){
                        images[i].setImageDrawable(getDrawable(R.drawable.ic_baseline_blur_circular_24))
                        i++
                    }
                }
            }else{
                pin += "8"
                count++
                images[count].setImageDrawable(getDrawable(R.drawable.ic_baseline_add_circle_24))
            }
        }
        binding.nine.setOnClickListener {
            if(pin.length == 6){
                if(pin == currentPin){
                    startActivity(Intent(this,MainActivity::class.java))
                    finish()
                }else{
                    binding.headerText.setTextColor(getColor(R.color.red))
                    binding.headerText.text = "Invalid Pin!,Try Again!"
                    pin = ""
                    count = -1
                    var i =0
                    while (i<6){
                        images[i].setImageDrawable(getDrawable(R.drawable.ic_baseline_blur_circular_24))
                        i++
                    }
                }
            }else{
                pin += "9"
                count++
                images[count].setImageDrawable(getDrawable(R.drawable.ic_baseline_add_circle_24))
            }
        }
        binding.zero.setOnClickListener {
            if(pin.length == 6){
                if(pin == currentPin){
                    startActivity(Intent(this,MainActivity::class.java))
                    finish()
                }else{
                    binding.headerText.setTextColor(getColor(R.color.red))
                    binding.headerText.text = "Invalid Pin!,Try Again!"
                    pin = ""
                    count = -1
                    var i =0
                    while (i<6){
                        images[i].setImageDrawable(getDrawable(R.drawable.ic_baseline_blur_circular_24))
                        i++
                    }
                }
            }else{
                pin += "0"
                count++
                images[count].setImageDrawable(getDrawable(R.drawable.ic_baseline_add_circle_24))
            }
        }
        binding.cut.setOnClickListener {
            if(count == -1){

            }else{
                pin = pin.substring(0,pin.length - 1)
                images[count].setImageDrawable(getDrawable(R.drawable.ic_baseline_blur_circular_24))
                count--
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun decrypt(Data: String, pwd: String): String {
        var key : SecretKeySpec = generateKey(pwd)
        var c : Cipher = Cipher.getInstance(AES)
        c.init(Cipher.DECRYPT_MODE,key)
        var decodedValue : ByteArray = Base64.getDecoder().decode(Data)
        var decValue : ByteArray = c.doFinal(decodedValue)
        var decValStr = String(decValue)
        return decValStr

    }

}