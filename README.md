Björn Scheppler, 13.8.2018

# eUmzug-Plattform 2018 (eumzug-plattform-2018)
In diesem Projekt ist eine mögliche Lösung für den UmzugsmeldepProzess entwickelt.
Die Lösung entstand im Rahmen des Moduls Geschäftsprozesssintegration im Studiengang
Wirtschaftsinformatik an der ZHAW School of Management and Law basierend auf der Lösung
vom HS 2017, aber architektonisch und technisch auf den Stand für HS 2018 gebracht.

Enthalten sind folgende Funktionalitäten:
1. Spring Boot 2.0.2 konfiguriert für Tomcat
2. Camunda Spring Boot Starter 3.0.0
3. Camunda Process Engine, REST API und Webapps (Tasklist, Cockpit, Admin) in der Version 7.9
4. H2-Datenbank-Unterstützung (von Camunda Engine benötigt)
5. "Sinnvolle" Grundkonfiguration in application.properties für Camunda, Datenbank und Tomcat
