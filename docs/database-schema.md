
Das Datenbank-Schema beschreiben.

Den Unterschied zwischen Basisdaten und Testdaten klar machen.

Prüfen wie man Basisdaten zusammen mit Evolutions anwenden und aufsetzen kann um einen guten
Basisdatenbestand bei erstmaliger Initialisierung der Applikation zu gewährleisten.

Prüfen ob eine vollständige Trennung von Schema und Basisdaten möglich ist,
so dass die Basisdaten auch typisiert angelegt werden könnten.

Grund: Basisdaten können sonst verwahlosen wenn sie mit in den Evolution-Files vorhanden sind.
Eine frische Initialisierung die auch typisiert stattfindet kann diese Up-To-Date halten.

Table und field Naming Best-Practices
- X-Tables
- Klein vs. Großschreibung
- Singular vs. Plural


ON DELETE CASCADE zeigen und beschreiben warum man es braucht und wann man welche ON DELETE 
nutzen sollte.

TRIGGERS zeigen und für created_at und updated_at felder nutzen um diese automatisch zu bestücken.
