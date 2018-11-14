# Advanced Web Technologies
# Lab 3 - HTTP Server

## How to launch the project :

From a command line interpreter in the project's root directory

mvn clean package

java -Dlog4j.configuration=file:log4j.properties -cp target/Lab3-0.0.1-SNAPSHOT-jar-with-dependencies.jar HTTPD.Main server.properties