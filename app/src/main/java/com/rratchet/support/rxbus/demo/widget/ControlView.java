/*
 * Copyright (c) 2019. RRatChet.
 * Created by ASLai(laijianhua@rratchet.com).
 *
 * 项目名称：rratchet-sdk-rxbus-trunk
 * 模块名称：app
 *
 * 文件名称：ControlPanel.java
 * 文件描述：
 *
 * 创 建 人：ASLai(laijianhua@rratchet.com)
 *
 * 上次修改时间：2019-04-03 11:39:22
 *
 * 修 改 人：ASLai(laijianhua@rratchet.com)
 * 修改时间：2019-04-03 11:39:22
 * 修改备注：
 */

package com.rratchet.support.rxbus.demo.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.HorizontalScrollView;
import android.widget.ScrollView;
import android.widget.TextView;

/**
 * <pre>
 *
 *      作 者 :        ASLai(laijianhua@ruixiude.com).
 *      日 期 :        2019/4/3
 *      版 本 :        V1.0
 *      描 述 :        控制台视图，负责显示输出
 *
 *
 * </pre>
 *
 * @author ASLai
 */
public class ControlView extends ScrollView {

    private TextView             mContentView;
    private HorizontalScrollView mHorizontalScrollView;

    public ControlView(Context context) {
        super(context);
        init(context);
    }

    public ControlView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public ControlView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        LayoutParams layoutParams = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);

        mContentView = new TextView(context);
        mContentView.setLayoutParams(layoutParams);

        mHorizontalScrollView = new HorizontalScrollView(context);
        mHorizontalScrollView.setLayoutParams(layoutParams);
        mHorizontalScrollView.addView(mContentView);

        setPadding(16, 16, 16, 16);
        this.addView(mHorizontalScrollView);
    }

    /**
     * 打印数据
     *
     * @param contents
     */
    public void print(String... contents) {


        if (contents != null) {
            for (String content : contents) {
                mContentView.append(content);
                mContentView.append("\n");
            }
        } else {
            mContentView.append("\n");
        }

        fullScroll(View.FOCUS_DOWN);
    }

    public void clear() {
        mContentView.setText(null);
    }
}
