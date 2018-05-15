package net.atos.scalability

import net.atos.scalability.Configuration._

import scala.util.matching.Regex

case class Configuration(numberOfWorkers: Int = DEFAULT_NUMBER_OF_WORKERS,
                         endpoint: (String, Int) = (DEFAULT_IP, DEFAULT_PORT))

object Configuration {
  type ConfigurationArgumentParser = (Configuration, Array[String]) => Configuration
  val DEFAULT_NUMBER_OF_WORKERS: Int = 1
  val DEFAULT_IP = "127.0.0.1"
  val DEFAULT_PORT: Int = 9000
  private val endpointRegex: Regex = "(\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3})(?::(\\d{2,5}))?".r
  private val acceptedArguments: Map[String, ConfigurationArgumentParser] = Map(
    "-workers" -> parseNumberOfWorkers,
    "-endpoint" -> parseEndpoint
  )

  def from(args: Array[String]): Configuration = parse(groupArgumentsAndValues(args))

  private def groupArgumentsAndValues(args: Array[String]): Map[String, Array[String]] = {
    def loop(args: Array[String], acc: Map[String, Array[String]]): Map[String, Array[String]] =
      if (args.isEmpty) acc
      else if (!isArgument(args.head))
        throw new IllegalArgumentException(s"Argument '${args.head}' must be prepended with '-'")
      else {
        val (values, remainder) = args.tail span (!isArgument(_))
        loop(remainder, acc + (args.head -> values))
      }

    loop(args, Map())
  }

  private def isArgument(s: String): Boolean = s.charAt(0) == '-'

  private def parse(arguments: Map[String, Array[String]]): Configuration = {
    def loop(arguments: Map[String, Array[String]], configuration: Configuration): Configuration =
      if (arguments.isEmpty) configuration
      else loop(arguments.tail, parseArgument(configuration, arguments.head))

    def parseArgument(configuration: Configuration, argumentWithValue: (String, Array[String])): Configuration =
      argumentWithValue match {
        case (argument, values) => acceptedArguments.get(argument) match {
          case Some(updateFunction) => updateFunction(configuration, values)
          case None => throw new IllegalArgumentException(s"Invalid argument: $argument")
        }
      }

    loop(arguments, Configuration())
  }

  private def parseNumberOfWorkers(configuration: Configuration, values: Array[String]): Configuration = {
    val numberOfWorkers = values.head.toInt

    require(numberOfWorkers > 0)

    configuration.copy(numberOfWorkers = numberOfWorkers)
  }

  private def parseEndpoint(configuration: Configuration, values: Array[String]): Configuration = {
    val endpoint = values.head

    endpoint match {
      case endpointRegex(ip, port) =>
        configuration.copy(endpoint = (ip, if (port != null) port.toInt else DEFAULT_PORT))
      case _ =>
        throw new IllegalArgumentException(
          s"Invalid endpoint '$endpoint', expected (regex) format: ${endpointRegex.toString}")
    }
  }
}