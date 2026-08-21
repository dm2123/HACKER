package com.example.hacker.phonecontrol.files

import android.content.Context
import android.net.Uri
import java.io.File

/** File manager */
class FileManager(private val context: Context) {
    /** Get external files directory */
    fun getExternalFilesDir(dir: String?): File {
        return context.getExternalFilesDir(dir)
    }

    /** Get internal files directory */
    fun getFilesDir(): File {
        return context.filesDir
    }

    /** Read file content as string */
    fun readFile(file: File): String? {
        return if (file.exists()) {
            java.io.File(file).readText()
        } else null
    }

    /** Write content to file */
    fun writeFile(file: File, content: String): Boolean {
        try {
            java.io.File(file).writeText(content)
            return true
        } catch (e: Exception) {
            return false
        }
    }

    /** Delete file */
    fun deleteFile(file: File): Boolean {
        return file.delete()
    }
}