package com.example.test.meetmap

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.beust.klaxon.Klaxon

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import khttp.get
import android.widget.Toast
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.GoogleMap.OnInfoWindowClickListener;
import com.google.android.gms.maps.GoogleMap.InfoWindowAdapter;
import com.google.android.gms.maps.model.LatLngBounds;
import android.content.Intent
import com.example.test.meetmap.EventInfoActivity

data class eventObject(
        val id: Long,
        val name: String,
        val owner: String,
        val tinyPic: String,
        val latitude: Double,
        val longitude: Double,
        val dateM: Short,
        val dateD: Short,
        val timeH: Short,
        val timeM: Short
)


class MapActivity : AppCompatActivity(), OnMapReadyCallback, OnInfoWindowClickListener {

    private lateinit var mMap: GoogleMap
    private var HARDCODED = true

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
            var mapObjects : MutableList<eventObject>? = ArrayList()
            if (HARDCODED) { // Значение переменной забито в классе, DEBUG - TRUE, когда серверная готова - FALSE
                mapObjects?.add(eventObject(1, "memes1", "memelord", "mem.png", 51.535373, 46.029223, 5, 11, 17, 51))
                mapObjects?.add(eventObject(2, "memes2", "memelord", "mem.png", 51.533373, 46.024923, 5, 11, 17, 51))
                mapObjects?.add(eventObject(3, "memes3", "memelord", "mem.png", 51.531373, 46.019223, 5, 11, 17, 51))
                mapObjects?.add(eventObject(4, "memes4", "memelord", "mem.png", 51.530373, 46.014223, 5, 11, 17, 51))
            }
            else {
                val resp = khttp.get("https://api.github.com/events")   // get from url
                mapObjects = Klaxon().parse<MutableList<eventObject>>(resp.jsonArray.toString()) // parse JSON
                for (i in 0 until mapObjects!!.size) {
                    val eventToShow = mapObjects[i]
                    mMap.addMarker(MarkerOptions().position(LatLng(eventToShow.latitude, eventToShow.longitude)).title(eventToShow.name))
                }
            }
            for (i in 0 until mapObjects!!.size) {
                val eventToShow = mapObjects[i]
                mMap.addMarker(MarkerOptions().position(LatLng(eventToShow.latitude, eventToShow.longitude)).title(eventToShow.name))
            }
        }

        // eventObject(0, "memes", "memelord", "mem.png", 50.0101, 101.0505, 5, 11, 17, 51)


        mMap.setOnInfoWindowClickListener(this)     // "Слушание" события при нажатии

    }


}
