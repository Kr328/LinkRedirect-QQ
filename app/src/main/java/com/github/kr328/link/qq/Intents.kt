package com.github.kr328.link.qq

import android.content.Context
import android.content.Intent
import android.content.pm.LabeledIntent
import android.net.Uri
import android.os.Parcelable
import android.support.v4.content.FileProvider
import android.util.Log
import java.io.File

fun Context.createViewChooser(file: File, original: Intent?): Intent {
    val authority = applicationContext.packageName + ".fileprovider"
    val uri = FileProvider.getUriForFile(applicationContext, authority, file)
    val type = contentResolver.getType(uri)

    return createViewChooser(uri, type, original)
}

fun Context.createViewChooser(uri: Uri, mimeType: String?, original: Intent?): Intent {
    val view = Intent(Intent.ACTION_VIEW)

    if (mimeType == null) {
        view.addCategory(Intent.CATEGORY_BROWSABLE)
    }
    else {
        view.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
    }

    view.setDataAndType(uri, mimeType)

    val chooser = Intent.createChooser(view, null)

    if (original != null) {
        original.addCategory(Interceptor.CATEGORY_IGNORE)

        chooser.putExtra(
            Intent.EXTRA_INITIAL_INTENTS, arrayOf<Parcelable>(
                LabeledIntent(
                    original,
                    packageName,
                    getString(R.string.internal_browser),
                    0
                )
            )
        )
    }

    Log.i(Constants.TAG, "Redirect: $uri")

    return chooser
}
