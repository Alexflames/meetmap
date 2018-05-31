package com.example.test.meetmap

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.beust.klaxon.Klaxon
import com.beust.klaxon.JsonReader
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import java.util.Timer
import kotlin.concurrent.schedule

import com.google.android.gms.maps.CameraUpdateFactory
import khttp.get
import android.widget.Toast
import com.google.android.gms.maps.GoogleMap.OnInfoWindowClickListener;
import com.google.android.gms.maps.GoogleMap.InfoWindowAdapter;
import android.telecom.Call
import android.widget.Button
import com.example.test.meetmap.EventInfoActivity
import com.google.android.gms.maps.model.*
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import khttp.async
import java.io.StringReader
import java.util.*
import kotlin.concurrent.fixedRateTimer


data class eventObject(
        val id: Int,
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

data class jsonEventObject(
        val id: String,
        val name: String,
        val owner: String,
        val tinyPic: String,
        val latitude: String,
        val longitude: String,
        val dateM: String,
        val dateD: String,
        val timeH: String,
        val timeM: String
)


class MapActivity : AppCompatActivity(), OnMapReadyCallback, OnInfoWindowClickListener, GoogleMap.OnMapClickListener {

    private lateinit var mMap: GoogleMap
    private var HARDCODED = false
    private var position = LatLng(51.533373, 46.020923)
    var clickMarker: Marker? = null

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
        val eventId = marker.tag as Int
        intent.putExtra("id", eventId)
        startActivity(intent)
    }

    override fun onMapClick(p0: LatLng?) {
        clickMarker?.remove()
        position = p0!!
        Toast.makeText(this, "Текущая координата ${position.latitude}, ${position.longitude}"
                , Toast.LENGTH_LONG).show()
        clickMarker = mMap.addMarker(MarkerOptions()
                .position(LatLng(position.latitude, position.longitude))
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_VIOLET))
        )
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        // Add a marker in Saratov and move the camera
        val thisEventLat = intent.getDoubleExtra("latitudeCoord", 51.533373)
        val thisEventLng = intent.getDoubleExtra("longitudeCoord", 46.020923)

        clickMarker = mMap.addMarker(MarkerOptions()
                .position(LatLng(thisEventLat, thisEventLng))
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_VIOLET)))

        val extras = intent.extras ?: return
        if (extras.getInt("hasCoordinates") == 1) {
            val eventCoord = LatLng(extras.getDouble("latitudeCoord"), extras.getDouble("longitudeCoord"))
            mMap.moveCamera(CameraUpdateFactory.newLatLng(eventCoord))
            mMap.animateCamera(CameraUpdateFactory.zoomTo(17.0f))
            mMap.addMarker(MarkerOptions().position(eventCoord))
        }
        else {
            mMap.moveCamera(CameraUpdateFactory.newLatLng(LatLng(thisEventLat, thisEventLng)))
            mMap.animateCamera(CameraUpdateFactory.zoomTo(17.0f))
            var mapObjects = arrayListOf<eventObject>()
            if (HARDCODED) { // Значение переменной забито в классе, DEBUG - TRUE, когда серверная готова - FALSE
                mapObjects?.add(eventObject(1, "memes1", "memelord", "mem.png", 51.535373, 46.029223, 5, 11, 17, 51))
                mapObjects?.add(eventObject(2, "memes2", "memelord", "mem.png", 51.533373, 46.024923, 5, 11, 17, 51))
                mapObjects?.add(eventObject(3, "memes3", "memelord", "mem.png", 51.531373, 46.019223, 5, 11, 17, 51))
                mapObjects?.add(eventObject(4, "memes4", "memelord", "mem.png", 51.530373, 46.014223, 5, 11, 17, 51))
            } else {
                // mocky:
                val resp = khttp.async.get("http://q9315385.beget.tech/meetmap/api/event/read.php",
                        headers=mapOf("User-Agent" to "Mozilla/5.0 (compatible; MSIE 9.0; Windows Phone OS 7.5; Trident/5.0; IEMobile/9.0)"), onError = {
                     println("Error message: $message")
                }) {
                    println(this.headers)
                    println("Status Code: $statusCode")
                    // println("Response Text: $text")
                    val klaxon = Klaxon()
                    JsonReader(StringReader(this.text)).use { reader ->
                        reader.beginArray {
                            while (reader.hasNext()) {
                                val thisJsonEvent = klaxon.parse<jsonEventObject>(reader)!!
                                val thisEvent = eventObject(thisJsonEvent.id.toInt(), thisJsonEvent.name, thisJsonEvent.owner, thisJsonEvent.tinyPic,
                                        thisJsonEvent.latitude.toDouble(), thisJsonEvent.longitude.toDouble(), thisJsonEvent.dateM.toInt(),
                                        thisJsonEvent.dateD.toInt(), thisJsonEvent.timeH.toInt(), thisJsonEvent.timeM.toInt())
                                mapObjects.add(thisEvent)
                                // println("Event should be added")
                            }
                        }
                    }
                }
            }

            var lastSize = 0
            // Карта обновляется с некоторой периодичностью. Время в ms.
            val fixedRateTimer = fixedRateTimer(name = "hello-timer",
                    initialDelay = 10000, period = 10000) {
                // println("Timer - CLICK")
                for (i in lastSize until mapObjects!!.size) {
                    val eventToShow = mapObjects[i]
                    val eventText = "ID: ${eventToShow.id}"
                    // println("Event: $i $eventToShow ")

                    runOnUiThread {
                        val thisMarker = mMap.addMarker(MarkerOptions()
                                .position(LatLng(eventToShow.latitude, eventToShow.longitude))
                                .title(eventToShow.name)
                                .snippet(eventText))
                        thisMarker.tag = eventToShow.id
                    }
                }
                lastSize = mapObjects!!.size
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

        mMap.setOnMapClickListener(this)
        var btn_click_create = findViewById<Button>(R.id.btn_create_event)
        btn_click_create.setOnClickListener {
            // var intent = Intent(this, EventInfoActivity::class.java)
            var intent = Intent(this, CreateEventActivity::class.java)
            intent.putExtra("long", position.longitude)
            intent.putExtra("lat", position.latitude)
            startActivity(intent)
        }
    }


}
