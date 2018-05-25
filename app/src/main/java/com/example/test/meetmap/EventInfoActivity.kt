package com.example.test.meetmap

import android.content.Intent
import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.beust.klaxon.JsonReader
import com.beust.klaxon.Klaxon
import com.example.test.meetmap.MapActivity
import com.example.test.meetmap.R
import kotlinx.android.synthetic.main.event_info.*
import java.io.StringReader


class EventInfoActivity : AppCompatActivity() {
    data class EventInfoObj(
            val name: String,
            val dateD: Int,
            val dateM: Int,
            val dateY: Int,
            val timeH: Int,
            val timeM: Int,
            val description : String,
            val owner: String,
            val picture: String,
            val latitude: Double,
            val longitude: Double
    )

    var eventInformation:EventInfoObj = EventInfoObj("noname",1,1,1,1
            , 1,"succ", "nobody", "pic", 1.0, 2.0)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.event_info)

        val extras = intent.extras ?: return
        val id = extras.getString("id")
        //, params = mapOf("id" to id) --- добавить в параметры гета
        khttp.async.get("http://www.mocky.io/v2/5aff30233200009c002231c6"){
            eventInformation = Klaxon().parse<EventInfoObj>(this.text)!!
            runOnUiThread {
                textView.text = eventInformation.description
                textView2.text = eventInformation.owner
                progressBar.visibility = View.INVISIBLE
            }
        }
    }

    fun getEventLocation(v: View){
        val intent = Intent(this, MapActivity::class.java)
        intent.putExtra("hasCoordinates", 1)
        intent.putExtra("longitudeCoord", eventInformation.longitude)
        intent.putExtra("latitudeCoord", eventInformation.latitude)
        startActivity(intent)
    }
}
