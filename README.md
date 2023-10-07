# Fk-Framework: Best-Practices for a Play-Framework Multi-Module-Project with Slick

This is a minimal CRUD service exposing a couple of endpoints over REST.

Under the hood, the code is using:
- Play-Framework
  - Swagger-UI Support
  - Db-Migrations
- Slick-Framework
  - Object Oriented Querying on the database
  - Db-Schema-To-Code-Generator
- Mariadb-Testcontainer for Code-Generator
- Sbt Build
  - Multi-Module project for shared-libraries approach
- Customizable Helpers
  - own DAO-Abstraction that you can extend from and fine tune.
  - own RemotePagination Case-Class to use for remote pagination

In the folder `./docs` you can find specific documentations about the different concepts this small seed-project implements, so you can benefit from them.

# Slick with Flyway, Scala and Play-Framework Example

Dieses Beispielprojekt demonstriert die Verwendung von slick in einer Scala / Play-Framework Umgebung. 

für Details, siehe auch: https://scalac.io/scala-slick-experience/

TODO


Wichtig:
- Java8
- In Intellij muss Play Compiler aktiviert sein!
- 
- 
  https://github.com/play-swagger/play-swagger


"sbt swagger" ausführen.
danach im browser:
- http://localhost:9000/assets/swagger-ui/index.html

Multi-Module Project Routes/Play project split:
- https://www.playframework.com/documentation/2.8.3/sbtSubProjects#Working-with-sub-projects

Compilation-Speed:
- https://www.playframework.com/documentation/2.8.3/CompilationSpeed



# Create it

```
sbt docker:clean
sbt docker:stage
sbt docker:publishLocal
```

# Start it

```
docker run --rm -p 9000:9000 pdfgen-scala-play-example
```
