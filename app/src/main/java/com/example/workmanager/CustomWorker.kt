package com.example.workmanager

import android.content.Context
import android.util.Log
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.Data
import androidx.work.WorkerParameters
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import java.net.UnknownHostException

@HiltWorker
class CustomWorker @AssistedInject constructor(
    @Assisted private val api: JsonPlaceholderApi,
    @Assisted context: Context,
    @Assisted workerParameters: WorkerParameters
): CoroutineWorker(context, workerParameters) {

    override suspend fun doWork(): Result {
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

}