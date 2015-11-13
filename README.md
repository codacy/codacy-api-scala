[![Circle CI](https://circleci.com/gh/codacy/codacy-api-scala/tree/master.svg?style=shield)](https://circleci.com/gh/codacy/codacy-api-scala/tree/master)
[![Codacy Badge](https://www.codacy.com/project/badge/650fe924dba349458ee29d44f07dae6c)](https://www.codacy.com/app/Codacy/codacy-api-scala)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.codacy/codacy-api-scala_2.11/badge.svg)](https://maven-badges.herokuapp.com/maven-central/com.codacy/codacy-api-scala_2.11)


# Codacy API Scala Client

Scala wrapper for the Codacy API.

### Usage

If you use Maven you can declare the dependency:

```
<dependency>
    <groupId>com.codacy</groupId>
    <artifactId>codacy-api-java</artifactId>
    <version>1.10</version>
</dependency>
```

First, declare the dependency on SBT:

```
"com.codacy" %% "codacy-api-scala" % "1.0.2"
```

Usage example (extracted from our [sbt-codacy-coverage tool](https://github.com/codacy/sbt-codacy-coverage/blob/c10e67fa6fe62992c871e9811c41603ae0a76870/src/main/scala/com/codacy/CodacyCoveragePlugin.scala#L56-L69):

```
val apiUrl = "https://api.codacy.com/"
val projectToken = "randomizedTokenGeneratedByCodacy"

val codacyClient = new CodacyClient(Some(apiUrl), projectToken = Some(projectToken))

val coverageServices = new CoverageServices(codacyClient)

val commitUUID = "gitCommitUUID"
val report = ...

coverageServices.sendReport(commitUUID, Language.Scala, report) match {
          case requestResponse if requestResponse.hasError =>
            logger.error(s"Failed to upload data. Reason: ${requestResponse.message}")
            state.exit(ok = false)
            Left(requestResponse.message)
          case requestResponse =>
            logger.success(s"Coverage data uploaded. ${requestResponse.message}")
            Right(state)
        }
```

### Creators

1. Rodrigo Fernandes

### Contributors

1. Rafael Cortês
2. João Machado
3. João Caxaria
4. Pedro Rijo

### License

codacy-api-scala is available under the MIT license. See the LICENSE file for more info.
