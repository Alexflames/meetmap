package com.example.test.meetmap

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.beust.klaxon.Klaxon
import com.beust.klaxon.JsonReader
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.Marker
import java.util.Timer
import kotlin.concurrent.schedule

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import khttp.get
import android.widget.Toast
import com.google.android.gms.maps.GoogleMap.OnInfoWindowClickListener;
import com.google.android.gms.maps.GoogleMap.InfoWindowAdapter;
import com.google.android.gms.maps.model.LatLngBounds;
import android.telecom.Call
import com.example.test.meetmap.EventInfoActivity
import okhttp3.OkHttpClient
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import com.github.kittinunf.fuel.httpGet
import khttp.async
import java.io.StringReader
import java.util.*
import kotlin.concurrent.fixedRateTimer


data class eventObject(
        val id: Long,
        val name: String,
        val owner: String,
        val tinyPic: String,
        val latitude: Double,
        val longitude: Double,
        val dateM: Int,
        val dateD: Int,
        val timeH: Int,
        val timeM: Int
)


class MapActivity : AppCompatActivity(), OnMapReadyCallback, OnInfoWindowClickListener {

    private lateinit var mMap: GoogleMap
    private var HARDCODED = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_map)
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
                .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

    }

    override fun onInfoWindowClick(marker: Marker) {
        var intent = Intent(this, EventInfoActivity::class.java)
        startActivity(intent)
        /**
        Toast.makeText(this, "Info window clicked",
                Toast.LENGTH_SHORT).show()
        /** Вместо написанного сверху по идее надо добавить intent перевода на другую activity
         *  в параметры закинуть ID мероприятия. А на активите информации ловить intent,
         *  который по данному ID запросит с сервера информацию об event. Чечня - круто!
         */  */
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        // Add a marker in Saratov and move the camera
        val saratovKirova = LatLng(51.533373, 46.020923)
        mMap.addMarker(MarkerOptions().position(saratovKirova).title("Marker in Saratov"))

        val extras = intent.extras ?: return
        if (extras.getInt("hasCoordinates") == 1) {
            mMap.moveCamera(CameraUpdateFactory.newLatLng(saratovKirova))
            mMap.animateCamera(CameraUpdateFactory.zoomTo(17.0f))
        }
        else {
            mMap.moveCamera(CameraUpdateFactory.newLatLng(saratovKirova))
            mMap.animateCamera(CameraUpdateFactory.zoomTo(17.0f))
            var mapObjects = arrayListOf<eventObject>()
            if (HARDCODED) { // Значение переменной забито в классе, DEBUG - TRUE, когда серверная готова - FALSE
                mapObjects?.add(eventObject(1, "memes1", "memelord", "mem.png", 51.535373, 46.029223, 5, 11, 17, 51))
                mapObjects?.add(eventObject(2, "memes2", "memelord", "mem.png", 51.533373, 46.024923, 5, 11, 17, 51))
                mapObjects?.add(eventObject(3, "memes3", "memelord", "mem.png", 51.531373, 46.019223, 5, 11, 17, 51))
                mapObjects?.add(eventObject(4, "memes4", "memelord", "mem.png", 51.530373, 46.014223, 5, 11, 17, 51))
            } else {
                khttp.async.get("http://www.mocky.io/v2/5af5c5833100002c000025c8", onError = {
                    // println("Error message: $message")
                }) {
                    // println("Status Code: $statusCode")
                    // println("Response Text: $text")
                    val klaxon = Klaxon()
                    JsonReader(StringReader(this.text)).use { reader ->
                        reader.beginArray {
                            while (reader.hasNext()) {
                                val person = klaxon.parse<eventObject>(reader)!!
                                mapObjects.add(person)
                                // println("Event should be added")
                            }
                        }
                    }
                }


            }
            // Карта обновляется с некоторой периодичностью. Время в ms.
            val fixedRateTimer = fixedRateTimer(name = "hello-timer",
                    initialDelay = 10000, period = 10000) {
                // println("Timer - CLICK")
                runOnUiThread { mMap.clear() }

                for (i in 0 until mapObjects!!.size) {
                    val eventToShow = mapObjects[i]
                    // println("Event: $i $eventToShow ")
                    runOnUiThread {
                        mMap.addMarker(MarkerOptions().position(LatLng(eventToShow.latitude, eventToShow.longitude)).title(eventToShow.name))
                    }
                }
            }
            try {
                // Функция при запуске
                // println("I am trying!")
            } finally {
                // Переводит сюда, и здесь можно специальной командой отключить таймер
                // println("finally?")
            }
        }

        // eventObject(0, "memes", "memelord", "mem.png", 50.0101, 101.0505, 5, 11, 17, 51)
        mMap.setOnInfoWindowClickListener(this)     // "Слушание" события при нажатии

    }


}
