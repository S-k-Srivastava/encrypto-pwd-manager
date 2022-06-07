package com.skdevstudio.encrypto

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DBHelper(context: Context) : SQLiteOpenHelper(context,"USERDB",null,1) {
    override fun onCreate(db: SQLiteDatabase?) {
        db?.execSQL("CREATE TABLE USERDATA(ID INTEGER PRIMARY KEY AUTOINCREMENT,ACCOUNT_TYPE TEXT,USERNAME TEXT,USER_PASSWORD TEXT)")
        db?.execSQL("CREATE TABLE PIN(ID INTEGER PRIMARY KEY AUTOINCREMENT,USER_PIN TEXT)")
    }
    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        TODO("Not yet implemented")
    }
}