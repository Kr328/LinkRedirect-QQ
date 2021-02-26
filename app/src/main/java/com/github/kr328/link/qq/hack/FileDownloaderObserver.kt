package com.github.kr328.link.qq.hack

import android.app.Activity
import android.app.Application
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import com.github.kr328.link.qq.Constants
import com.github.kr328.link.qq.createViewChooser
import java.io.File

class FileDownloaderObserver(private val context: Context) :
    Application.ActivityLifecycleCallbacks {
    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
        val filePath = activity.intent.getStringExtra(EXTRA_FILE_PATH)
        val fileSize = activity.intent.getLongExtra(EXTRA_FILE_SIZE, -1)

        if (filePath == null || fileSize < 0)
            return

        val file = File(filePath)

        val observer = FileDownloadObserver(file, fileSize) {
            try {
                context.startActivity(
                    context.createViewChooser(file, null).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK),
                    null
                )

                activity.finish()
            } catch (t: Throwable) {
                Log.e(Constants.TAG, "Start activity for $filePath", t)
            }
        }

        observer.start()

        fileObservers[activity] = observer
    }

    override fun onActivityDestroyed(activity: Activity) {
        fileObservers.remove(activity)?.stop()
    }

    override fun onActivityStarted(activity: Activity) {}
    override fun onActivityResumed(activity: Activity) {}
    override fun onActivityPaused(activity: Activity) {}
    override fun onActivityStopped(activity: Activity) {}
    override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {}

    companion object {
        const val EXTRA_FILE_PATH = "_polling_file_path"
        const val EXTRA_FILE_SIZE = "_polling_file_size"

        private val fileObservers: MutableMap<Activity, FileDownloadObserver> = mutableMapOf()
    }
}