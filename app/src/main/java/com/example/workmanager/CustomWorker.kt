package com.example.workmanager

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.Data
import androidx.work.ForegroundInfo
import androidx.work.WorkerParameters
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.delay
import java.net.UnknownHostException

@HiltWorker
class CustomWorker @AssistedInject constructor(
    @Assisted private val api: JsonPlaceholderApi,
    @Assisted context: Context,
    @Assisted workerParameters: WorkerParameters
): CoroutineWorker(context, workerParameters) {

    override suspend fun doWork(): Result {
        setForeground(getForegroundInfo(applicationContext))
        delay(10000)
        return try {
            val response = api.getPost()
            if (response.isSuccessful) {
                Log.e("CustomWorker", "success")
                Log.e("CustomWorker", "Id: ${response.body()?.id} Title: ${response.body()?.title}")
                Result.success()
            } else {
                Log.e("CustomWorker", "retrying")
                Result.retry()
            }
        } catch (e: Exception) {
            if (e is UnknownHostException) {
                Log.e("CustomWorker", "retrying")
                Result.retry()
            } else {
                Log.e("CustomWorker", "error")
                Result.failure(Data.Builder().putString("error", e.toString()).build())
            }
        }
    }

    override suspend fun getForegroundInfo(): ForegroundInfo {
        return getForegroundInfo(applicationContext)
    }

}
private fun getForegroundInfo(context: Context): ForegroundInfo {
    return ForegroundInfo(
        1,
        createNotification(context)
    )
}

private fun createNotification(context: Context): Notification {
    val channelId = "main_channel_id"
    val channelName = "Main Channel"

    val builder = NotificationCompat.Builder(context, channelId)
        .setSmallIcon(R.drawable.ic_launcher_background)
        .setContentTitle("Notification Title")
        .setContentText("This is a test notification.")
        .setOngoing(true)
        .setAutoCancel(true)

    val notificationManager =
        context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val channel = NotificationChannel(
            channelId,
            channelName,
            NotificationManager.IMPORTANCE_DEFAULT
        )
        notificationManager.createNotificationChannel(channel)
    }

    return builder.build()
}
