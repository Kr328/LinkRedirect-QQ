package com.github.kr328.link.qq.hack

import android.os.Parcel
import com.tencent.mobileqq.filemanager.data.ForwardFileInfo

data class FileInfo(val fileName: String, val fileSize: Long, val localPath: String?)

fun ForwardFileInfo.toFileInfo(): FileInfo {
    val parcel = Parcel.obtain()

    try {
        writeToParcel(parcel, 0)

        parcel.setDataPosition(0)

        parcel.readInt() // type
        parcel.readLong() // session id

        val fileSize = parcel.readLong() // file size
        val localPath = parcel.readString() // local path

        parcel.readString() // uuid
        parcel.readString() // file id
        parcel.readInt() // unknown

        val fileName = parcel.readString()!!

        return FileInfo(fileName, fileSize, localPath)
    } finally {
        parcel.recycle()
    }
}