package com.github.kr328.link.qq

import android.content.ContentProvider
import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import androidx.core.os.bundleOf

class OptionProvider : ContentProvider() {
    override fun onCreate(): Boolean {
        return true
    }

    override fun call(method: String, arg: String?, extras: Bundle?): Bundle {
        if ("GET" == method) {
            val sp = context!!.getSharedPreferences("options", Context.MODE_PRIVATE)

            return bundleOf(
                "normal_link" to sp.getBoolean("normal_link", true),
                "bilibili_link" to sp.getBoolean("bilibili_link", true),
                "file_open" to sp.getBoolean("file_open", true)
            )
        }
        throw UnsupportedOperationException("unsupported")
    }

    override fun query(
        uri: Uri,
        projection: Array<String>?,
        selection: String?,
        selectionArgs: Array<String>?,
        sortOrder: String?
    ): Cursor? {
        throw UnsupportedOperationException("unsupported")
    }

    override fun getType(uri: Uri): String? {
        throw UnsupportedOperationException("unsupported")
    }

    override fun insert(uri: Uri, values: ContentValues?): Uri? {
        throw UnsupportedOperationException("unsupported")
    }

    override fun delete(uri: Uri, selection: String?, selectionArgs: Array<String>?): Int {
        throw UnsupportedOperationException("unsupported")
    }

    override fun update(
        uri: Uri,
        values: ContentValues?,
        selection: String?,
        selectionArgs: Array<String>?
    ): Int {
        throw UnsupportedOperationException("unsupported")
    }
}