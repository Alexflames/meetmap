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
            val eventName = findViewById<EditText>(R.id.textEventName).text.toString()
            val eventDate = findViewById<EditText>(R.id.textEventDate).text
            val dateD = (if (eventDate[1] == '.') eventDate[0].toString() else eventDate.substring(0..1)).toInt()
            val dateM = (if (eventDate[1] == '.') eventDate.substring(2..(eventDate.length - 1)) else eventDate.substring(3..(eventDate.length - 1))).toInt()
            val eventTime = findViewById<EditText>(R.id.textEventTime).text
            val timeH = (if (eventDate[1] == '.') eventTime[0].toString() else eventTime.substring(0..1)).toInt()
            val timeM = (if (eventDate[1] == '.') eventTime.substring(2..(eventTime.length - 1)) else eventTime.substring(3..(eventTime.length - 1))).toInt()
            val eventDesc = findViewById<EditText>(R.id.textEventDescription).text.toString()

            val url = "http://www.mocky.io/v2/5af9b9ad2e00008c00278d5b"
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
            async.post(url, json=payload,
                    onResponse = {
                        println(thisEventLat)
                        println(thisEventLng)
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
