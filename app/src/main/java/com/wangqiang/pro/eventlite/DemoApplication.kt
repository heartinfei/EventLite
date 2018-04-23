package com.wangqiang.pro.eventlite

import android.app.Application
import com.passiontec.pos.eventManager.EventMessenger

/**
 *
 * @author 王强 on 2018/4/20 249346528@qq.com
 */
class DemoApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        EventMessenger.init(this)
    }
}