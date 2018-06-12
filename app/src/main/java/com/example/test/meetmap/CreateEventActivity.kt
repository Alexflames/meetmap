package com.example.test.meetmap

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import khttp.async
import khttp.post
import kotlinx.android.synthetic.main.activity_create_event.*
import com.example.test.meetmap.MapActivity
import com.example.test.meetmap.R

class CreateEventActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_event)

        val thisEventLng = intent.getDoubleExtra("long", 0.0)
        val thisEventLat = intent.getDoubleExtra("lat", 0.0)

        val btn_create = findViewById<FloatingActionButton>(R.id.buttonCreate)
        btn_create.setOnClickListener{
            var eventName = findViewById<EditText>(R.id.textEventName).text.toString()
            val eventDate = findViewById<EditText>(R.id.textEventDate).text
            val datePointPos = eventDate.indexOf('.')
            val dateD = eventDate.substring(0 until datePointPos).toInt()
            val dateM = eventDate.substring((datePointPos + 1) until eventDate.length).toInt()
            val eventTime = findViewById<EditText>(R.id.textEventTime).text
            val timePointPos = eventTime.indexOf(':')
            val timeH = eventTime.substring(0 until timePointPos).toInt()
            val timeM = eventTime.substring((timePointPos + 1) until eventTime.length).toInt()
            var eventDesc = findViewById<EditText>(R.id.textEventDescription).text.toString()
            if (eventName == "") eventName = "Безымянное событие"
            if (eventDesc == "") eventDesc = "Описания нет."

            val url = "http://q9315385.beget.tech/meetmap/api/event/create.php"
            val payload = mapOf("name" to eventName,
                    "dateD" to dateD,
                    "dateM" to dateM,
                    "dateY" to 2018,
                    "timeH" to timeH,
                    "timeM" to timeM,
                    "description" to eventDesc,
                    "owner" to "alpha-tester",
                    "picture" to "smth",
                    "latitude" to thisEventLat,
                    "longitude" to thisEventLng)
            async.post(url, json=payload, headers=mapOf("User-Agent" to "Mozilla/5.0 (compatible; MSIE 9.0; Windows Phone OS 7.5; Trident/5.0; IEMobile/9.0)"),
                    onResponse = {
                        println("Post request info: " + this.statusCode)
                        println(this.text)
                        var intent = Intent(this@CreateEventActivity, MapActivity::class.java)
                        intent.putExtra("hasCoordinates", 0)
                        intent.putExtra("longitudeCoord", thisEventLng)
                        intent.putExtra("latitudeCoord", thisEventLat)
                        startActivity(intent)
            },
                    onError = {
                        println("Absolutely not fine!")
                    })
        }

    }
}
