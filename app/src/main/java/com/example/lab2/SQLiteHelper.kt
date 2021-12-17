package com.example.lab2

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class SQLiteHelper(context: Context?) :
    SQLiteOpenHelper(context,"barberData.db", null, 1) {

    override fun onCreate(db: SQLiteDatabase?) {
        db?.execSQL("CREATE TABLE IF NOT EXISTS barber (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "barberName TEXT, " +
//                "serviceName TEXT, " +
                "barberId INTEGER)"
//                "serviceId TEXT)"
        )

        db?.execSQL("CREATE TABLE IF NOT EXISTS service (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
//                "barberName TEXT, " +
                "serviceName TEXT, " +
//                "barberId INTEGER, " +
                "serviceId INTEGER)")

        db?.execSQL("CREATE TABLE IF NOT EXISTS date (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
//                "barberName TEXT, " +
                "date TEXT)")
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVer: Int, newVer: Int) {
        // Ничего не делаем
    }
}