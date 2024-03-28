# Maven runner

> ,,Run jar from maven artifact''

_QAddict_ organization brings various tools mainly for use in Test Automation.

* Maven runner plugin allows running main class or tests directly from a jar of a maven artifact.
* No need to have local maven project (pom.xml).
* Manage dependencies using powerful maven infrastructure.

## Running main class
Example of running of a main class from a maven artifact:
```shell
mvn org.qaddict:run:main -Din=org.group:artifactId:version -DmainClass=org.group.Main
```

When the jar contains mainfest with main class, then you can use it even simpler:
```shell
mvn org.qaddict:run:main -Din=org.group:artifactId:version
```

## Running TestNG tests using bundled testng.xml
```shell
mvn org.qaddict:run:testng -Din=org.group:artifactId:version
```
