# Scalability Research

[![Build Status](https://travis-ci.com/Jarinus/scalability-research.svg?token=MKhDfVCLHhGXWqDxynsL&branch=develop)](https://travis-ci.com/Jarinus/scalability-research)

The source code for the text analysis application. The application's purpose is to gain insight in developing scalable
applications with the help of the Akka library. Additionally, this application is written in Scala to gain insight in
the potential benefits of Scala in comparison to Java with respect to scalability.

This project was developed for an assignment issued by Atos Nederland B.V.

## Importing the project in IntelliJ IDEA
Note: requires the Scala/SBT plugins
* File -> New -> Project from Existing Sources...
* Navigate to the project directory and select `build.sbt`
* Click `OK` and the project should be correctly imported by IntelliJ.

## Configuration
### Building
Create a local Docker container by executing:

`$ sbt docker:publishLocal`

### Arguments
* `--workers, -w` The number of workers (threads) to use on this node. Default: `1`.
* `--port` The port to publish the HTTP API on.
* `--akka-host` The host to publish Akka on. Default: `127.0.0.1`
* `--akka-port` The port to publish Akka on. Default: `8081`.
* `--docker-ip` If deploying remotely, the internal Docker container IP.
* `--seed-nodes, -s` The Akka TCP URL('s) of the seed node(s).

### Running
Example run command (anything in parentheses is optional):

```
$ docker run \
> -(i)t \
> -p PORT-PORT+1:8080-8081 \
> jarinus/text-analysis-app \
(> -w NUMBER_OF_WORKERS \)
> -s AKKA_TCP_SEED_NODE_ADDRESS(ES)
```

Where:
* `PORT` is the port to publish the service on;
* `NUMBER_OF_WORKERS` is the number of workers used for text analysis;
* `AKKA_TCP_SEED_NODE_ADDRESS(ES)` is/are the address(es) of the Akka TCP seed nodes.

Note:
* `-t` keeps the container running in the background. If you wish for the container to exit if you exit the
  shell, use `-it` (`-i` stands for interactive). This will allow you to press any key to exit the program, killing the
  container in the process. Using `--rm` with `-i` kills the container and removes the container upon exit.
* If the node to be started is to be the first cluster node, it should pass its own address as the seed node argument
  value. The reasoning is that the first node of a cluster should always be a seed node, as otherwise other nodes are
  unable to join the cluster, which is the point of clustering.
* All arguments defined in the Arguments section can be used after the `jarinus/text-analysis-app` part.
* Akka TCP seed node addresses must use IP addresses, not DNS hosts. DNS hosts can be used, but require additional setup
  that is not provided in this application by default.

### Example
Local:

```
$ docker run \
> --rm \
> -it \
> -p 8080-8081:8080-8081 \
> jarinus/text-analysis-app \
> -w 8 \
> -s akka.tcp://TextAnalysisApp@127.0.0.1:8081
```

Remote (replace `REMOTE_IP` with host's external IP address):

```
$ docker run \
> --rm \
> -it \
> -p 8080-8081:8080-8081 \
> jarinus/text-analysis-app \
> --akka-host REMOTE_IP \
> -w 8 \
> -s akka.tcp://TextAnalysisApp@REMOTE_IP:8081
```
