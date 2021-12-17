package com.example.lab2

import android.app.Activity
import android.content.ContentValues
import android.content.Intent
import android.database.sqlite.SQLiteDatabase
import android.graphics.BitmapFactory
import android.os.Bundle
import android.provider.ContactsContract.CommonDataKinds.Website.URL
import android.util.Log
import android.view.MenuItem
import android.widget.*
import androidx.appcompat.app.ActionBar
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import com.example.lab2.databinding.ActivityBarberBinding
import com.example.lab2.models.Barber
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import java.net.URL
import java.util.concurrent.Executors
import android.widget.TextView




class BarberActivity : AppCompatActivity() {

     private lateinit var con: SQLiteDatabase;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_barber)

        val db = SQLiteHelper(this);
        con = db.writableDatabase

        Executors.newSingleThreadExecutor().execute {
            val mapper = jacksonObjectMapper()
            val jsonStr = URL("http://10.0.2.2:5192/api/barbers").readText()
            val barbers: List<Barber> = mapper.readValue(jsonStr)

            Log.w("X", barbers[0].name)

            val barberList = ArrayList<Map<String, Any>>()
            var adapterItem: MutableMap<String, Any>

            for (item in barbers){
                adapterItem = HashMap()
                adapterItem["name"] = item.name
                adapterItem["mastery"] = item.mastery
                barberList.add(adapterItem)
            }

            val from = arrayOf<String>("name", "mastery")
            val to = intArrayOf(R.id.name, R.id.mastery)

            runOnUiThread {
                val adapter = SimpleAdapter(this,
                    barberList,
                    R.layout.barber_list_item,
                    from, to)

                val listView: ListView = findViewById(R.id.listBarbers)
                listView.adapter = adapter

                listView.setOnItemClickListener(AdapterView.OnItemClickListener { parent, view, position, id ->
                    Log.w("pos", position.toString())
                    Log.w("id", id.toString())

                    val cv = ContentValues()
                    cv.put("barberId", barbers[position].id)
                    cv.put("barberName", barbers[position].name)

                    val clearTable = "DELETE FROM barber"
                    con.execSQL(clearTable)
                    con.insert("barber", null, cv)
                    con.close()

                    val intent = Intent(this, MainActivity::class.java)

                    startActivityForResult(intent, 0)
                })
            }
        }

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setTitle("Выберите барбера")
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            finish()
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}