# HbciPush - Readme

HbciPush stellt eine HBCI-Verbindung zu deiner Bank her, holt sich die letzten
Umsätze und speichert sie in einer SQLite-Datenbank.

Die Installation ist nicht unbedingt problemlos. Außerdem müssen die Kontodaten,
inklusive der PIN gespeichert, daher sollte das Programm nur auf einem Server
installiert werden, wo man sehr genau kontrollieren kann, wer Zugriff hat.

Falls das jemandem zu unsicher ist: Es ist Open-Source, macht es ruhig sicherer.

## 1. Requirements

1. Java 1.6
    
## 2. Installation

1. Ein Verzeichnis `passports/` erstellen.
2. Installieren. Bei diesem Vorgang wird zunächst eine konto.db erstellt (die SQLite-Datenbank) und dann eine entsprechende Datei im `passports/`-Verzeichnis, wo die Kontodaten gespeichert sind.

   java -jar HbciPush.jar --install
    
3. Die vorher vergebene Passphrase und die Konto-PIN  in die hbci.properties schreiben, etwa so:

    pin=123123
    passphrase=kontodatenpassphrase123

4. Ausführen: 

    java -jar HbciPush.jar

5. Sich ein Script schreiben, was die SQLite-Datenbank ausliest und die Daten verwertet.