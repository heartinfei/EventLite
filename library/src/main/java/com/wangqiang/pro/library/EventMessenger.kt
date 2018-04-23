package com.passiontec.pos.eventManager

import android.app.Activity
import android.app.Application
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v7.app.AppCompatActivity
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ConcurrentLinkedQueue

/**
 * 内部消息通知接口
 * @author 王强 on 2018/4/12 249346528@qq.com
 */
interface OnEventMessageCallback {
    /**
     * @param msg 消息
     * @return true消息被消耗，不再传递
     */
    fun onEventMessageReceived(msg: Any?): Boolean
}

/**
 *
 * @author 王强 on 2018/4/12 249346528@qq.com
 */
class EventMessenger private constructor() : FragmentManager.FragmentLifecycleCallbacks(), Application.ActivityLifecycleCallbacks {

    companion object {
        /**
         * 回调接口
         */
        @JvmStatic
        private val eventCallbacks = ConcurrentHashMap<String, OnEventMessageCallback>()

        /**
         * 当前活动的回调接口
         */
        @JvmStatic
        private val activedEventCallbacks = ConcurrentLinkedQueue<OnEventMessageCallback>()

        /**
         * 消息
         */
        @JvmStatic
        private val stickEventMessage = ConcurrentHashMap<String, LinkedList<Any>>()

        @JvmStatic
        private val instance = EventMessenger()

        @JvmStatic
        fun init(app: Application) {
            app.registerActivityLifecycleCallbacks(instance)
        }

        @JvmStatic
        fun registObserver(activity: AppCompatActivity) {
            activity.supportFragmentManager.registerFragmentLifecycleCallbacks(instance, false)
        }

        @JvmStatic
        fun unregistObserver(activity: AppCompatActivity) {
            activity.supportFragmentManager.unregisterFragmentLifecycleCallbacks(instance)
        }

        @JvmStatic
        fun cleanMessage() {
            stickEventMessage.clear()
        }

        /**
         * 发送即时消息
         *
         * @param msg    消息
         * @param targets 目标-- 接收类
         */
        @JvmStatic
        fun sendEvent(msg: Any, vararg targets: Class<*>) {
            checkTarget(*targets)
            if (targets.isEmpty()) {
                for (activedEventCallback in activedEventCallbacks) {
                    activedEventCallback.onEventMessageReceived(msg)
                }
            } else {
                dispatchMessage(listOf(msg), *targets)
            }
        }

        /**
         * 发送粘性（跨页面）消息
         *
         * @param msg    消息
         * @param targets 目标 -- 接收类
         */
        @JvmStatic
        fun sendStickEvent(msg: Any, vararg targets: Class<*>) {
            checkTarget(*targets)
            pushMessage(msg, *targets)
        }

        private fun checkTarget(vararg targets: Class<*>) {
            for (target in targets) {
                if (!(AppCompatActivity::class.java.isAssignableFrom(target)
                                || Fragment::class.java.isAssignableFrom(target))) {
                    throw RuntimeException("Only support send message to Activity/Fragment!")
                }
            }
        }


        private fun pushMessage(msg: Any, vararg targets: Class<*>) {
            for (target in targets) {
                val key = getKey(target)
                if (stickEventMessage[key] == null) {
                    stickEventMessage[key] = LinkedList()
                }
                stickEventMessage[key]?.add(msg)
            }
        }

        private fun dispatchMessage(msgs: List<Any>, vararg targets: Class<*>) {
            for (target in targets) {
                for (msg in msgs) {
                    val key = getKey(target)
                    if (eventCallbacks[key]?.onEventMessageReceived(msg) == true) {
                        stickEventMessage[key]?.remove(key)
                    }
                }
            }
        }


        private fun getKey(obj: Class<*>): String {
            return obj.name
        }
    }

    override fun onFragmentResumed(fm: FragmentManager?, f: Fragment) {
        super.onFragmentResumed(fm, f)
        if (f is OnEventMessageCallback) {
            val key = getKey(f::class.java)
            eventCallbacks[key] = f
            if (activedEventCallbacks.peek() == f.activity) {
                activedEventCallbacks.add(f)
            }
            dispatchMessage(stickEventMessage[key] ?: listOf(), f::class.java)
        }
    }

    override fun onFragmentPaused(fm: FragmentManager?, f: Fragment) {
        super.onFragmentPaused(fm, f)
        if (f is OnEventMessageCallback) {
            eventCallbacks.remove(getKey(f::class.java))
            activedEventCallbacks.remove(f)
        }
    }


    override fun onActivityResumed(act: Activity) {
        if (act is OnEventMessageCallback) {
            val key = getKey(act::class.java)
            eventCallbacks[key] = act
            activedEventCallbacks.add(act)
            dispatchMessage(stickEventMessage[key] ?: listOf(), act::class.java)
        }

//        if (act is MainActivity) {
//            stickEventMessage.clear()
//        }
    }

    override fun onActivityPaused(activity: Activity) {
        if (activity is OnEventMessageCallback) {
            activedEventCallbacks.clear()
            eventCallbacks.remove(getKey(activity::class.java))
        }
    }

    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) = Unit

    override fun onActivityStarted(activity: Activity) = Unit

    override fun onActivityStopped(activity: Activity) = Unit

    override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle?) = Unit

    override fun onActivityDestroyed(activity: Activity) = Unit
}