package com.gennan.summer.util

import android.content.ContentResolver
import android.content.Context
import android.net.Uri
import android.os.Environment
import android.os.Environment.DIRECTORY_PICTURES
import android.provider.MediaStore
import android.text.TextUtils
import java.io.File


/**
 *Created by Gennan on 2019/8/19.
 */
object UriToRealPathUtil {
    fun getRealFilePath(context: Context, uri: Uri?): String? {
        if (null == uri) return null
        val scheme = uri.scheme
        var realPath: String? = null
        if (scheme == null)
            realPath = uri.path
        else if (ContentResolver.SCHEME_FILE == scheme) {
            realPath = uri.path
        } else if (ContentResolver.SCHEME_CONTENT == scheme) {
            val cursor = context.contentResolver.query(
                uri,
                arrayOf(MediaStore.Images.ImageColumns.DATA), null, null, null
            )
            if (null != cursor) {
                if (cursor.moveToFirst()) {
                    val index = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA)
                    if (index > -1) {
                        realPath = cursor.getString(index)
                    }
                }
                cursor.close()
            }
        }
        if (TextUtils.isEmpty(realPath)) {
            if (uri != null) {
                val uriString = uri.toString()
                val index = uriString.lastIndexOf("/")
                val imageName = uriString.substring(index)
                var storageDir: File

                storageDir = Environment.getExternalStoragePublicDirectory(
                    DIRECTORY_PICTURES
                )
                val file = File(storageDir, imageName)
                if (file.exists()) {
                    realPath = file.getAbsolutePath()
                } else {
                    storageDir = context.getExternalFilesDir(DIRECTORY_PICTURES)
                    val file1 = File(storageDir, imageName)
                    realPath = file1.absolutePath
                }
            }
        }
        return realPath
    }
}