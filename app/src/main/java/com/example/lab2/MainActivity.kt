package com.example.lab2

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.database.sqlite.SQLiteDatabase
import android.graphics.BitmapFactory
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.ContactsContract
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.*

import android.widget.AdapterView.OnItemClickListener
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat.getSystemService
import com.example.lab2.models.Barber
import com.example.lab2.models.Service
import com.google.gson.annotations.SerializedName
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST
import java.net.URL

class MainActivity : AppCompatActivity() {

    private var index = 0
    private lateinit var barber: Barber
    private lateinit var service: Service
    private lateinit var date: String
    lateinit var notificationManager: NotificationManager
    val CHANNEL_ID = "channelID"
    val CHANNEL_NAME = "channelName"

    private lateinit var con: SQLiteDatabase;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val db = SQLiteHelper(this);
        con = db.readableDatabase

        val barbers = arrayOf("Сотрудник", "Услуга", "Время")
        date = ""

        val intent = intent
        date = intent?.getStringExtra("date") ?: ""

        barber = Barber(0, "", "")
        service = Service()

        var cursor = con.query("barber", arrayOf("barberId", "barberName"), null, null, null, null, null)
        cursor.moveToFirst()
        if(cursor.getCount() > 0){
            while (!cursor.isAfterLast) {
                barber.id = cursor.getInt(0)
                barber.name = cursor.getString(1)
                cursor.moveToNext()
            }
        }

        cursor = con.query("service", arrayOf("serviceId", "serviceName"), null, null, null, null, null)
        cursor.moveToFirst()
        if(cursor.getCount() > 0){
            while (!cursor.isAfterLast) {
                service.id = cursor.getInt(0)
                service.name = cursor.getString(1)
                cursor.moveToNext()
            }
        }

        cursor.close()

        val menuList = ArrayList<Map<String, Any>>()
        var menuItem: MutableMap<String, Any>

        menuItem = HashMap()
        menuItem["title"] = "Сотрудник"
        menuItem["info"] = barber.name
        menuList.add(menuItem)

        menuItem = HashMap()
        menuItem["title"] = "Услуга"
        menuItem["info"] = service.name
        menuList.add(menuItem)

        menuItem = HashMap()
        menuItem["title"] = "Время"
        menuItem["info"] = date
        menuList.add(menuItem)

        val from = arrayOf<String>("title", "info")
        val to = intArrayOf(R.id.title, R.id.info)

        val adapter = SimpleAdapter(this,
            menuList,
            R.layout.my_list_item_advanced,
            from, to)

        var playButton = findViewById<View>(R.id.button) as Button
        if (barber.id != 0 && service.id != 0 && date != ""){
            playButton.setVisibility(View.VISIBLE)
        } else {
            playButton.setVisibility(View.GONE)
        }

        playButton.setOnClickListener {
            var userText = findViewById<EditText>(R.id.textView).text.toString()

            var reservationData = Reservation(barber.id, service.id, date, userText)
            val apiService = RestApiService()
            apiService.addUser(reservationData) {
                if (it?.barberId != null) {
                    Log.w("success", "asd")
                    var popUp = findViewById<TextView>(R.id.popUp)
                    popUp.setVisibility(View.VISIBLE)

                    NotificationHelper(this,"Вы записались!").Notification()
                    val kh = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                    kh.hideSoftInputFromWindow(currentFocus?.windowToken,0)
                    /**ok set Text msg*/
                    Toast.makeText(this,"is send",Toast.LENGTH_SHORT).show()

                } else {
                    Log.w("Error", "error")
                }
            }
        }

        val listView: ListView = findViewById(R.id.listItems)
        listView.adapter = adapter

        listView.setOnItemClickListener(OnItemClickListener { parent, view, position, id ->
            Log.w("pos", position.toString())
            Log.w("id", id.toString())

            if(position == 0){
                val intent = Intent(applicationContext, BarberActivity::class.java)
                startActivity(intent)
            } else if(position == 1){
                val intent = Intent(applicationContext, ServiceActivity::class.java)
                startActivity(intent)
            } else {
                val intent = Intent(applicationContext, DateActivity::class.java)
                startActivity(intent)
            }
        })
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)

        val sharedPref = this.getPreferences(Context.MODE_PRIVATE) ?: return
        with (sharedPref.edit()) {
            putInt("barberId", barber.id)
            putString("barberName", barber.name)
            putInt("serviceId", service.id)
            putString("serviceName", service.name)
            apply()
        }
    }
}

interface RestApi {

    @Headers("Content-Type: application/json")
    @POST("/api/reservations")
    fun addUser(@Body userData: Reservation): Call<Reservation>
}

data class Reservation(
    @SerializedName("barberId") val barberId: Int?,
    @SerializedName("serviceId") val serviceId: Int?,
    @SerializedName("date") val time: String?,
    @SerializedName("clientName") val user: String?,
)

data class ReservationInfo(
    @SerializedName("ReservationId") val serviceId: Int?,
)

object ServiceBuilder {
    private val client = OkHttpClient.Builder().build()

    private val retrofit = Retrofit.Builder()
        .baseUrl("http://10.0.2.2:5192/") // change this IP for testing by your actual machine IP
        .addConverterFactory(GsonConverterFactory.create())
        .client(client)
        .build()

    fun<T> buildService(service: Class<T>): T{
        return retrofit.create(service)
    }
}

class RestApiService {
    fun addUser(userData: Reservation, onResult: (Reservation?) -> Unit){
        val retrofit = ServiceBuilder.buildService(RestApi::class.java)
        retrofit.addUser(userData).enqueue(
            object : Callback<Reservation> {
                override fun onFailure(call: Call<Reservation>, t: Throwable) {
                    onResult(null)
                }
                override fun onResponse(call: Call<Reservation>, response: Response<Reservation>) {
                    val addedUser = response.body()
                    Log.w("here", response.body().toString())


                    onResult(addedUser)
                }
            }
        )
    }
}

class NotificationHelper(var co:Context,var msg:String) {
    private val CHANNEL_ID = "message id"
    private val NOTIFICATION_ID=123

    fun Notification(){
        createNotificationChannel()
        val senInt = Intent(co,MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingInt = PendingIntent.getActivities(co,0, arrayOf(senInt),0)
        /**set notification Dialog*/
        val icon = BitmapFactory.decodeResource(co.resources,R.drawable.ic_done)
        val isnotification = NotificationCompat.Builder(co,CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_done)
            .setLargeIcon(icon)
            .setContentTitle("BarberShopApp")
            .setContentText(msg)
            .setContentIntent(pendingInt)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .build()
        NotificationManagerCompat.from(co)
            .notify(NOTIFICATION_ID,isnotification)
    }

    private fun createNotificationChannel(){
        if (Build.VERSION.SDK_INT>= Build.VERSION_CODES.O){
            val name = CHANNEL_ID
            val descrip = "Channel description"
            val imports = NotificationManager.IMPORTANCE_DEFAULT
            val cannels = NotificationChannel(CHANNEL_ID,name,imports).apply {
                description = descrip
            }
            val notificationManger = co.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManger.createNotificationChannel(cannels)
        }
    }
}