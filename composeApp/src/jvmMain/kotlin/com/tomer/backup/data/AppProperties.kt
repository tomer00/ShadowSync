package com.tomer.backup.data

import kotlinx.coroutines.runBlocking
import sambabackup.composeapp.generated.resources.Res
import java.io.BufferedReader
import java.io.ByteArrayInputStream
import java.io.InputStreamReader
import java.util.*

object AppProperties {
    val props = Properties()

    init {
        runBlocking {
            try {
                props.load(BufferedReader(InputStreamReader(ByteArrayInputStream(Res.readBytes("drawable/app.properties")))))
                props.setProperty("app.dir", System.getenv("SAMBA_DEVICE") ?: "common")
            } catch (e: Exception) {
                e.printStackTrace(System.err)
            }
        }
    }

    fun getProperty(name: String): String? = props.getProperty(name)
}