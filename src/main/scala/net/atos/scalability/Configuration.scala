package net.atos.scalability

import scala.util.matching.Regex

case class Configuration(numberOfThreads: Int = 1,
                         endpointIp: String = "127.0.0.1")

object Configuration {
  val ipAddressRegex: Regex = "\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}".r
  val acceptedArguments: Map[String, (Configuration, Array[String]) => Configuration] = Map(
    "-threads" -> ((conf, values) => conf.copy(numberOfThreads = parseNumberOfThreads(values))),

    "-endpoint" -> ((conf, values) => conf.copy(endpointIp = parseEndpointIp(values)))
  )

  def initialize(args: Array[String]): Configuration = parse(groupArgumentsAndValues(args))

  private def groupArgumentsAndValues(args: Array[String]): Map[String, Array[String]] = {
    def loop(args: Array[String], acc: Map[String, Array[String]]): Map[String, Array[String]] =
      if (args.isEmpty) acc
      else if (args.head.charAt(0) != '-')
        throw new IllegalArgumentException(s"Argument '${args.head}' must be prepended with '-'")
      else {
        val (values, remainder) = args.tail span (_.charAt(0) != '-')
        loop(remainder, acc + (args.head -> values))
      }

    loop(args, Map())
  }

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

  private def parseNumberOfThreads(values: Array[String]): Int = {
    val numberOfThreads = values.head.toInt

    require(numberOfThreads > 0)

    numberOfThreads
  }

  private def parseEndpointIp(values: Array[String]): String = {
    val endpointIp = values.head

    require(ipAddressRegex.findFirstIn(endpointIp).nonEmpty)

    endpointIp
  }
}
