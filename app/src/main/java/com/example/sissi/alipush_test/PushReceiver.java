/*
 * Copyright (c) 2018 it.kedacom.com, Inc. All rights reserved.
 */

package com.example.sissi.alipush_test;

import android.content.Context;

import com.alibaba.sdk.android.push.MessageReceiver;
import com.alibaba.sdk.android.push.notification.CPushMessage;

import java.util.Map;


public class PushReceiver extends MessageReceiver {

    /**
     * 推送通知的回调方法
     * @param context
     * @param title
     * @param summary
     * @param extraMap
     */
    //这个只针对阿里推送的通知，如果是收到消息后自定义通知则不会走该回调
    @Override
    public void onNotification(Context context, String title, String summary, Map<String, String> extraMap) {
        // TODO 处理推送通知
        if ( null != extraMap ) {
            for (Map.Entry<String, String> entry : extraMap.entrySet()) {
                PcTrace.p("@Get diy param : Key=" + entry.getKey() + " , Value=" + entry.getValue());
            }
        } else {
            PcTrace.p("@收到通知 && 自定义消息为空");
        }
        PcTrace.p("onNotify{title:%s, body:%s}", title, summary);
    }

    /**
     * 应用处于前台时通知到达回调。注意:该方法仅对自定义样式通知有效,相关详情请参考https://help.aliyun.com/document_detail/30066.html?spm=5176.product30047.6.620.wjcC87#h3-3-4-basiccustompushnotification-api
     * @param context
     * @param title
     * @param summary
     * @param extraMap
     * @param openType
     * @param openActivity
     * @param openUrl
     */
    @Override
    protected void onNotificationReceivedInApp(Context context, String title, String summary, Map<String, String> extraMap, int openType, String openActivity, String openUrl) {
        PcTrace.p("onNotificationReceivedInApp ： " + " : " + title + " : " + summary + "  " + extraMap + " : " + openType + " : " + openActivity + " : " + openUrl);
    }

    /**
     * 推送消息的回调方法
     * @param context
     * @param cPushMessage
     */
    @Override
    public void onMessage(Context context, CPushMessage cPushMessage) {
        PcTrace.p("onMessage{title:%s, body:%s}", cPushMessage.getTitle(), cPushMessage.getContent());
    }

    /**
     * 从通知栏打开通知的扩展处理
     * @param context
     * @param title
     * @param summary
     * @param extraMap
     */
    //这个只针对阿里推送的通知，如果是收到消息后自定义通知则不会走该回调
    @Override
    public void onNotificationOpened(Context context, String title, String summary, String extraMap) {
        PcTrace.p("onNotifyOpened{title:%s, body:%s, extraMap=%s}", title, summary, extraMap);
    }

    /**
     * 通知删除回调
     * @param context
     * @param messageId
     */
    //这个只针对阿里推送的通知，如果是收到消息后自定义通知则不会走该回调
    @Override
    public void onNotificationRemoved(Context context, String messageId) {
        PcTrace.p( "onNotificationRemoved ： " + messageId);
    }

    /**
     * 无动作通知点击回调。当在后台或阿里云控制台指定的通知动作为无逻辑跳转时,通知点击回调为onNotificationClickedWithNoAction而不是onNotificationOpened
     * @param context
     * @param title
     * @param summary
     * @param extraMap
     */
    //这个只针对阿里推送的通知，如果是收到消息后自定义通知则不会走该回调
    @Override
    protected void onNotificationClickedWithNoAction(Context context, String title, String summary, String extraMap) {
        PcTrace.p("onNotificationClickedWithNoAction ： " + " : " + title + " : " + summary + " : " + extraMap);
    }
}