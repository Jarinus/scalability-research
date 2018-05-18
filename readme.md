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
By default, you can create a Docker container by executing:

`sbt docker:publishLocal`

To run the container:

`docker run -t -p PUBLISHING_PORT:8080 text-analysis-app -workers NUMBER_OF_WORKERS`

Where `PUBLISHING_PORT` is the port to publish the service on and `NUMBER_OF_WORKERS` is the number of workers used for
text analysis.

Note that `-workers NUMBER_OF_WORKERS` may be omitted. In that case, a single worker will be used.

Example:

`docker run -t -p 8080:8080 text-analysis-app -workers 8`
