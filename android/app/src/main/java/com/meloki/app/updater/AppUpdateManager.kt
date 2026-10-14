package com.meloki.app.updater

import android.app.DownloadManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.Uri
import android.os.Build
import android.os.Environment
import androidx.core.content.FileProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.io.File
import java.net.HttpURLConnection
import java.net.URL

/**
 * OTA (Over-The-Air) Uzaktan Otomatik Güncelleme Yöneticisi
 * Uygulamayı her geliştirdiğinizde sevgilinizin veya sizin yeniden Android Studio / USB bağlamanıza gerek kalmaz!
 * GitHub Releases veya Firebase üzerinden yeni APK çıktığında ekrana "Yeni Güncelleme Mevcut" butonu çıkar ve
 * tek dokunuşla APK indirilip otomatik kurulur.
 */
data class UpdateInfo(
    val hasUpdate: Boolean,
    val latestVersionCode: Int,
    val latestVersionName: String,
    val apkDownloadUrl: String,
    val releaseNotes: String
)

object AppUpdateManager {

    // Buraya GitHub Releases veya sunucu JSON bağlantınız gelir
    private const val VERSION_CHECK_URL = "https://raw.githubusercontent.com/meloki-app/updates/main/version.json"

    /**
     * Uzaktaki en son sürümü kontrol eder
     */
    suspend fun checkForUpdates(currentVersionCode: Int = 1): UpdateInfo = withContext(Dispatchers.IO) {
        try {
            val url = URL(VERSION_CHECK_URL)
            val connection = url.openConnection() as HttpURLConnection
            connection.connectTimeout = 5000
            connection.readTimeout = 5000

            if (connection.responseCode == 200) {
                val jsonString = connection.inputStream.bufferedReader().readText()
                val json = JSONObject(jsonString)

                val remoteCode = json.getInt("versionCode")
                val remoteName = json.getString("versionName")
                val apkUrl = json.getString("apkUrl")
                val notes = json.optString("releaseNotes", "Yeni özellikler ve hata düzeltmeleri.")

                return@withContext UpdateInfo(
                    hasUpdate = remoteCode > currentVersionCode,
                    latestVersionCode = remoteCode,
                    latestVersionName = remoteName,
                    apkDownloadUrl = apkUrl,
                    releaseNotes = notes
                )
            }
        } catch (e: Exception) {
            // Çevrimdışıysa veya henüz JSON yüklenmediyse sessizce devam et
        }

        return@withContext UpdateInfo(
            hasUpdate = false,
            latestVersionCode = 1,
            latestVersionName = "1.0.0",
            apkDownloadUrl = "",
            releaseNotes = ""
        )
    }

    /**
     * Yeni APK'yı Android DownloadManager ile indirir ve otomatik yükleme ekranını açar.
     */
    fun downloadAndInstallUpdate(context: Context, apkUrl: String) {
        val fileName = "meloki_update.apk"
        val destinationFile = File(context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS), fileName)
        if (destinationFile.exists()) {
            destinationFile.delete()
        }

        val request = DownloadManager.Request(Uri.parse(apkUrl))
            .setTitle("Meloki Güncellemesi İndiriliyor")
            .setDescription("Yeni özellikler kuruluyor...")
            .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
            .setDestinationUri(Uri.fromFile(destinationFile))

        val downloadManager = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
        val downloadId = downloadManager.enqueue(request)

        // İndirme tamamlandığında yakalayıp paketi kur
        val onComplete = object : BroadcastReceiver() {
            override fun onReceive(ctxt: Context, intent: Intent) {
                val id = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1)
                if (id == downloadId) {
                    context.unregisterReceiver(this)
                    promptInstall(context, destinationFile)
                }
            }
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            context.registerReceiver(onComplete, IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE), Context.RECEIVER_NOT_EXPORTED)
        } else {
            context.registerReceiver(onComplete, IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE))
        }
    }

    /**
     * Android Paket Yükleyicisini (Package Installer) ekrana getirir
     */
    private fun promptInstall(context: Context, file: File) {
        val apkUri = FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            file
        )

        val installIntent = Intent(Intent.ACTION_VIEW).apply {
            setDataAndType(apkUri, "application/vnd.android.package-archive")
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_GRANT_READ_URI_PERMISSION
        }

        context.startActivity(installIntent)
    }
}
