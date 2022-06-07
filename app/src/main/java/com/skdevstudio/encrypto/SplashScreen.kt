package com.skdevstudio.encrypto

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Intent
import android.database.Cursor
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.Menu
import android.widget.Toast

class SplashScreen : AppCompatActivity() {

    private var currentPin : String = "default"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)

        checkPinSet()

        Handler(Looper.getMainLooper()).postDelayed({

            if(currentPin != "default"){
                startActivity(Intent(this,PinVerify::class.java))
                finish()
            }else{
                startActivity(Intent(this,SetUserKey::class.java))
                finish()
            }
        },1500)

    }

    @SuppressLint("Recycle")
    private fun checkPinSet(){
        var query = "SELECT * FROM PIN"
        var helper = DBHelper(applicationContext)
        var db = helper.readableDatabase


        var cursor : Cursor? = null
        if(db != null){
            cursor = db.rawQuery(query,null)
        }

        if(cursor?.count == 0){

        }else{
            if (cursor != null) {
                while(cursor.moveToNext()){
                    currentPin = cursor.getString(1)
                }
            }
        }
    }
}