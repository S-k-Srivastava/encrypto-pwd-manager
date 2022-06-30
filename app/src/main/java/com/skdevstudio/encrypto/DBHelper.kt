package com.skdevstudio.encrypto

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DBHelper(context: Context) : SQLiteOpenHelper(context,"USERDB",null,1) {
    override fun onCreate(db: SQLiteDatabase?) {
        db?.execSQL("CREATE TABLE USERDATA(ACCOUNT_TYPE TEXT PRIMARY KEY,USERNAME TEXT,USER_PASSWORD TEXT,CHECK_KEY TEXT,UNIQUE (ACCOUNT_TYPE COLLATE NOCASE))")
    }
    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        TODO("Not yet implemented")
    }
}
//db?.execSQL("CREATE TABLE USERDATA(ID INTEGER PRIMARY KEY AUTOINCREMENT,ACCOUNT_TYPE TEXT,USERNAME TEXT,USER_PASSWORD TEXT,CHECK_KEY TEXT)")
