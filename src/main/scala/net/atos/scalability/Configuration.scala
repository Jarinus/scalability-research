package net.atos.scalability

case class Configuration(numberOfWorkers: Int,
                         endpoint: (String, Int),
                         seedNodes: List[String])

object Configuration {
  type ConfigurationArgumentParser = (Configuration, Array[String]) => Configuration
  val DEFAULT_NUMBER_OF_WORKERS: Int = 1
  val DEFAULT_IP = "0.0.0.0"
  val DEFAULT_PORT: Int = System.getenv("port").toInt
  private val seedNodeRegex =
    "akka\\.tcp:\\/\\/[a-zA-Z0-9]+@(?:\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}|[a-z-]+):\\d{2,6}".r
  private val acceptedArguments: Map[String, ConfigurationArgumentParser] = Map(
    "--workers" -> parseNumberOfWorkers,
    "--seed-nodes" -> parseSeedNodes,
    "-w" -> parseNumberOfWorkers,
    "-s" -> parseSeedNodes,
  )

  def from(args: Array[String]): Configuration = parse(groupArgumentsAndValues(args))

  private def groupArgumentsAndValues(args: Array[String]): Map[String, Array[String]] = {
    def loop(args: Array[String], acc: Map[String, Array[String]]): Map[String, Array[String]] = args.headOption match {
      case Some(head) =>
        val (values, remainder) = args.drop(1) span (!isArgument(_))
        loop(remainder, acc + (head -> values))
      case None => acc
    }

    loop(args, Map())
  }

  private def isArgument(s: String): Boolean = s.charAt(0) == '-'

  private def parse(arguments: Map[String, Array[String]]): Configuration = {
    def loop(arguments: Map[String, Array[String]], configuration: Configuration): Configuration = arguments.headOption match {
      case Some(head) =>
        val newConfiguration = parseArgument(configuration, head) getOrElse configuration
        loop(arguments.drop(1), newConfiguration)
      case None => configuration
    }

    def parseArgument(configuration: Configuration, argumentWithValue: (String, Array[String])): Option[Configuration] =
      argumentWithValue match {
        case (argument, values) => acceptedArguments.get(argument) map { parseFunction =>
          parseFunction(configuration, values)
        }
      }

    loop(arguments, Configuration.default)
  }

  def default = Configuration(DEFAULT_NUMBER_OF_WORKERS, (DEFAULT_IP, DEFAULT_PORT), Nil)

  private def parseNumberOfWorkers(configuration: Configuration, values: Array[String]): Configuration = {
    val numberOfWorkers = values.head.toInt

    require(numberOfWorkers > 0)

    configuration.copy(numberOfWorkers = numberOfWorkers)
  }

  private def parseSeedNodes(configuration: Configuration, values: Array[String]): Configuration = {
    require(values forall {
      seedNodeRegex.findFirstMatchIn(_).nonEmpty
    })

    configuration.copy(seedNodes = values.toList)
  }

}
