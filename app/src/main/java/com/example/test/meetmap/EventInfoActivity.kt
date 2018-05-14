package com.example.test.meetmap

import android.content.Intent
import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.example.test.meetmap.MapActivity
import com.example.test.meetmap.R

class EventInfoActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.event_info)
    }

    fun getEventLocation(v: View){
        val loc = Uri.parse("geo:51.533362, 46.020908")
        val intent = Intent(Intent.ACTION_VIEW, loc, this, MapActivity::class.java)
        intent.putExtra("hasCoordinates", 1)
        startActivity(intent)
    }
}
