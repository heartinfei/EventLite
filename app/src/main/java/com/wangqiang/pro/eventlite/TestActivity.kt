package com.wangqiang.pro.eventlite

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.TextView
import com.passiontec.pos.eventManager.OnEventMessageCallback

class TestActivity : AppCompatActivity(), OnEventMessageCallback {

    private lateinit var tv: TextView

    override fun onEventMessageReceived(msg: Any?): Boolean {
        tv.text = msg.toString()
        return true
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_test)
        tv = findViewById(R.id.tv)
    }
}
