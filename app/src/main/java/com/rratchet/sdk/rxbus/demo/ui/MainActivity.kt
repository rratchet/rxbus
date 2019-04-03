/*
 * Copyright (c) 2019. RRatChet.
 * Created by ASLai(laijianhua@rratchet.com).
 *
 * 项目名称：rratchet-sdk-rxbus-trunk
 * 模块名称：app
 *
 * 文件名称：MainActivity.kt
 * 文件描述：
 *
 * 创 建 人：ASLai(laijianhua@rratchet.com)
 *
 * 上次修改时间：2019-04-03 11:12:25
 *
 * 修 改 人：ASLai(laijianhua@rratchet.com)
 * 修改时间：2019-04-03 11:34:49
 * 修改备注：
 */

package com.rratchet.sdk.rxbus.demo.ui

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import com.rratchet.sdk.rxbus.BusProvider
import com.rratchet.sdk.rxbus.Subscribe
import com.rratchet.sdk.rxbus.demo.R
import kotlinx.android.synthetic.main.activity_simple.*

/**
 *
 * <pre>
 *
 *      作 者 :        ASLai(laijianhua@ruixiude.com).
 *      日 期 :        2019/4/3
 *      版 本 :        V1.0
 *      描 述 :        description
 *
 *
 * </pre>
 *
 *
 * @author ASLai
 *
 */
class MainActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        BusProvider.getInstance().register(this)
    }

    override fun onDestroy() {
        super.onDestroy()
        BusProvider.getInstance().unregister(this)
    }

    @Subscribe
    fun onEvent(obj: Object) {
        control_view.print(obj.toString())
    }

    fun onSimpleAction(view: View) {

        val intent = Intent(this, SimpleActivity::class.java)
        startActivity(intent)

    }

    fun onCustomAction(view: View) {

        val intent = Intent(this, CustomActivity::class.java)
        startActivity(intent)
    }

    fun onEventAction(view: View) {

        val intent = Intent(this, EventActivity::class.java)
        startActivity(intent)
    }
}