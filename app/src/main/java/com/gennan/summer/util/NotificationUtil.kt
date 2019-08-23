package com.gennan.summer.util

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.media.RingtoneManager
import androidx.core.app.NotificationCompat
import com.gennan.summer.R
import com.gennan.summer.app.CoolChatApp
import com.gennan.summer.util.Constants.Companion.APP_NAME

/**
 *Created by Gennan on 2019/8/23.
 * 还是有点迷茫到底要不要添加Notification 毕竟一退出进程就几乎被杀死了...
 */

object NotificationUtil {
    fun createNotification(notificationContent: String, notificationId: Int, channelId: String, channelName: String) {
        val builder = NotificationCompat.Builder(CoolChatApp.getAppContext())
        builder.setSmallIcon(R.drawable.logo)//设置通知的icon
        builder.setContentTitle(APP_NAME)//设置通知的标题
        builder.setContentText(notificationContent)//设置通知的内容
        builder.setWhen(System.currentTimeMillis())
//        //设置通知的点击事件
//        val intent = Intent(CoolChatApp.getAppContext(), MainActivity::class.java)
//        val pendingIntent =
//            PendingIntent.getActivity(CoolChatApp.getAppContext(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
//        builder.setContentIntent(pendingIntent)
        builder.setAutoCancel(true)

        // 设置通知的优先级
        builder.priority = NotificationCompat.PRIORITY_MAX
        val alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        // 设置通知的提示音
        builder.setSound(alarmSound)
        //指定应用唯一的id
        val notificationManager =
            CoolChatApp.getAppContext()?.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        //Android8.0要求设置通知渠道
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            val channel = NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_HIGH)
            notificationManager.createNotificationChannel(channel)
            builder.setChannelId(channelId)
        }
        notificationManager.notify(notificationId, builder.build())
    }
}