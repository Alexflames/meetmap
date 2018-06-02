package com.example.test.meetmap

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.beust.klaxon.Klaxon
import com.example.test.meetmap.R.id.eventDateText
import kotlinx.android.synthetic.main.activity_create_event.*
import kotlinx.android.synthetic.main.event_info.*
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*


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

    data class JsonEventInfoObj(
            val name: String,
            val dateD: String,
            val dateM: String,
            val dateY: String,
            val timeH: String,
            val timeM: String,
            val description : String,
            val owner: String,
            val picture: String,
            val latitude: String,
            val longitude: String
    )

    var eventInformation:EventInfoObj = EventInfoObj("noname",1,1,1,1
            , 1,"succ", "nobody", "pic", 1.0, 2.0)

    fun getTimeString(h :String, m : String) :String{
        var resH = h
        var resM = m
        if (h.isEmpty()) resH = "00"
        else if (h.length < 2) resH = "0" + h
        if (m.isEmpty()) resM = "00"
        else if (m.length < 2) resM = "0" + m
        return resH + ":" + resM
    }

    fun getDateString(d :String, m : String, y : String) :String{
        var resD = d
        var resM = m
        if (d.isEmpty()) resD = "00"
        else if (d.length < 2) resD = "0" + d
        if (m.isEmpty()) resM = "00"
        else if (m.length < 2) resM = "0" + m

        return resD + "-" + resM + "-" + y
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.event_info)

        val extras = intent.extras ?: return
        val id = extras.getInt("id")
        //, params = mapOf("id" to id) --- добавить в параметры гета
        khttp.async.get("http://q9315385.beget.tech/meetmap/api/event/read_one.php", params = mapOf("id" to id.toString()),
                headers=mapOf("User-Agent" to "Mozilla/5.0 (compatible; MSIE 9.0; Windows Phone OS 7.5; Trident/5.0; IEMobile/9.0)")){
            println(this.text)
            val jsonEventInf = Klaxon().parse<JsonEventInfoObj>(this.text)!!
            eventInformation = EventInfoObj(jsonEventInf.name, jsonEventInf.dateD.toInt(), jsonEventInf.dateM.toInt(), jsonEventInf.dateY.toInt(),
                    jsonEventInf.timeH.toInt(), jsonEventInf.timeM.toInt(), jsonEventInf.description, jsonEventInf.owner,
                    jsonEventInf.picture, jsonEventInf.latitude.toDouble(), jsonEventInf.longitude.toDouble())
            runOnUiThread {
                eventDateText.text = getDateString(eventInformation.dateD.toString(),
                        eventInformation.dateM.toString(), eventInformation.dateY.toString())
                eventTimeText.text = getTimeString(eventInformation.timeH.toString(),
                        eventInformation.timeM.toString())
                ownerNameText.text = eventInformation.owner
                eventDescriptionText.text = eventInformation.description
                eventNameText.text = eventInformation.name
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
