package com.tomer.backup.data.daos

import com.tomer.backup.data.BackupLog
import com.tomer.backup.data.FileMetaData
import com.tomer.backup.data.SourcePath
import java.util.Optional

interface SourcePathRepository {
    fun saveSourcePaths(sourcePaths: List<SourcePath>)
    fun deleteAllSourcePaths()
    fun getAllSourcePaths(): List<SourcePath>
}

interface BackupLogRepository {
    fun saveBackupLogs(backupLogs: List<BackupLog>)
    fun getBackupLogById(id: Int): Optional<BackupLog>
    fun getAllBackupLogs(): List<BackupLog>
}

interface FileMetaDataRepository {
    fun saveFileMetaData(fileMetaData: FileMetaData)
    fun getFileMetaDataByPath(absolutePath: String): Optional<FileMetaData>
    fun updateFileMetaData(fileMetaData: FileMetaData)
    fun deleteFileMetaData(absolutePath: String)
}
