package com.github.kr328.link.qq

import android.content.ContentProvider
import android.content.ContentValues
import android.content.UriMatcher
import android.database.Cursor
import android.database.MatrixCursor
import android.net.Uri

class Configuration : ContentProvider() {
    companion object {
        private const val authority = BuildConfig.APPLICATION_ID + ".configuration"

        private const val PATH_TARGETS = "targets"
        private const val PATH_CONFIGS = "configs"
        private const val PATH_TARGETS_ID = 1
        private const val PATH_CONFIGS_ID = 2
        private const val COLUMN_TARGETS_PACKAGE_NAME = "packageName"
        private const val COLUMN_CONFIGS_KEY = "key"
        private const val COLUMN_CONFIGS_VALUE = "value"

        private val matcher = UriMatcher(UriMatcher.NO_MATCH).apply {
            addURI(authority, PATH_TARGETS, PATH_TARGETS_ID)
            addURI(authority, PATH_CONFIGS, PATH_CONFIGS_ID)
        }
    }

    override fun onCreate(): Boolean {
        return true
    }

    override fun query(
        uri: Uri,
        projection: Array<String>?,
        selection: String?,
        selectionArgs: Array<String>?,
        sortOrder: String?
    ): Cursor? {
        return when (matcher.match(uri)) {
            PATH_TARGETS_ID -> {
                MatrixCursor(arrayOf(COLUMN_TARGETS_PACKAGE_NAME)).apply {
                    addRow(listOf("com.tencent.tim"))
                }
            }
            PATH_CONFIGS_ID -> {
                MatrixCursor(arrayOf(COLUMN_CONFIGS_KEY, COLUMN_CONFIGS_VALUE)).apply {
                    addRow(listOf("interceptor", Interceptor::class.java.name))
                }
            }
            else -> null
        }
    }

    override fun getType(uri: Uri): String? {
        return when (matcher.match(uri)) {
            PATH_TARGETS_ID -> {
                "vnd.android.cursor.dir/vnd.$authority.$PATH_TARGETS"
            }
            PATH_CONFIGS_ID -> {
                "vnd.android.cursor.dir/vnd.$authority.$PATH_CONFIGS"
            }
            else -> null
        }
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