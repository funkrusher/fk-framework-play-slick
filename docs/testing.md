Testing implementieren 

Test-Strategie.

Mocking.
- Controllers testen mit Mocks für hintere Teile
- DAOs testen 
- ...

Testcontainers nutzen um Testdatenbank hochzufahren bei Ausführen der Tests.

Specs2 vs ScalaTest vergleichen

Verweis auf database-schema und prüfen wie geschickt Testdaten vorbereitet werden können.
Je stärker typsiert desto besser. Schema kann weiterhin als SQL hochgefahren werden.

Tempfs-Nutzung prüfen um Testdatenbank in den RAM zu laden.

Interfaces/Traits nutzen um verschiedene Layer-Klassen (DAO, Repository, ...) mockbar machen zu können.