package com.wangqiang.pro.eventlite


import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.passiontec.pos.eventManager.OnEventMessageCallback


/**
 * A simple [Fragment] subclass.
 *
 */
class TestFragment : Fragment(), OnEventMessageCallback {
    private lateinit var tv: TextView

    override fun onEventMessageReceived(msg: Any?): Boolean {
        tv.text = msg.toString()
        return false
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        tv = view.findViewById(R.id.tv)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_test, container, false)
    }
}
