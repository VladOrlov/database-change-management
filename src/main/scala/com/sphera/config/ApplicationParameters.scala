package com.sphera.config

sealed trait LiquibaseOperation
case class Update() extends LiquibaseOperation
case class Downgrade() extends LiquibaseOperation

case class ApplicationParameters(liquibaseOperation: LiquibaseOperation = Update(),
                                 version: Option[String] = None,
                                 env: String = "local")
