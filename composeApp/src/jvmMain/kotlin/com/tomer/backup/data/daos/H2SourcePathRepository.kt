package com.tomer.backup.data.daos

import com.tomer.backup.data.SourcePath
import com.tomer.backup.data.SourcePathTable
import org.jetbrains.exposed.sql.batchInsert
import org.jetbrains.exposed.sql.deleteAll
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import java.time.Instant
import java.time.ZoneId

object H2SourcePathRepository : SourcePathRepository {

    override fun saveSourcePaths(sourcePaths: List<SourcePath>) {
        transaction {
            SourcePathTable.batchInsert(sourcePaths) { i ->
                this[SourcePathTable.name] = i.fileName
                this[SourcePathTable.absolutePath] = i.absolutePath
                this[SourcePathTable.isFile] = i.isFile
            }
        }
    }

    override fun deleteAllSourcePaths() {
        transaction {
            SourcePathTable.deleteAll()
        }
    }

    override fun getAllSourcePaths() =
        transaction {
            SourcePathTable.selectAll().map { row ->
                SourcePath(
                    absolutePath = row[SourcePathTable.absolutePath],
                    fileName = row[SourcePathTable.name],
                    isFile = row[SourcePathTable.isFile],
                    dateAdded = Instant.ofEpochSecond(row[SourcePathTable.dateAdded])
                        .atZone(ZoneId.systemDefault())
                        .toLocalDate(),
                )
            }
        }
}