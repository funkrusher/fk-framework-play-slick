# Fk-Framework: Best-Practices for a Play-Framework Multi-Module-Project with Slick

TODO:

- Warning: This documentation is just a stub and collection of different documentations on the relevant topics, 
and needs to be completely overhauled from scratch, as soon as all the basics are implemented.
 
---

important:
- always disable! the play2-compiler in intellij if you use intellij. 
  - a) play2-compiler in intellij slows down your build
  - b) play2-compiler in intellij is not needed
  - c) play2-compiler in intellij does not show problems nicely in build-window.
  - d) play2-compiler does not compile tests (can not resolve classpath correctly)
  - e) always delete all target/ folders when you disable the play2-compiler in the settings.

---


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

------------------------

# Play Multi-Project

https://www.scala-sbt.org/1.x/docs/Multi-Project.html
see: https://pbassiner.github.io/blog/defining_multi-project_builds_with_sbt.html

An example of building applications that consist of multiple services.

First off, the top-level project is an sbt multi-project. It defines two Play applications, `app1` and `app2`. It also
defines a non-Play project `model` that contains code shared by the Play applications.

Each Play app is then split into a master app, and Play sub-modules (in this case only an admin module). Play sub-modules
follow Play conventions, but require to use a separate routes file and sub-package for controllers and views. They
cannot have conf files.

Note how sbt plugins are only defined by the root build.

## Running

1. Play2-Compiler in Settings wählen und den Root des Projects angeben.

You can run each app individually by using `sbt ";project app<n>;run`. Two scripts are provided for this.

Intellij

SBT-Task with following task:
```
"project app1" run
"project app2" run
```

- `Allow Parallel Run` den Haken setzen
- `Use SBT Shell` den Haken wegnehmen

- Nun eine `Compound` Run-Configuration erstellen mit der beide Services gestartet werden können
- Jeweils einen unterschiedlichen Port angeben.
- Den "Services"-Tab nutzen um die Services alle schön komfortabel zu starten oder zu debuggen parallel.
- Alternativ: Shell-Scripts nutzen um zu starten.


## Deploy

es reicht aus im Root-Verzeichnis des Projekts folgendes Kommando auszuführen
um für alle Subprojekte die das können entsprechende Dockerimage und co. zu erstellen so dass das Projekt bereit ist
deployed zu werden (dies kann über gitlab-pipelines gemacht werden):

```
sbt docker:stage
```

## Testing

`sbt test` will run all tests in all sub-projects.




-------------


Deploy Build

sbt fk_scheduler/universal:packageBin
sbt fk_server/universal:packageBin

Ausgabe erfolgt im "target/universal" ordner des jeweiligen projects