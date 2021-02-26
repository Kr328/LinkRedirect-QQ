package com.github.kr328.link.qq.hack

import android.os.Handler
import android.os.HandlerThread
import android.util.Log
import com.github.kr328.link.qq.BuildConfig
import com.github.kr328.link.qq.Constants
import java.io.File

class FileDownloadObserver(
    private val file: File,
    private val fileSize: Long,
    private val onMatched: () -> Unit,
) {
    private val handler = Handler(looper)

    fun start() {
        handler.removeMessages(0)

        poll()
    }

    fun stop() {
        handler.post {
            handler.removeMessages(0)
        }
    }

    private fun poll() {
        val length = file.length()

        if (length == fileSize)
            return onMatched()

        if (BuildConfig.DEBUG)
            Log.d(Constants.TAG, "Polling $file length = $length expect = $fileSize")

        handler.postDelayed({ poll() }, 1000)
    }

    companion object : HandlerThread("file_observer") {
        init {
            start()
        }
    }
}