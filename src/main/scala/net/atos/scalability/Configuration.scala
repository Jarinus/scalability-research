package net.atos.scalability

case class Configuration(numberOfThreads: Int = 1)

object Configuration {
  val acceptedArguments: Map[String, (Configuration, Array[String]) => Configuration] = Map(
    "-threads" -> ((configuration, values) => {
      val numberOfThreads = values.head.toInt

      require(numberOfThreads > 0)
      configuration.copy(numberOfThreads)
    })
  )

  def initialize(args: Array[String]): Configuration = parse(groupArgumentsAndValues(args))

  private def groupArgumentsAndValues(args: Array[String]): Map[String, Array[String]] = {
    def loop(args: Array[String], acc: Map[String, Array[String]]): Map[String, Array[String]] =
      if (args.isEmpty) acc
      else if (args.head.charAt(0) != '-')
        throw new IllegalArgumentException(s"Argument ${args.head} must be prepended with '-'")
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
}
