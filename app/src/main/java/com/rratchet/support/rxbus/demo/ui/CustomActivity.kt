/*
 * Copyright (c) 2019. RRatChet.
 * Created by ASLai(laijianhua@rratchet.com).
 *
 * 项目名称：rratchet-sdk-rxbus-trunk
 * 模块名称：app
 *
 * 文件名称：CustomActivity.kt
 * 文件描述：
 *
 * 创 建 人：ASLai(laijianhua@rratchet.com)
 *
 * 上次修改时间：2019-04-03 15:00:51
 *
 * 修 改 人：ASLai(laijianhua@rratchet.com)
 * 修改时间：2019-04-03 15:00:51
 * 修改备注：
 */

package com.rratchet.support.rxbus.demo.ui

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.rratchet.support.rxbus.BusProvider
import com.rratchet.support.rxbus.DefaultSubscriber
import com.rratchet.support.rxbus.demo.R
import com.rratchet.support.rxbus.demo.event.EventInfo
import com.rratchet.support.rxbus.demo.widget.ControlView

class CustomActivity : AppCompatActivity() {

    private var eventInfo = EventInfo()

    private var controlView: ControlView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_custom)

        controlView = findViewById(R.id.control_view)
        eventInfo.name = "custom"

        var subscriber = DefaultSubscriber.create(EventInfo::class.java) {
            controlView?.print(it?.toString())
        }
        BusProvider.getInstance().register(this, subscriber)
    }

    override fun onDestroy() {
        super.onDestroy()
        // 注销
        BusProvider.getInstance().unregister(this)
    }

    fun onPostAction(view: View) {

        eventInfo.time++

        BusProvider.getInstance().post(eventInfo)
    }

    fun onClearAction(view: View) {
        controlView?.clear()
    }
}
