package com.wangqiang.pro.eventlite

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import com.passiontec.pos.eventManager.EventMessenger
import com.passiontec.pos.eventManager.OnEventMessageCallback
import com.wangqiang.pro.eventlite.message.TestMessage

class MainActivity : AppCompatActivity(), OnEventMessageCallback, View.OnClickListener {
    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btn1 -> {
                EventMessenger.sendEvent(TestMessage("Msg from MainActivity"))
            }
            else -> {
                EventMessenger.sendStickEvent(TestMessage("Msg from MainActivity"))
            }
        }
    }

    override fun onEventMessageReceived(msg: Any?): Boolean {
        return true
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        EventMessenger.registObserver(this)
    }

    override fun onStop() {
        super.onStop()
        EventMessenger.unregistObserver(this)
    }
}
