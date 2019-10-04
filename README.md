# eUmzug-Plattform 2018 (eumzug-plattform-2018)

> Autoren der Dokumentation: Björn Scheppler

> Dokumentation letztmals aktualisiert: 31.10.2018

> TOC erstellt mit https://ecotrust-canada.github.io/markdown-toc/

In diesem Projekt ist eine mögliche Lösung für den [UmzugsmeldeProzess](https://www.egovernment.ch/de/umsetzung/schwerpunktplan/e-umzug-schweiz/) entwickelt.

Die Lösung entstand im Rahmen des **Moduls Geschäftsprozesssintegration im Studiengang Wirtschaftsinformatik** an der ZHAW School of Management and Law basierend auf der Lösung vom HS 2017, aber architektonisch und technisch auf den Stand für HS 2018 gebracht.

## Inhaltsverzeichnis
  * [Anforderungsspezifikation und Aufgabenstellung](#anforderungsspezifikation-und-aufgabenstellung)
    + [Wie können Studierende Bonus-Punkte sammeln (in Aufgabenstellung verschieben)](#wie-k-nnen-studierende-bonus-punkte-sammeln--in-aufgabenstellung-verschieben-)
  * [Architektur der Umzugsplattform inklusive Umsystemen](#architektur-der-umzugsplattform-inklusive-umsystemen)
    + [Haupt-Komponenten und ihr Zusammenspiel](#haupt-komponenten-und-ihr-zusammenspiel)
    + [Umzugsplattform](#umzugsplattform)
    + [Personenregister sowie Gebäude- und Wohnungsregister](#personenregister-sowie-geb-ude--und-wohnungsregister)
    + [VeKa-Center-Auskunftsdienst](#veka-center-auskunftsdienst)
    + [Stripe-Online-Bezahldienst](#stripe-online-bezahldienst)
    + [Einwohnerkontrollsysteme (EKS)](#einwohnerkontrollsysteme--eks-)
    + [EKS-Kommunikationsservice](#eks-kommunikationsservice)
    + [Kantonaler Benachrichtigungsdienst](#kantonaler-benachrichtigungsdienst)
  * [(Technische) Komponenten der Umzugsplattform](#-technische--komponenten-der-umzugsplattform)
  * [Erforderliche Schritte für das Testen der Applikation](#erforderliche-schritte-f-r-das-testen-der-applikation)
    + [Voraussetzungen](#voraussetzungen)
    + [Deployment](#deployment)
  * [Nutzung (Testing) der Applikation](#nutzung--testing--der-applikation)
    + [Manuelles Testen](#manuelles-testen)
    + [Semi-Manuelles Testen](#semi-manuelles-testen)
    + [Automatisiertes Testen](#automatisiertes-testen)
  * [Prototypische Vereinfachungen](#prototypische-vereinfachungen)
  * [TODOs](#todos)
  * [Mitwirkende](#mitwirkende)

## Anforderungsspezifikation und Aufgabenstellung
Dieses Projekt ist die Musterlösung für eine Aufgabenstellung, welche den Studierenden gestellt wurde. Die Aufgabenstellung (PDF), das BPMN-Modell der im nächsten Kapitel abgebildeten Architektur sowie die originalen eCH-Anforderungsdokumente sind im Ordner **src/docs** abgelegt.
Unter anderem befindet sich dort im Ordner **exception-handling** eine Excel-Datei als Musterlösung für verschiedene Ausnahmefälle und ihrer Behandlung. Diese Ausnahmefallbehandlungen sind soweit sinnvoll auch in den jeweiligen BPMN-Modellen vorhanden.

### Wie können Studierende Bonus-Punkte sammeln (in Aufgabenstellung verschieben)
1. **Zahlungsprozess fortgeschritten**: Statt bloss Kreditkarten soll auch SEPA unterstützt werden, was aber einen Rattenschwanz an Konsequenzen mit sich bringt aufgrund des asynchronen Zahlungsvorgangs:
    1. Der Zahlungsvorgang muss rausgelöst werden aus "Umzugsmeldung erfassen und bezahlen" und parallel stattfinden mit "Mit EK-Systemen kommunizieren".
    2. Es muss ein REST-Endpoint registriert werden, um die Stripe Webhook-Notifications empfangen zu können.
    3. Falls von Stripe eine Ablehnung kommt, dann muss der Meldepflichtige benachrichtigt werden und ihm erneut ein User Task für einen neuen Zahlungsversuch angezeigt werden.
    4. (Falls von den Einwohnergemeinden eine Ablehnung kommt, dann muss per Stripe eine Rückerstattung veranlasst werden)
4. **Benutzerverwaltung**: Neue Benutzer sollen sich bei der BEservices-Plattform zunächst registrieren. Im Idealfall bedeutet dies:
    1. Zusätzliche eigene "Webapp" mit Registrierungs-Formular
    2. Über REST wird ein neuer Benutzer in Camunda angelegt
    3. Eine Registrierung umfasst auch Telefon-Verifizierung mittels SMS (per Twilio) oder einfacher eine E-Mail-Verifizierung
    4. Nach erfolgreicher Registrierung wird der Benutzer an die Welcome-App von BEservices geleitet
5. **PDF generieren mit Abschlussbestätigung**:
    1. Nach dem erfolgreichen Erfassen und Zahlen soll ein PDF generiert werden mit allen Angaben (analog AlleAngabenPruefenForm).
    2. Dieses soll bei der Aktivität "Erfolgreiche Umzugsmeldung-Erfasung mitteilen" in ein Base64-String serialisiert werden.
    3. Dieser wird dem kantonalen Benachrichtigungsdienst übergeben, damit dieser das deserialisierte PDF der Mail anhängen kann.
6. **Zusatzdienste erfassen**: Also z.B. Parkkarten oder Hundedaten.

## Architektur der Umzugsplattform inklusive Umsystemen
Die Umzugsplattform benötigt für das Funktionieren verschiedene Komponenten, welche teilweise in der Umzugsplattform selbst (= das vorliegende Maven-Projekt) enthalten sind und teilweise extern.

### Haupt-Komponenten und ihr Zusammenspiel
Die Haupt-Komponenten und ihr Zusammenspiel sind in der folgenden Grafik abgebildet. Was die unterschiedlichen **Farben** bedeuten wird unterhalb der Grafik erläutert. Im Anschluss daran wird jede Hauptkomponente etwas ausführlicher beschrieben und ihre Implementation begründet.

![Abbildung Haupt-Komponenten](src/docs/architecture/MainProcessArchitectureView.png "Abbildung Haupt-Komponenten")

Die **Farben** bedeuten dabei:
- **Weiss/farblos**: Die eigentliche Umzugsplattform mit dem Hauptprozess, welche als Camunda Spring Boot-Applikation implementiert ist. Service Tasks, welche nicht eingefärbt sind, werden über JavaDelegates implementiert, User Tasks über Embedded Forms, die in der Camunda Webapp Tasklist eingebettet sind.
- **Blau (Aufrufprozesse)**: Implementation als eigener Prozess innerhalb der Umzugsplattform, welcher über eine [Call Activity](https://docs.camunda.org/manual/7.9/reference/bpmn20/subprocesses/call-activity/) aufgerufen wird
- **Violett (Microservice mit Camunda)**: Implementation als Microservice-Applikation, welche wie die Umzugsplattform selbst auch als Camunda Spring Boot-Applikation implementiert ist.  Die Einbindung in den Hauptprozess geschieht über das [External Task Pattern](https://docs.camunda.org/manual/7.9/user-guide/process-engine/external-tasks/).
- **Grün (Microservice ohne Camunda)**: Implementation als Microservice-Applikation, welche jedoch ohne Process Engine auskommen, sondern lediglich simple Spring Boot-Applikationen sind.
- **Rot und Orange (Umsysteme per REST/SOAP)**: Die rot und orange eingezeichneten Systeme sind in einer produktiven Umgebung komplett in einer anderen Verantwortung als beim Kanton Bern, sprich bei den Gemeinden (Einwohnerkontrollsysteme EKS), dem Bund (GWR), einer anderen Kantonsstelle (Personenregister), einem privaten Anbieter mit öffentlichem Auftrag (VeKa-Center) oder einem komplett privaten Anbieter (Stripe). Sie könnten entsprechend in irgendeiner Technologie implementiert sein. Für die Umzugsplattform relevant ist lediglich, dass diese über definierte Schnittstellen erreichbar sind, konkret über SOAP-Schnittstellen (Rot) gemäss eCH-Standards oder über REST (Orange). Für Testzwecke sind diese Umsysteme gemocked, jeweils in einer eigenen Github-Repository verfügbar und dokumentiert.

### Umzugsplattform
1. **Umzugsplattform insgesamt**: Der Einfachheit halber umfasst die Plattform alle folgende Komponenten in einer Applikation, die produktiv teilweise separat deployed würden:
    1. **Applikationsserver und Webserver**: Über Spring Boot wird automatisch ein In-Memory-Tomcat-Applikationsserver (inkl. Webserver) gestartet.
    2. **Process Engine**: Darin eingebettet wird die Camunda Process Engine als Applikation ausgeführt und ist per REST von aussen zugreifbar. Sie umfasst nebst der BPMN Core Engine auch einen Job Executor für das Erledigen asynchroner Aufgaben (z.B. Timer).
    3. **Process Engine-Datenbank**: Da eine Process Engine eine State Machine ist, muss sie irgendwo den Status laufender als auch abgeschlossener Prozessinstanzen persistieren können. Dies geschieht über eine File-basierte H2-Datenbank.
    4. **Umzugsplattform-Datenbank**: Gewisse Stammdaten (z.B. Liste aller Gemeinden) als auch ausgewählte Bewegungsdaten erfolgreich oder fehlgeschlagen abgeschlossener Prozessinstanzen sollen in einer Umzugsplattform-Datenbank gehalten werden. Der Einfachheit halber wird hierfür dieselbe Datenbank verwendet wie für die Process Engine.
    5. **Tasklist-Applikation**: Damit der Meldepflichtige seine Meldung erfassen kann, wird eine Client-Applikation benötigt, die im Browser ausgeführt werden kann. Hierfür wird die ebenfalls im Applikations- und Webserver Tomcat eingebettete Camunda Tasklist WebApp eingesetzt. In einer produktiven Lösung würde diese sicher separat deployed oder sogar durch eine spezifisch für den Kanton Bern entwickelte Tasklist-Applikation ersetzt, welche über REST mit der Process Engine kommuniziert. Immerhin wurde die Tasklist-Webapp (und die übrigen Webapps) angepasst (deutsche Übersetzung, Logo und Farben, Stripe Checkout integriert) in einem eigenen WebJAR-Projekt. Details hierzu siehe https://github.com/zhaw-gpi/be-services-plattform
    6. **Cockpit-Applikation**: Damit der Systemadministrator bei technischen Problemen und der Prozessverantwortliche aus Gründen des Monitorings und Controllings die laufenden und vergangenen Prozessinstanzen verwalten kann, wird die ebenfalls im Applikations- und Webserver Tomcat eingebettete Camunda Cockpit Webapp genutzt. In einer produktiven Lösung würde diese vermutlich zwar genutzt, würde aber auch die Daten von anderen Process Engines (der Microservices) enthalten, damit alle Daten an einem Ort eingesehen werden können.
2. **Hauptprozess 'Umzug melden'**:
    1. Dies ist der Hauptprozess (End-to-End), welcher aus der Tasklist-Applikation heraus vom Meldepflichtigen gestartet wird.
    2. Da wir auf eine eigene Tasklist-Applikation verzichten und stattdessen die Camunda Webapp Tasklist benutzen, muss der Benutzer bereits an dieser Webapp angemeldet sein, um überhaupt diesen Prozess starten zu können. Dies wäre in einer produktiven Applikation wohl nicht sinnvoll, würde aber aktuell wohl am ehesten dem Modell des Kantons Zürich mit den ZH Services entsprechen, wo man die Steuererklärung auch erst ausfüllen kann, wenn man sich an der Plattform angemeldet hat.
    3. Innerhalb des Hauptprozesses gibt es verschiedene Aktivitäten gemäss Darstellung, die im Folgenden von links nach rechts erläutert werden.
3. **Aufrufprozess 'Umzugsmeldung erfassen und bezahlen'**:
    1. Dies ist derjenige Teil des Hauptprozesses, in welchem der Meldepflichtige über verschiedene Formulare sowie Anbindung von Umsystemen (GWR und Personenregister) die **Umzugsmeldung erstellt**.
    2. Dieser Teil wurde der Einfachheit halber bewusst **nicht als eigenständiger Microservice ausgelagert**, weil dies sonst dazu führen würde, dass der Benutzer mit zwei Tasklists agieren müsste (inkl. Anmeldung & Co.). In einem produktiven Szenario würde man allerdings ohnehin die Process Engine von der Tasklist-Applikation trennen, so dass dann ein Microservice geeignet wäre.
    3. Streng genommen wäre statt einem über eine **Call Activity** aufgerufenen Prozess ein **Embedded Subprocess** passender. Denn dieser macht für sich alleine ohne Hauptprozess keinen Sinn, kann nun aber aus der Tasklist unsinnigerweise separat gestartet werden. Die nun gewählte Call Activity ist aber tool-bedingt erforderlich, weil der Camunda Modeler (noch) kein Verlinken von Teilprozessmodellen (Embedded Subprocesses) mit dem Hauptprozess erlaubt.
    4. Innerhalb des Aufrufprozesses werden weitere Aufrufprozesse ebenfalls über Call Activities eingebunden aus dem soeben genannten Grund. Was in den jeweiligen Prozessen und Aktivitäten gemacht wird, kann aus den ausführlichen **Element Descriptions** pro BPMN-Element entnommen werden (in Camunda Modeler z.B. im Properties-Panel sichtbar). Hier soll nur kurz auf diejenigen Aktivitäten eingegangen werden, welche **mit anderen Komponenten kommunizieren**:
        1.  **Person identifizieren**: Hier geschieht eine Kommunikation mit der Komponente **Personenregister** über SOAP, implementiert über die Spring Webservice-Komponenten.
        2.  **Wegzugsadresse prüfen** und **Wohnungen an Zuzugsadresse bestimmen**: Hier geschieht eine Kommmunikation mit der Komponente **Gebäude- und Wohnungsregister GWR**, ebenfalls über SOAP.
        3.  **Grundversicherung prüfen**: Hier geschieht eine Kommunikation mit der Komponente **VeKa-Center-Auskunftsdienst** über REST, implementiert über den Camunda HTTP-Connector.
        4.  **Zahlung durchführen**: Hier geschieht eine Kommunikation mit dem **Online-Bezahldienst Stripe**. Die Kommunikation geschieht über REST, implementiert einerseits client-seitig über die Stripe Checkout JavaScript-Library und server-seitig über die Stripe Java API Library.
4. **External Task 'Mit EK-Systemen kommunizieren'**:
    1. In diesem Teil des Hauptprozesses erfolgt die automatisierte Kommunikation mit den **Einwohner-Kontrollsystemen** der Wegzugs-/Umzugs-/Zuzugsgemeinden.
    2. Diese geschieht nicht direkt, sondern über einen Dienst (**EKS-Kommmunikationsservice**), welcher über das **External Task Pattern** eingebunden wird. Dabei wird lediglich festgehalten, dass Arbeit von einem bestimmten Typ zu erledigen ist. External Task Workers wie der genannte Dienst können sich bei der Umzugsplattform registrieren und die zu erledigende Arbeit durchführen.
5. **External Tasks 'xyz mitteilen'**:
    1. In diesem Teil des Hauptprozesses (und auch teilweise in den aufgerufenen Prozessen) erfolgt die automatisierte **Ein-Weg-Kommunikation mit dem Meldepflichtigen**.
    2. Diese geschieht nicht direkt, sondern über einen Dienst (**Kantonaler Benachrichtigungsdienst**), welcher ebenfalls über das **External Task Pattern** eingebunden wird.
6. **JavaDelegate 'xyz persistieren'**:
    1. In diesem Teil des Hauptprozesses (und auch teilweise in den aufgerufenen Prozessen) erfolgt die automatisierte **Persistierung von Personendaten und Prozessstatus in der Umzugsplattform-Datenbank**.
    2. Da die Datenbank für Demo-Zwecke vereinfacht dieselbe ist wie für die Process Engine...
    3. ... und da das Persistieren über einfache Repositories und Entities mittels Java Persistence API (JPA) **sehr einfach** ist,
    4. ... wird auf eine Auslagerung verzichtet, sondern die Persistierung geschieht stattdessen **über eine in der Umzugsplattform integrierte JavaDelegate-Klasse**, welche auf die Repositories zugreift.

### Personenregister sowie Gebäude- und Wohnungsregister
1. Diese Umsysteme müssen wir für unsere Demo-Zwecke **mocken**, also selbst irgendwie implementieren.
2. Sie enthalten nur **einfache Prozesse und auch keine User Tasks**, daher verzichten wir auf den "Ballast" der Camunda Process Engine und implementieren sie als **einfache Spring Boot-Applikationen**.
3. Sie kommunizieren mit der Aussenwelt über **SOAP**.
4. Die detaillierte Dokumentation ist zu finden in folgenden **Github-Repositories**:
    1. Personenregister: https://github.com/zhaw-gpi/personenregister-2018
    2. Gebäude- und Wohnungsregister: https://github.com/zhaw-gpi/gwr-2018

### VeKa-Center-Auskunftsdienst
1. Es gelten die Punkte 1 und 2 des vorherigen Kapitels.
2. Im Unterschied dazu erfolgt hier die Kommunikation mit der Aussenwelt über REST.
3. Die detaillierte Dokumentation ist zu finden in folgender **Github-Repository**: https://github.com/zhaw-gpi/vekacenter

### Stripe-Online-Bezahldienst
1. Mit **Stripe** existiert ein Bezahldienst, der für Entwickler **zum Testen gratis** genutzt werden kann.
2. Entsprechend müssen wir auch hier nichts selbst implementieren, sondern können den **realen Bezahldienst** out-of-the-box nutzen.
3. Nebst Stripe gäbe es auch **andere Anbieter und Architekturen**, welche z.B. [hier](https://medium.com/get-ally/how-to-architect-online-payment-processing-system-for-an-online-store-6dc84350a39) und [hier](https://dreamproduction.com/kreditkarten-ihrem-onlineshop-zahlungsloesungen-und-payment-anbieter-im-vergleich/) beschrieben sind. Stripe hat den Vorteil, dass es relativ einfach zu implementieren ist.

### Einwohnerkontrollsysteme (EKS)
1. Diese Umsysteme müssen wir wieder für unsere Demo-Zwecke **mocken**, also selbst irgendwie implementieren.
2. In der **Realität** sind solche EKS in der Regel **Module in umfassenden Gemeindesoftware-Paketen**. Zwei Bekannte sind etwa [Loganto von VRSG](https://www.abraxas.ch/de/loesungen/fachanwendungen/subjekte) oder [NEST](https://www.nest.ch/).
3. Daraus benötigen wir **gemocked lediglich einen Teil**, wo es um die Verwaltung von Zuzug/Wegzug/Umzug geht.
4. Dieser Teil ist aus Prozessicht relativ einfach (mindestens aufs Wesentliche beschränkt), enthält jedoch einen User Task, benötigt also ein Eingabeformular. Damit die Studierenden nicht noch eine Technologie mehr lernen müssen (z.B. Erstellen einer eigenständigen Webapplikation), wird auch das EKS **über Camunda Spring Boot implementiert**.
5. In der Realität haben wir es **nicht bloss mit einem EKS zu tun**, sondern mit sovielen, wie der Kanton Bern Gemeinden hat. Für Testzwecke wird dasselbe Maven-Projekt einfach mehrfach gestartet mit lediglich unterschiedlicher Port-Nummer und unterschiedlicher Datenbank.
6. Die detaillierte Dokumentation ist zu finden in folgender **Github-Repository**: ??????????????

### EKS-Kommunikationsservice
1. Wie weiter oben erwähnt kommuniziert die Umzugsplattform nicht direkt mit den Einwohnerkontrollsystemen, sondern über einen **separaten Dienst**.
2. Dies ist sinnvoll, weil es denkbar ist, dass nicht nur die Umzugsplattform mit den Einwohnerkontrollsystemen kommunizieren muss, sondern eine solche **auch in anderen Prozessen des Kantons** erforderlich ist (z.B. Einbürgerungsverfahren).
3. Für unsere Demo-Zwecke mocken wir aber **nur den für die Umzugsplattform relevanten Teil**.
4. Der Microservice ist aufgrund seiner Komplexität ebenfalls **als Camunda Spring Boot-Applikation implementiert**, wird vom Hauptprozess jedoch nicht aufgerufen, sondern über das **External Task Pattern** eingebunden, wie weiter oben erläutert.
5. Die Dokumentation des Microservices für die Umzugsplattform ist zu finden in folgendem **Github-Repository**: ??????????????

### Kantonaler Benachrichtigungsdienst
1. Aufgrund der **Komplexität** - es sollen nebst Mail-Benachrichtigung z.B. auch SMS-Benachrichtigung ermöglicht werden -, ist eine Implementation nur als JavaDelegate direkt in der Umzugsplattform nicht sinnvoll.
2. Vor allem wäre eine in die Umzugsplattform eingebettete Komponente auch deshalb wenig sinnvoll, weil wohl nicht bloss im "Umzug melden"-Prozess Mitteilungen zu versenden sind, sondern **auch in anderen Prozessen des Kantons Bern**.
3. Umgekehrt ist der Benachrichtigungs**prozess trivial**, so dass eine eigene Process Engine nicht sinnvoll erscheint. Zumal ja schon im Hauptprozess nachvollziehbar persistiert wird, wer über was benachrichtigt wurde.
4. Aus diesem Grund wird eine "einfache" **Spring Boot-Applikation ohne Camunda als Microservice** entwickelt.
5. Die Kommunikation mit diesem Microservice geschieht analog zu den EKS über das **External Task Pattern**.
6. Die Dokumentation des kantonalen Benachrichtigungsdienstes ist zu finden in folgendem **Github-Repository**: https://github.com/zhaw-gpi/kantonaler-benachrichtigungsdienst

## (Technische) Komponenten der Umzugsplattform
1. **Camunda Spring Boot Starter** 3.0.0 beinhaltend:
    1. Spring Boot-Standardkonfiguration mit Tomcat als Applikations- und Webserver
    2. Camunda Process Engine, REST API und Webapps (Tasklist, Cockpit, Admin) in der Version 7.9.2 (Enterprise Edition), wobei für die Webapps zusätzlich ein eigenes WebJAR-Projekt aus dem lokalen Maven-Repository geladen wird.
    3. Main-Methode in EumzugPlattform2018Application-Klasse
2. **Prozess-Komponenten**:
    1. @EnableProcessApplication-Annotation in EumzugPlattform2018Application-Klasse
    2. processes.xml mit minimaler Konfiguration der Prozessapplikation
    3. Ordner processes mit allen BPMN-Modellen
    4. Package processdata für POJOs, welche Objekte instanzieren, die nicht zu persistieren sind
    5. Package delegates mit JavaDelegate-Klassen, die von den Prozessen aufgerufen werden, um entweder direkt etwas zu tun oder als Vermittler zu anderen Serivces & Co. zu dienen (z.B. zu LocalPersonIdGeneratorService).
3. **Persistierungs-Komponenten**:
    1. JDBC-Komponente als Treiber
    2. H2-Datenbank-Unterstützung inklusive Console-Servlet über application.properties-Einstellung
    3. Java Persistence API (JPA) inklusive EumzugPlattform2018Model.jpa-Diagramm (von Jeddict erstellt)
    4. Package Repositories und Package entities für Municipality, Person, TransactionLog und Document
    5. JavaDelegates GetDocumentsDelegate, GetFeesDelegate, GetMunicipalityListDelegate und PersistUserEntriesAndStatusDelegate
    6. initialData.sql/xlsx in test/ressources, um die Datenbank initial mit sinnvollen Stammdaten zu füllen
4. **WebService (SOAP)-Komponenten**:
    1. Spring Boot Webservices-Komponenten für den Aufruf von SOAP-Services
    2. GenericWebServiceClientConfiguration-Klasse
    3. WebServiceHeaderActionCodeEnumeration-Klasse
    4. Package webserviceclients
    5. JAXB-Maven-Plugin, welches aus der Angabe der WSDL-URL automatisch Klassen generiert (eCH und gwr)
    6. Package helpers mit Helferlein-Klassen
    7. WebServiceHeaderActionCodeEnumeration-Klasse
    8. Endpoint-Deklarationen für GWR und Personenregister in application.properties
5. **Zahlungsabwicklungs-Komponenten**:
    1. Stripe Java API Library für die server-seitige Kommunikation mit Stripe
    2. StripeClientService-Klasse als Vermittler zwischen der API Library und ...
    3. CreateChargeDelegate-Klasse
    4. ErfassenDerZahlungsdetailsForm.html inklusive Verweis auf die Stripe Checkout JavaScript-Library
6. **Frontend-Komponenten**:
    1. Die bereits erwähnte Camunda Tasklist App, welche AngularJS, Bootstrap und das Camunda Forms SDK beinhaltet
    2. Camunda Spin (inklusive Jackson), um von/nach JSON zu (de-)serialisieren
    3. Ordner static/forms mit allen Embedded Forms-HTML-Dateien, welche den HTML-Code für die Forms sowie JavaScript-Code enthalten
7. "Sinnvolle" **Grundkonfiguration** in application.properties für Camunda, Datenbank und Tomcat
8. Ein **soapUI-Testprojekt** in test/ressources/EumzugPlattform2018Tests-soapui-project.xml

## Erforderliche Schritte für das Testen der Applikation
### Voraussetzungen
1. **Laufende Umsysteme**: Die Maven-Projekte Personenregister, Gebäude- und Wohnungsregister, Einwohnerkontrollsysteme, EKS-Kommunikationsservice, Kantonaler Benachrichtigungsdienst sowie VeKa-Center-Auskunftsdienst müssen gestartet sein. Details hierzu siehe in den ReadMe der verlinkten Github-Repositories.
2. **Camunda Enterprise**: Wenn man die Enterprise Edition von Camunda verwenden will, benötigt man die Zugangsdaten zum Nexus Repository und eine gültige Lizenz. Wie man diese "installiert", steht in den Kommentaren im pom.xml.
3. **Stripe-Konto und -Konfiguration**: Damit man den Stripe-Online-Bezahldienst nutzen kann, benötigt man einen Public Key und einen Secret Key. Beides erhält man nur, wenn man ein kostenloses Konto bei Stripe eröffnet. Details stehen etwa [hier](https://medium.com/oril/easy-way-to-integrate-payments-with-stripe-into-your-spring-boot-angular-application-c4d03c7fc6e) beschrieben. Im Wesentlichen sind die Schritte:
    1. Konto erstellen mit einer gültigen E-Mail-Adresse.
    2. Den Bestätigungslink in der erhaltenen Mail ausführen
    3. Sich anmelden bei https://dashboard.stripe.com/
    4. NICHT auf "Activate your account" klicken, solange man es nur zum Testen verwenden will.
    5. In der [API Keys-Seite](https://dashboard.stripe.com/account/apikeys) die beiden Schlüssel herauskopieren
    6. Den Publishable Key in /src/main/ressources/static.forms/ErfassenDerZahlungsdetailsForm.html im Code unter `var stripeCheckoutHandler = StripeCheckout.configure({` beim Parameter key einfügen
    7. Den Secret Key als Umgebungsvariable hinzufügen, wie dies in /src/main/ressources/application.properties beim Eintrag `stripe.apiKey` im Kommentar beschrieben ist.
4. **Angepasste Camunda Webapps**: Das Projekt https://github.com/zhaw-gpi/be-services-plattform muss gemäss der dort aufgeführten Anleitung konfiguriert und gebuilded sein, damit es im lokalen Maven-Repository zur Verfügung steht.

### Deployment
1. Wenn man die **Enterprise Edition** von Camunda verwenden will, benötigt man die Zugangsdaten zum Nexus Repository und eine gültige Lizenz. Wie man diese "installiert", steht in den Kommentaren im pom.xml.
2. **Erstmalig** oder bei Problemen ein **Clean & Build (Netbeans)**, respektive `mvn clean install` (Cmd) durchführen
3. Bei Änderungen am POM-File oder bei **(Neu)kompilierungsbedarf** genügt ein **Build (Netbeans)**, respektive `mvn install`
4. Für den **Start** ist ein **Run (Netbeans)**, respektive `java -jar .\target\NAME DES JAR-FILES.jar` (Cmd) erforderlich. Dabei wird Tomcat gestartet, die Datenbank erstellt/hochgefahren, Camunda in der Version 7.9 mit den Prozessen und den Eigenschaften (application.properties) hochgefahren.
5. Das **Beenden** geschieht mit **Stop Build/Run (Netbeans)**, respektive **CTRL+C** (Cmd)
6. Falls man die bestehenden **Prozessdaten nicht mehr benötigt** und die Datenbank inzwischen recht angewachsen ist, genügt es, die Datei DATENBANKNAME.mv.db im Wurzelverzeichnis des Projekts zu löschen.
7. HINWEIS ZU initalData.sql ANBRINGEN!!!

## Nutzung (Testing) der Applikation
### Manuelles Testen
1. In http://localhost:8081 mit dem Benutzer a und Passwort a **anmelden**.
2. **Tasklist**-App starten.
3. Start Process > **Umzug melden** wählen.
4. Dann **durchspielen**, wie dies auch ein Benutzer machen würde mit folgenden **Testhinweisen**:
    1. **Personendaten im Personenregister**:
        1. Entweder selbst nachschlagen in der **H2-Datenbank-Konsole** oder folgende Werte verwenden.
        2. **Umzugsberechtigt mit Familienmitgliedern** ist: Hans, Meier, 1.1.1970, männlich
        3. **Umzugsberechtigt ohne Familienmitglieder** ist: Pipi, Langstrumpf, 1.1.1960, weiblich
        3. **Nicht umzugsberechtigt** ist: Ruth, Meier, 1.1.1980, weiblich
    2. **Adressen im Gebäude- und Wohnungsregister**:
        1. Entweder selbst nachschlagen in der **H2-Datenbank-Konsole** oder folgende Werte verwenden.
        2. Für jede PLZ im Kanton Bern (z.B. 3127 Lohnstorf oder 3073 Gümligen) sind zwei Adressen erfasst:
            1. **Bahnhofstrasse 1**
            2. **Dorfstrasse 13a**
    3. **E-Mail-Adresse bei Kontaktangaben**: Wenn man wirklich eine Mail erhalten möchte und mail.debug nicht auf true gesetzt ist, dann muss hier die eigene Mail-Adresse angegeben werden.
    4. **Grundversicherung prüfen**: Hinterlegt sind folgende vier Karten:
        1. 360269466985268: Gehört zu Hans Meier, ist aber 2016 abgelaufen.
        2. 743794085316616: Gehört zu Hans Meier und ist gültig
        3. 788475892684035: Gehört zu Anna Meier und ist gültig, aber nicht grundversichert
        4. 596947027238113: Gehört zu Annaliese Meier und ist gültig und grundversichert
    5. **Zahlungsdetails**:
        1. Für das Testen von Stripe Checkout kann man eine beliebige CVC verwenden und je nach gewünschtem Testergebnis ein Datum in der Zukunft oder Vergangenheit.
        2. Je nachdem, welches Testergebnis man wünscht, gibt es die verschiedensten Kartnnummern, welche [hier](https://stripe.com/docs/testing#cards) aufgelistet sind. Die wohl am häufigsten benötigten sind:
            1. 4000007560000009: Gültige in der Schweiz ausgestellte Visa-Karte
            2. 5555555555554444: Gültige amerikanische Mastercard-Karte
            3. 4100000000000019: Aus Sicht des Kartenherausgebers gültige Karte, die aber von Stripe aufgrund Betrugsverdacht blockierte Karte
            4. 4000000000000002: Vom Kartenherausgeber abgelehnte Karte ohne Angabe von Gründen
    6. **Zahlungserfolg prüfen**: Um zu sehen, ob eine Zahlung auch "wirklich" durchgeführt wurde, bei Stripe anmelden und die [Events-Seite](https://dashboard.stripe.com/test/events) aufrufen.
    7. **Persistierung prüfen**: Um zu sehen, ob ein Status im Transaktionslog der Umzugsdatenbank persistiert wurde, einen SELECT auf die Tabelle TRANSACTIONLOG durchführen wie beschrieben in ???????
    8. **Prozessverlauf überwachen/nachvollziehen**: Um zu sehen, wo der Prozess momentan steckt (Runtime) oder wo abgeschlossene Instanzen durchliefen (History) kann die Cockpit-Webapplikation genutzt werden unter http://localhost:8081.

### Semi-Manuelles Testen

### Automatisiertes Testen

## Prototypische Vereinfachungen
1. **Session-Handling, Benutzerverwaltung, usw.**: In der Realität müssen sich Melde pflichtige nicht anmelden (und damit zunächst registrieren), sondern können eine Meldung auch ohne Anmeldung starten. Unter Verwendung von Camunda Webapps mit User Tasks muss aber ein Benutzer angemeldet sein, damit er den Prozess starten kann und damit ihm Tasks zugewiesen werden können. Wir ignorieren dies und stattdessen ist der Meldepflichtige stets der Benutzer "a", was natürlich dann Probleme gibt, wenn gleichzeitig mehrere Prozessinstanzen laufen, denn dann werden diesem mehrere/verschiedene Aufgaben zugewiesen. Da Name, Vorname, AHV-Nummer, usw. pro Prozessinstanz unterschiedlich ist, sollte es aber dennoch möglich sein, das im Griff zu haben (siehe auch nächster Punkt).
2. **Page Flow vs. BPMN Flow**: In der Realität würde der Meldepflichtige ein Formular nach dem anderen ausfüllen INNERHALB von einem einzigen User Task (oder einem Subprocess), denn verteilt über mehrere User Tasks würde ja bedeuten, dass jedes ausgefüllte Formular einen Eintrag in der Tasklist verursacht und der Benutzer dadurch überhaupt eine Tasklist braucht, was in der Realität nicht zwingend der Fall ist. Auch sollte man in der Realität natürlich im Browser zurück springen können zu einem vorherigen Formular. In der Realität könnte dies z.B. über einen JSF-PageFlow innerhalb des User Tasks gelöst werden. StephenOTT hat [hier](https://forum.camunda.org/t/chaining-user-tasks-to-create-an-interactive-flow-sort-of-a-wizard-style/613/9?u=scepbjoern) gut dargelegt, dass wenn es darum geht, Page Flows (Wizards) abzubilden, ein BPMN-Flow (wie hier umgesetzt) keine geeignete Form ist (allenfalls für das Dokumentieren, aber nicht ausführbar), sondern dann würde man eher eine Lösung wie den form.io-FormBuilder im wizard-Modus nutzen.
3. Beim Umsystem **Personenregister** wird die municipalityId und municipalityName vermutlich deshalb mitgeliefert, weil eine Person in mehreren Gemeinden registriert sein kann (Hauptwohnsitz und Wochenaufenthalter). Das heisst, moveAllowed müsste eigentlich bei der Beziehungstabelle sein zwischen Municipality und Resident. Dies mit JPA umzusetzen, ist gar nicht so einfach, u.a. braucht es dann eine eigene Entität für diese Beziehungstabelle mit einem Pseudo-Primärschlüssel: https://www.thoughts-on-java.org/many-relationships-additional-properties/. Daher lassen wir dies weg.
4. **Schema Validation**: Mit @SchemaValidation bei der Webservice-Endpoint-Klasse werden lediglich Standard-Fehlermeldungen ausgegeben, nicht aber benutzerdefinierte. Das ist ok bei einem Prototyp. Störender ist, dass XJC keine Annotations für die XSD-Restrictions hinzufügt (also, dass z.B. ein String nicht leer oder länger als 40 Zeichen sein darf). Es gibt teilweise Plugins, aber die sind seit mehreren Jahren nicht mehr aktualisiert => man müsste entweder ein eigenes Plugin schreiben oder aber die Annotations in den generierten Java-Klassen von Hand hinzufügen, aber dann wäre die Generierung im Build-Prozess natürlich nicht mehr opportun.

## TODOs
1. DokumenteHochladenForm: Bei mitumziehenden Personen muss nur die Hauptperson aktuell Dokumente hochladen
2. Daten aufräumen: Auf keinen Fall sollen Prozessvariablen wie z.B MunicipalityList persistiert werden, welche Stammdaten enthalten oder nur Hilfsvariablen => aufräumen am Schluss (eigener Service Task) => allenfalls müssen aber sogar noch alte Aktivitäten gelöscht werden, weil ja schon zuvor diese Variablen persistiert wurden
3. Aufruf des EKS-Kommunikationsdienstes

## Mitwirkende
1. Björn Scheppler: Hauptarbeit
2. Peter Heinrich: Der stille Support im Hintergrund mit vielen Tipps sowie zuständig
für den Haupt-Stack mit SpringBoot & Co.
3. Raphael Schertenleib: Etliche Formulare
4. Studierende aus dem Herbstsemester 2017:
    1. Gruppe TZb02 (Bekim Kadrija, Jovica Rajic, Luca Belmonte, Simon Bärtschi, Sven 
    Baumann):
        1. Formulare: DatePicker mit Format TT.MM.JJJJ
        2. Mitzuziehende Personen auswählen
        3. Dokumente hochladen
        4. PDF mit allen relevanten Informationen wird als E-Mail-Anhang mitgesendet
        5. Kleinere Verbesserungen an der originalen Musterlösung
    2. Gruppe TZa02 (Alessandro Paradiso, Davor Gavranic, Lars Günthardt, Luka Devcic,
    Robin Chahal):
        1. Wohnungen auswählen
        2. Kleinere Verbesserungen an der originalen Musterlösung
    3. Gruppe TZa03 (Christian Sonntag, Felix Huber, Lukas Brütsch, Tamara Wenk, Vladan 
    Jankovic):
        1. Formulare: Mehrspaltiges Layout
        2. Formulare: Placeholders
        3. Formulare: Bessere Alert- und Help Block-Texte
    4. Gruppe TZb05 (Arbr Wagner, Christoffer Brunner, Leulinda Gutaj, Martin Schwab, 
    Saskia Iwaniw): Formular "Alle Angaben prüfen"