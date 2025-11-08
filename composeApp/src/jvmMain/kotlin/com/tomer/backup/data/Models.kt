package com.tomer.backup.data

import java.time.LocalDate
import java.time.LocalDateTime

enum class LogType {
    UPDATED, ADDED, DELETED
}

data class SourcePath(
    val absolutePath: String,
    val fileName: String,
    val isFile: Boolean,
    val dateAdded: LocalDate
)

data class BackupLog(
    val fileName: String,
    val absolutePath: String,
    val logType: LogType,   // Enum LogType: UPDATED, ADDED, DELETED
    val id: Int = 0,       //  Auto-generated ID
    val timeLogged: LocalDateTime = LocalDateTime.now()
)

data class FileMetaData(
    val absolutePath: String,
    val lastSize: Long,
    val lastModified: Long, // Represents file_last_modified_millis
    val shaDigest: String  //  Represents sha_digest (SHA-256 hash)
)
