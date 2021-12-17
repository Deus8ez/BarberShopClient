package com.example.lab2

import android.content.ContentValues
import android.content.Intent
import android.database.sqlite.SQLiteDatabase
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.AdapterView
import android.widget.ListView
import android.widget.SimpleAdapter
import com.example.lab2.models.Service
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import java.net.URL
import java.util.concurrent.Executors

class ServiceActivity : AppCompatActivity() {
    private lateinit var con: SQLiteDatabase;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_service)

        Executors.newSingleThreadExecutor().execute {
            val mapper = jacksonObjectMapper()
            val jsonStr = URL("http://10.0.2.2:5192/api/services").readText()
            val services: List<Service> = mapper.readValue(jsonStr)

            val db = SQLiteHelper(this);
            con = db.writableDatabase

            Log.w("X", services[0].name)

            val serviceList = ArrayList<Map<String, Any>>()
            var adapterItem: MutableMap<String, Any>

            for (item in services){
                adapterItem = HashMap()
                adapterItem["name"] = item.name
                adapterItem["price"] = item.price
                serviceList.add(adapterItem)
            }

            val from = arrayOf<String>("name", "price")
            val to = intArrayOf(R.id.name, R.id.price)

            runOnUiThread {
                val adapter = SimpleAdapter(this,
                    serviceList,
                    R.layout.service_list_item,
                    from, to)

                val listView: ListView = findViewById(R.id.listServices)
                listView.adapter = adapter

                listView.setOnItemClickListener(AdapterView.OnItemClickListener { parent, view, position, id ->
                    Log.w("pos", position.toString())
                    Log.w("id", id.toString())
                    val intent = Intent(this, MainActivity::class.java)

                    val cv = ContentValues()
                    cv.put("serviceId", services[position].id)
                    cv.put("serviceName", services[position].name)

                    val clearTable = "DELETE FROM service"
                    con.execSQL(clearTable)
                    con.insert("service", null, cv)
                    con.close()

//                    intent.putExtra("service", services[position])

                    startActivityForResult(intent, 0)
                })
            }
        }

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setTitle("Выберите услугу")
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            finish()
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}