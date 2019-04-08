/*
 * Copyright (c) 2019. RRatChet.
 * Created by ASLai(laijianhua@rratchet.com).
 *
 * 项目名称：rratchet-sdk-rxbus-trunk
 * 模块名称：app
 *
 * 文件名称：SimpleActivity.kt
 * 文件描述：
 *
 * 创 建 人：ASLai(laijianhua@rratchet.com)
 *
 * 上次修改时间：2019-04-03 11:35:22
 *
 * 修 改 人：ASLai(laijianhua@rratchet.com)
 * 修改时间：2019-04-03 11:35:22
 * 修改备注：
 */

package com.rratchet.sdk.rxbus.demo.ui

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import com.rratchet.sdk.rxbus.BusProvider
import com.rratchet.sdk.rxbus.Subscribe
import com.rratchet.sdk.rxbus.demo.R
import com.rratchet.sdk.rxbus.demo.event.EventInfo
import com.rratchet.sdk.rxbus.demo.widget.ControlView

class SimpleActivity : AppCompatActivity() {

    private var eventInfo = EventInfo()

    private var controlView: ControlView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_simple)

        controlView = findViewById(R.id.control_view)

        // 注册
        BusProvider.getInstance().register(this)
        eventInfo.name = "simple"
    }

    override fun onDestroy() {
        super.onDestroy()
        // 注销
        BusProvider.getInstance().unregister(this)
    }

    fun onPostAction(view: View) {

        eventInfo.time++

        BusProvider.getInstance().post(eventInfo)

        BusProvider.getInstance().post("testSubscribeMethod", "Hi, " + eventInfo.toString())
    }

    fun onClearAction(view: View) {
        controlView?.clear()
    }

    @Subscribe
    fun onEvent(event: Object) {
        if (event is EventInfo) {
            controlView?.print(event.toString())
        }
    }
}
