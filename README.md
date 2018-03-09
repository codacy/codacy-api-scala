# Codacy API Scala Client

Scala wrapper for the Codacy API.

[![Codacy Badge](https://api.codacy.com/project/badge/Grade/46749e1408d94e6fbb785201c1eb9bd8)](https://www.codacy.com/app/Codacy/codacy-api-scala?utm_source=github.com&amp;utm_medium=referral&amp;utm_content=codacy/codacy-api-scala&amp;utm_campaign=Badge_Grade)
[![Codacy Badge](https://api.codacy.com/project/badge/Coverage/46749e1408d94e6fbb785201c1eb9bd8)](https://www.codacy.com/app/Codacy/codacy-api-scala?utm_source=github.com&utm_medium=referral&utm_content=codacy/codacy-api-scala&utm_campaign=Badge_Coverage)
[![Circle CI](https://circleci.com/gh/codacy/codacy-api-scala/tree/master.svg?style=shield)](https://circleci.com/gh/codacy/codacy-api-scala/tree/master)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.codacy/codacy-api-scala_2.11/badge.svg)](https://maven-badges.herokuapp.com/maven-central/com.codacy/codacy-api-scala_2.11)

## Usage

If you use Maven you can declare the dependency:

```xml
<dependency>
    <groupId>com.codacy</groupId>
    <artifactId>codacy-api-java</artifactId>
    <version>VERSION</version>
</dependency>
```

First, declare the dependency on SBT:

```xml
"com.codacy" %% "codacy-api-scala" % "VERSION"
```

Usage example (extracted from our [sbt-codacy-coverage tool](https://github.com/codacy/sbt-codacy-coverage/blob/c10e67fa6fe62992c871e9811c41603ae0a76870/src/main/scala/com/codacy/CodacyCoveragePlugin.scala#L56-L69):

```xml
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

## Creators

1. Rodrigo Fernandes

## Contributors

1. Rafael Cortês
2. João Machado
3. João Caxaria
4. Pedro Rijo

## What is Codacy

[Codacy](https://www.codacy.com/) is an Automated Code Review Tool that monitors your technical debt, helps you improve your code quality, teaches best practices to your developers, and helps you save time in Code Reviews.

### Among Codacy’s features

- Identify new Static Analysis issues
- Commit and Pull Request Analysis with GitHub, BitBucket/Stash, GitLab (and also direct git repositories)
- Auto-comments on Commits and Pull Requests
- Integrations with Slack, HipChat, Jira, YouTrack
- Track issues in Code Style, Security, Error Proneness, Performance, Unused Code and other categories

Codacy also helps keep track of Code Coverage, Code Duplication, and Code Complexity.

Codacy supports PHP, Python, Ruby, Java, JavaScript, and Scala, among others.

## Free for Open Source

Codacy is free for Open Source projects.

## License

codacy-api-scala is available under the MIT license. See the LICENSE file for more info.
