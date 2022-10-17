package com.sphera

import com.sphera.config.{ApplicationParameters, Downgrade, Update}


object App {

  def main(args: Array[String]): Unit = {

    val parameters = getApplicationParameters(args)
    DatabaseMigration.execute(parameters)

  }

  private def getApplicationParameters(args: Array[String]): ApplicationParameters = {
    //    CommandLineParser
    val parser = new scopt.OptionParser[ApplicationParameters]("scopt") {
      head("scopt", "4.x")
      opt[String]("env") required() action { (x, c) =>
        c.copy(env = x)
      }
      opt[String]("operation") required() action { (x, c) =>
        c.copy(liquibaseOperation = x.toLowerCase() match {
          case "update" => Update()
          case "downgrade" => Downgrade()
        })
      }
      opt[String]("version") optional() action { (x, c) =>
        c.copy(version = Option(x))
      }

    }

    parser.parse(args, ApplicationParameters()) match {
      case Some(config: ApplicationParameters) => config
      case None => throw new IllegalArgumentException("Wrong parameters!")
    }
  }

  private def getApplicationParametersFunc(args: Array[String]): ApplicationParameters = {
    None
  }


}
