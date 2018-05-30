# Scalability Research

[![Build Status](https://travis-ci.com/Jarinus/scalability-research.svg?token=MKhDfVCLHhGXWqDxynsL&branch=develop)](https://travis-ci.com/Jarinus/scalability-research)

The source code for the text analysis application. The application's purpose is to gain insight in developing scalable
applications with the help of the Akka library. Additionally, this application is written in Scala to gain insight in
the potential benefits of Scala in comparison to Java with respect to scalability.

## Importing the project in IntelliJ IDEA
Note: requires the Scala/SBT plugins
* File -> New -> Project from Existing Sources...
* Navigate to the project directory and select `build.sbt`
* Click `OK` and the project should be correctly imported by IntelliJ.

## Configuration
### Building
By default, you can create a Docker container by executing:

`sbt docker:publishLocal`

### Arguments
* `--workers, -w` The number of workers (threads) to use on this node.
* `--seed-nodes, -s` The Akka TCP URL('s) of the seed node(s).

### Running
To run the container (anything in parentheses is optional):

`docker run -(i)t -p PORT-PORT+1:8080-8081 text-analysis-app (-w NUMBER_OF_WORKERS) -s AKKA_TCP_SEED_NODE_ADDRESS(ES)`

Where:
* `PORT` is the port to publish the service on;
* `NUMBER_OF_WORKERS` is the number of workers used for text analysis.
* `AKKA_TCP_SEED_NODE_ADDRESS(ES)` is/are the address(es) of the Akka TCP seed nodes.

Note:
* If `-w NUMBER_OF_WORKERS` is omitted, a single worker will be used.
* `-t` keeps the container running in the background. If you wish for the container to exit if you exit the
  shell, use `-it` (`-i` stands for interactive). This will allow you to press any key to exit the program, killing the
  container in the process.
* If the node to be started is to be the first cluster node, it should pass its own address as the seed node argument
  value. The reasoning is that the first node of a cluster should always be a seed node, as otherwise other nodes are
  unable to join the cluster, which is the point of clustering.

### Example

`docker run -it -p 8080-8081:8080-8081 text-analysis-app -w 8 -s akka.tcp://TextAnalysisApp@localhost:8081`
