package com.sphera

import com.sphera.config.{ApplicationParameters, DatabaseMigrationProperties, DatabasesMigrationConfig, Update}
import liquibase.{Contexts, LabelExpression, Liquibase}
import liquibase.database.{Database, DatabaseFactory}
import liquibase.database.jvm.JdbcConnection
import liquibase.resource.ClassLoaderResourceAccessor
import wvlet.airframe.config.Config
import wvlet.airframe.launcher.{command, option}
import wvlet.log.{LogLevel, LogSupport, Logger}

import java.sql.{Connection, DriverManager}
import scala.util.{Failure, Success, Using}


object DatabaseMigration{

  def execute(applicationParameters: ApplicationParameters) = {

    applicationParameters.liquibaseOperation match {
      case Update() => update()
      case _ => ""
    }
  }

  def update() = {

    val databasesMigrationConfig = Config(env = "default", configPaths = Seq("./src/main/resources/"))
      .registerFromYaml[DatabasesMigrationConfig]("databases-migration-config.yaml")
      .of[DatabasesMigrationConfig]

    databasesMigrationConfig.databaseMigrations
      .flatMap(createSchema)
      .map((dbMigrationProperties: DatabaseMigrationProperties) =>
        initMigrationContext(dbMigrationProperties, databasesMigrationConfig.changeLogPath))
      .foreach(updateDatabase)
  }

  private def updateDatabase(liquibase: Liquibase): Unit = {
    liquibase.update(
      new Contexts(liquibase.getDatabase.getLiquibaseSchemaName),
      new LabelExpression(liquibase.getDatabase.getLiquibaseSchemaName))
  }

  private def createSchema(dbMigrationProperties: DatabaseMigrationProperties): Option[DatabaseMigrationProperties] = {

    val url = s"jdbc:mysql://${dbMigrationProperties.server}:${dbMigrationProperties.port}"

    val sql = s"CREATE SCHEMA IF NOT EXISTS ${dbMigrationProperties.schemaName}"

    Using(DriverManager.getConnection(url, dbMigrationProperties.user, dbMigrationProperties.password)) {
      connection =>
        val s = connection.prepareStatement(sql)
        s.execute()
    } match {
      case Success(_) =>
        Some(dbMigrationProperties)
      case Failure(ex) =>
        print(ex.getMessage)
        None
    }
  }

  private def initMigrationContext(dbMigrationProperties: DatabaseMigrationProperties, changeLogPath: String): Liquibase = {

    val connection: Connection = DriverManager
      .getConnection(dbMigrationProperties.getConnectionString(), dbMigrationProperties.user, dbMigrationProperties.password)

    val database: Database = DatabaseFactory.getInstance()
      .findCorrectDatabaseImplementation(new JdbcConnection(connection))

    val liquibase: Liquibase = new Liquibase(changeLogPath, new ClassLoaderResourceAccessor(), database)

    liquibase
  }
}
