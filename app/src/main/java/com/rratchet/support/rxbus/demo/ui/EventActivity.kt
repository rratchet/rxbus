/*
 * Copyright (c) 2019. RRatChet.
 * Created by ASLai(laijianhua@rratchet.com).
 *
 * 项目名称：rratchet-sdk-rxbus-trunk
 * 模块名称：app
 *
 * 文件名称：EventActivity.kt
 * 文件描述：
 *
 * 创 建 人：ASLai(laijianhua@rratchet.com)
 *
 * 上次修改时间：2019-04-03 15:56:42
 *
 * 修 改 人：ASLai(laijianhua@rratchet.com)
 * 修改时间：2019-04-03 15:56:42
 * 修改备注：
 */

package com.rratchet.support.rxbus.demo.ui

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import com.rratchet.support.rxbus.BusProvider
import com.rratchet.support.rxbus.DefaultSubscriber
import com.rratchet.support.rxbus.Subscribe
import com.rratchet.support.rxbus.demo.event.DefaultEvent
import com.rratchet.support.rxbus.demo.event.EventInfo
import com.rratchet.support.rxbus.demo.widget.ControlView
import io.reactivex.schedulers.Schedulers


class EventActivity : AppCompatActivity() {

    private var defaultEvent = DefaultEvent()
    private var eventInfo = EventInfo()

    private var controlView: ControlView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(com.rratchet.support.rxbus.demo.R.layout.activity_event)

        controlView = findViewById(com.rratchet.support.rxbus.demo.R.id.control_view)


        eventInfo.name = "event"

        val subscriber = DefaultSubscriber.create(DefaultEvent::class.java) {
            // TODO: Do something
            controlView?.print(it.toString())
        }.withFilter {
            // TODO: 对该类型事件进行过滤
            it.data != null
        }.withScheduler(Schedulers.trampoline())


        // 注册
        BusProvider.getInstance().register(this, defaultEvent, subscriber)
        BusProvider.getInstance().register(this)

    }

    @Subscribe(
            tag = "DEFAULT",
            type = "testSubscribeMethod"
    )
    fun onDefaultEvent(obj: Object) {
        control_view.print("onDefaultEvent#" + obj.toString())
    }

    override fun onDestroy() {
        super.onDestroy()
        // 注销
        BusProvider.getInstance().unregister(this)
    }

    fun onPostAction(view: View) {

        eventInfo.time++
        defaultEvent.data = eventInfo

        BusProvider.getInstance().post(defaultEvent)
    }

    fun onClearAction(view: View) {
        controlView?.clear()
    }
}
