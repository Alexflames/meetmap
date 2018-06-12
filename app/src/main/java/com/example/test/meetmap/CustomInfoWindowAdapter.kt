package com.example.test.meetmap

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.Marker
import org.w3c.dom.Text

class CustomInfoWindowAdapter(context: Context) : GoogleMap.InfoWindowAdapter{
    private var mContext = context
    private var mWindow = LayoutInflater.from(context).inflate(R.layout.custom_info_window, null)

    private fun renderWindowText(marker: Marker, view: View) {
        val title = marker.title
        val tvTitle = view.findViewById<TextView>(R.id.title)

        if (title != "") {
            tvTitle.text = title
        }

        val snippet = marker.snippet
        val tvSnippet = view.findViewById<TextView>(R.id.snippet)

        if (snippet != "") {
            tvSnippet.text = snippet
        }
    }
    override fun getInfoWindow(p0: Marker?): View {
        renderWindowText(p0!!, mWindow!!)
        return mWindow!!
    }

    override fun getInfoContents(p0: Marker?): View {
        renderWindowText(p0!!, mWindow!!)
        return mWindow!!
    }
}