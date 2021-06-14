package com.alyndroid.thirdeyechecklist.fcm

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import com.alyndroid.thirdeyechecklist.ui.notification.AlertReceiver
import com.alyndroid.thirdeyechecklist.ui.notification.NotificationHelper
import com.alyndroid.thirdeyechecklist.util.SharedPreference
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class FireBaseMessagingService : FirebaseMessagingService() {

    lateinit var notificationHelper: NotificationHelper

    override fun onCreate() {
        super.onCreate()
        notificationHelper = NotificationHelper(this)
    }

    override fun onNewToken(newToken: String) {
        SharedPreference(this).save("fireBaseToken", newToken)
        Log.d("fireBaseService", newToken)
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        remoteMessage.data.get("message")
        remoteMessage.data.get("checklist_name")
        remoteMessage.data.get("created_by")
        remoteMessage.data.get("time_in_mills")
        Log.d("fireBaseService", "message")
        val notificationHelper = NotificationHelper(this)
        val nb = notificationHelper.getChannelNotification(
            "new checklist",
            remoteMessage.data["created_by"] + " has created " + remoteMessage.data
                .get("checklist_name") + " to you"
        )
        startAlarm(
            remoteMessage.data["time_in_mills"]!!.toLong(),
            remoteMessage.data["checklist_name"].toString(),
            "this checklist starts now"
        )
        notificationHelper.manager.notify(1, nb.build())
    }

    private fun startAlarm(c: Long, title: String, content: String) {
        val alarmManager = this.getSystemService(Context.ALARM_SERVICE) as? AlarmManager
        val intent = Intent(this, AlertReceiver::class.java)
        intent.putExtra("title", title)
        intent.putExtra("content", content)
        val pendingIntent = PendingIntent.getBroadcast(this, 1, intent, 0)
        when {
            Build.VERSION.SDK_INT >= 23 -> {
                alarmManager!!.setExactAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    c,
                    pendingIntent
                )
            }
            Build.VERSION.SDK_INT >= 19 -> {
                alarmManager!!.setExact(AlarmManager.RTC_WAKEUP, c, pendingIntent)
            }
            else -> {
                alarmManager!!.set(AlarmManager.RTC_WAKEUP, c, pendingIntent)
            }
        }

    }

}