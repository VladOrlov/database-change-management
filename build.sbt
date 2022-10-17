ThisBuild / version := "0.1.0-SNAPSHOT"
ThisBuild / scalaVersion := "2.11.12"

import com.permutive.sbtliquibase.SbtLiquibase


lazy val root = (project in file("."))
  .enablePlugins(SbtLiquibase)
  .enablePlugins(PackPlugin)
  .settings(
    name := "database-change-management",
    assembly / assemblyJarName := "app.jar",
    libraryDependencies ++= Seq(
      "org.liquibase" % "liquibase-core" % "4.16.1",
      "mysql" % "mysql-connector-java" % "8.0.30",
      "com.github.scopt" % "scopt_2.11" % "4.0.1",
      "org.wvlet.airframe" %% "airframe-config" % "20.10.3",
      "org.wvlet.airframe" %% "airframe-launcher" % "20.10.3"
    ),
    packMain := Map("etl" -> "com.sphera.DatabaseMigration"),

    liquibaseUsername := "root",
    liquibasePassword := "admin",
    liquibaseDriver := "com.mysql.jdbc.Driver",
    liquibaseUrl := "jdbc:mysql://localhost:4406/test_db?createDatabaseIfNotExist=true",
    liquibaseDefaultSchemaName := Option("test_database"),
    liquibaseChangelog := new File("/home/vorlov/IdeaProjects/database-change-managment-example/src/main/resources/databases_changelogs/ikea/db.changelog-master.xml")
  )

ThisBuild / assemblyMergeStrategy := {
  case PathList("javax", "servlet", xs@_*) => MergeStrategy.first
  case PathList(ps@_*) if ps.last endsWith ".html" => MergeStrategy.first
  case "application.conf" => MergeStrategy.concat
  case "unwanted.txt" => MergeStrategy.discard
  case x =>
    val oldStrategy = (ThisBuild / assemblyMergeStrategy).value
    oldStrategy(x)
}