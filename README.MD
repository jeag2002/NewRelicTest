# NEW RELIC TEST

## First Exercise

Proposal of solution for the proposal exercice of new relic, for later discussion

### Statement:

This is your first day working at NewRelic and a task named: “Refactor metric-consumer” has been assigned to you.You will find the current metric-cosumer code in the metric-consumer.zip file. It contains:

the input.txt file with some metric values. This file is used for testing. Metrics are represented in the following format: <timestamp> <metric-name> <value>

1650973075 cpu 47</br>
1650973087 cpu 47</br>
1650973099 mem 737917</br>
1650973111 cpu 50</br>

The Main.java class creates the metric-consumer and tests it with the previous input.txt file. As a result, an output.txt file is created

The MetricConsumer.java class is the actual code

Other gradle files for compiling and running the application

We ask you to do 3 simple things:

- Read the code and try to understand it.

- Refactor the application to make it more readable and easier to modify.

- Modify the metric-consumer in order to be able to process multiple metrics in parallel.

Complete statement can be found [here](nr-be-technical-interview/doc/statement.pdf)

### Environment:

- Microsoft Windows 10 Professional Edition x64
- Java 11 (jdk-11.0.3)


### Execution:

- Go to baseline and run ".\gradlew.bat run"

you can see this:

![alt text](https://github.com/jeag2002/NewRelicTest/blob/master/gradlew-execution.jpg?raw=true)

### Solution:

Main class which executes the three implementations can be found [here](nr-be-technical-interview/src/main/java/com/newrelic/metrics/Main.java)

Original Metric implementation can be found [here](nr-be-technical-interview/src/main/java/com/newrelic/metrics/MetricConsumer.java)

Refactor Metric implementation can be found [here](nr-be-technical-interview/src/main/java/com/newrelic/metrics/refactor/MetricConsumerRefactor.java)

Enhanced Metric implementation can be found [here](nr-be-technical-interview/src/main/java/com/newrelic/metrics/enhanced/MetricConsumerEnhanced.java)

This last proposed solution uses parallelStream and concurrent containers

## Second Exercise

Architecture discussion of this schema

![alt text](https://github.com/jeag2002/NewRelicTest/blob/master/second-exercice/second-exercice.jpg?raw=true)
