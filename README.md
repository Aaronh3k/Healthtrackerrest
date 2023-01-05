# Health Tracker Rest API

> ### Kotlin + Javalin + Exposed codebase containing real world examples (CRUD, Unit Tests, Integration Tests etc)

### [Healthtrackerrest](https://github.com/Aaronh3k/Healthtrackerrest)

This codebase was created to demonstrate a fully fledged fullstack application built with **Kotlin + Javalin + Koin + Exposed** including CRUD operations, authentication, routing, pagination, and more.

We've gone to great lengths to adhere to the **Kotlin + Javalin** community styleguides & best practices.

# How it works

The application was made mainly to demo the functionality of Javalin framework.

The application was built with:

  - [Kotlin](https://github.com/JetBrains/kotlin) as programming language
  - [Javalin](https://github.com/tipsy/javalin) as web framework
  - [Jackson](https://github.com/FasterXML/jackson-module-kotlin) as data bind serialization/deserialization
  - [H2](https://github.com/h2database/h2database) as database for integration tests
  - [Exposed](https://github.com/JetBrains/Exposed) as Sql framework to persistence layer
  - [Railway](https://railway.app/) to deploy, monitor and automatically scale the app
  - [PostgreSQL](https://www.postgresql.org/) as database in ElephantSQL

Tests:

  - [junit](https://github.com/junit-team/junit4)
  - [Unirest](https://github.com/Kong/unirest-java) to call endpoints in tests


# Getting started

You need just JVM installed.

The server is configured to start on [7000](http://localhost:7000/api) with `api` context.

# Domain - Railway

https://healthtrackerrest-production-cb60.up.railway.app/
