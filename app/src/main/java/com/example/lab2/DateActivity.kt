package com.example.lab2

import android.content.ContentValues
import android.content.Intent
import android.database.sqlite.SQLiteDatabase
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ListView
import java.time.LocalDate
import java.util.*

class DateActivity : AppCompatActivity() {
    private lateinit var con: SQLiteDatabase;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_date)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setTitle("Выберите дату")

        val db = SQLiteHelper(this)
        con = db.writableDatabase

        var dates = arrayListOf("10:00", "12:00", "14:00", "16:00", "18:00", "20:00")

        val adapter = ArrayAdapter<String>(this,
            android.R.layout.simple_list_item_1, dates)
        val listView: ListView = findViewById(R.id.listDates)
        listView.adapter = adapter

        listView.setOnItemClickListener(AdapterView.OnItemClickListener { parent, view, position, id ->
//            val cv = ContentValues()
//            cv.put("date", dates[position])
//            val clearTable = "DELETE FROM date"
//            con.execSQL(clearTable)
//            var insert = "INSERT INTO date (date) VALUES ('" + dates[position] + "');"
//            con.insert("date", null, cv)
//            con.execSQL(insert)
//            con.close()

            val intent = Intent(this, MainActivity::class.java)
            intent.putExtra("date", dates[position])
            startActivityForResult(intent, 0)
        })

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            finish()
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}