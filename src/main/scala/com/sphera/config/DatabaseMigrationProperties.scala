package com.sphera.config

import scala.language.implicitConversions

case class DatabaseMigrationProperties(databaseDriver: String,
                                       server: String,
                                       port: String,
                                       user: String,
                                       password: String,
                                       schemaName: String)

object DatabaseMigrationProperties {

  implicit class EnrichedDatabaseMigrationProperties(dbMigrationConfig: DatabaseMigrationProperties) {

    def getConnectionString(): String = {
      dbMigrationConfig.databaseDriver.toLowerCase() match {
        case "mysql" => s"jdbc:mysql://${dbMigrationConfig.server}:${dbMigrationConfig.port}/${dbMigrationConfig.schemaName}"
        case _ => throw new IllegalArgumentException(s"Connection for database driver ${dbMigrationConfig.databaseDriver} aren't supported!")
      }

    }
  }

}