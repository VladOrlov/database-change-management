package com.sphera.config

case class DatabasesMigrationConfig(changeLogPath: String, databaseMigrations: Seq[DatabaseMigrationProperties])
