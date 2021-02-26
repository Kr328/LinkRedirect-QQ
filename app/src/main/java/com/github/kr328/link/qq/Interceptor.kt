package com.github.kr328.link.qq

import android.app.Application
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.annotation.Keep
import androidx.core.os.bundleOf
import com.github.kr328.link.qq.hack.FileDownloaderObserver
import com.github.kr328.link.qq.hack.toFileInfo
import com.tencent.mobileqq.filemanager.data.ForwardFileInfo
import java.io.File
import java.util.*

class Interceptor @Keep constructor(context: Context?) : ContextWrapper(context) {
    @Keep
    fun intercept(intent: Intent): Intent {
        if (intent.component == null) return intent

        Log.i(Constants.TAG, "Intercept " + applicationContext.packageName + " intent: " + intent)

        intent.setExtrasClassLoader(Interceptor::class.java.classLoader)

        if (intent.hasCategory(CATEGORY_IGNORE)) return intent

        if (BuildConfig.DEBUG)
            dumpExtras(intent)

        return interceptNormalLink(intent) ?: interceptFileViewer(intent) ?: intent
    }

    private fun interceptNormalLink(intent: Intent): Intent? {
        val uri = Uri.parse(intent.getStringExtra("url") ?: return null)

        if (uri.scheme?.toLowerCase(Locale.getDefault()) !in VALID_URL_SCHEME)
            return null

        return if (options.getBoolean("normal_link", true)) {
            createViewChooser(uri, null, intent)
        } else {
            null
        }
    }

    private fun interceptFileViewer(intent: Intent): Intent? {
        if (intent.component?.className != FILE_VIEWER_CLASS_NAME)
            return null

        val raw = intent.getParcelableExtra("fileinfo") as ForwardFileInfo?
        val fileInfo = (raw ?: return null).toFileInfo()

        Log.d(
            Constants.TAG,
            "Opening ${fileInfo.localPath ?: fileInfo.fileName} size = ${fileInfo.fileSize}"
        )

        if (!options.getBoolean("file_open", true)) {
            return null
        }

        if (fileInfo.localPath == null) {
            val base = applicationContext.getExternalFilesDir(null)?.parentFile ?: return null
            val path = base.resolve("Tencent/$nameOfReceiveFile").resolve(fileInfo.fileName)

            intent.putExtra(FileDownloaderObserver.EXTRA_FILE_PATH, path.absolutePath)
            intent.putExtra(FileDownloaderObserver.EXTRA_FILE_SIZE, fileInfo.fileSize)

            return null
        }

        return createViewChooser(File(fileInfo.localPath), intent)
    }

    private val options: Bundle
        get() {
            val uri = Uri.Builder()
                .scheme("content")
                .authority(BuildConfig.APPLICATION_ID + ".options")
                .build()

            return try {
                val result = contentResolver.call(uri, "GET", null, null)

                cacheOptions = result

                result
            } catch (e: Exception) {
                Log.w(Constants.TAG, "Get options", e)

                null
            } ?: cacheOptions ?: bundleOf()
        }

    private fun dumpExtras(intent: Intent) {
        Log.d(
            Constants.TAG, Dumper.dump(
                packageName, intent
            )
        )
    }

    private val nameOfReceiveFile: String
        get() {
            return when (applicationContext.packageName) {
                "com.tencent.tim" -> "TIMFile_recv"
                "com.tencent.mobileqq" -> "QQFile_recv"
                else -> {
                    val label =
                        applicationContext.applicationInfo.loadLabel(applicationContext.packageManager)

                    return "${label}File_recv"
                }
            }
        }

    companion object {
        const val CATEGORY_IGNORE = "com.github.kr328.link.qq.IGNORE"

        private val VALID_URL_SCHEME = setOf("http", "https", "content")
        private val FILE_VIEWER_CLASS_NAME =
            "com.tencent.mobileqq.filemanager.fileviewer.TroopFileDetailBrowserActivity"

        private var cacheOptions: Bundle? = null
    }

    init {
        (applicationContext as Application)
            .registerActivityLifecycleCallbacks(FileDownloaderObserver(this))

        Log.i(Constants.TAG, "application " + applicationContext.packageName + " injected")
    }
}