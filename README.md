# eUmzug-Plattform 2018 (eumzug-plattform-2018)

> Autoren der Dokumentation: Björn Scheppler

> Dokumentation letztmals aktualisiert: 14.8.2018

> TOC erstellt mit https://ecotrust-canada.github.io/markdown-toc/

In diesem Projekt ist eine mögliche Lösung für den [UmzugsmeldepProzess](https://www.egovernment.ch/de/umsetzung/schwerpunktplan/e-umzug-schweiz/) entwickelt.

Die Lösung entstand im Rahmen des Moduls Geschäftsprozesssintegration im Studiengang Wirtschaftsinformatik an der ZHAW School of Management and Law basierend auf der Lösung vom HS 2017, aber architektonisch und technisch auf den Stand für HS 2018 gebracht.

## Inhaltsverzeichnis
  * [Architektur der Umzugsplattform inklusive Umsystemen](#architektur-der-umzugsplattform-inklusive-umsystemen)
  * [Komponenten und Funktionalitäten der Umzugsplattform](#komponenten-und-funktionalitaeten-der-umzugsplattform)

## Architektur der Umzugsplattform inklusive Umsystemen
Die Umzugsplattform benötigt für das Funktionieren verschiedene Komponenten, welche teilweise in der Umzugsplattform selbst (= das vorliegende Maven-Projekt) enthalten sind und teilweise extern.

### Haupt-Komponenten und ihr Zusammenspiel
Die Haupt-Komponenten und ihr Zusammenspiel sind in der folgenden Grafik abgebildet. Was die unterschiedlichen Farben bedeuten wird unterhalb der Grafik erläutert. Im Anschluss daran wird jede Hauptkomponente etwas ausführlicher beschrieben und ihre Implementation begründet.

![Abbildung Haupt-Komponenten](src/docs/architecture/MainProcessArchitectureView.png "Abbildung Haupt-Komponenten")

Die **Farben** bedeuten dabei:
- **Weiss/farblos**: Die eigentliche Umzugsplattform mit dem Hauptprozess, welche als Camunda Spring Boot-Applikation implementiert ist. Service Tasks, welche nicht eingefärbt sind, werden über JavaDelegates implementiert, User Tasks über Embedded Forms, die in der Camunda Webapp Tasklist eingebettet sind.
- **Blau**: Implementation als eigener Prozess innerhalb der Umzugsplattform, welcher über eine [Call Activity](https://docs.camunda.org/manual/7.9/reference/bpmn20/subprocesses/call-activity/) aufgerufen wird
- **Violett**: Implementation als Microservice-Applikation, welche wie die Umzugsplattform selbst auch als Camunda Spring Boot-Applikation implementiert ist.  Die Einbindung in den Hauptprozess geschieht über das [External Task Pattern](https://docs.camunda.org/manual/7.9/user-guide/process-engine/external-tasks/).
- **Grün**: Implementation als Microservice-Applikation, welche jedoch ohne Process Engine auskommen, sondern lediglich simple Spring Boot-Applikationen sind.
- **Rot und Orange**: Die rot eingezeichneten Systeme sind in einer produktiven Umgebung komplett in einer anderen Verantwortung als beim Kanton Bern, sprich bei den Gemeinden (Einwohnerkontrollsysteme EKS), dem Bund (GWR), einer anderen Kantonsstelle (Personenregister), einem privaten Anbieter mit öffentlichem Auftrag (VeKa-Center) oder einem komplett privaten Anbieter (Stripe). Sie könnten entsprechend in irgendeiner Technologie implementiert sein. Für die Umzugsplattform relevant ist lediglich, dass diese über definierte Schnittstellen erreichbar sind, konkret über SOAP-Schnittstellen (Rot) gemäss eCH-Standards oder über REST (Orange). Für Testzwecke sind diese Umsysteme gemocked, jeweils in einer eigenen Github-Repository verfügbar und dokumentiert.

### Umzugsplattform
1. **Umzugsplattform insgesamt**: Der Einfachheit halber umfasst die Plattform alle folgende Komponenten in einer Applikation, die produktiv teilweise separat deployed würden:
    1. **Applikationsserver und Webserver**: Über Spring Boot wird automatisch ein In-Memory-Tomcat-Applikationsserver (inkl. Webserver) gestartet.
    2. **Process Engine**: Darin eingebettet wird die Camunda Process Engine als Applikation ausgeführt und ist per REST von aussen zugreifbar. Sie umfasst nebst der BPMN Core Engine auch einen Job Executor für das Erledigen asynchroner Aufgaben (z.B. Timer).
    3. **Process Engine-Datenbank**: Da eine Process Engine eine State Machine ist, muss sie irgendwo den Status laufender als auch abgeschlossener Prozessinstanzen persistieren können. Dies geschieht über eine File-basierte H2-Datenbank.
    4. **Umzugsplattform-Datenbank**: Gewisse Stammdaten (z.B. Liste aller Gemeinden) als auch ausgewählte Bewegungsdaten erfolgreich oder fehlgeschlagen abgeschlossener Prozessinstanzen sollen in einer Umzugsplattform-Datenbank gehalten werden. Der Einfachheit halber wird hierfür dieselbe Datenbank verwendet wie für die Process Engine.
    5. **Tasklist-Applikation**: Damit der Meldepflichtige seine Meldung erfassen kann, wird eine Client-Applikation benötigt, die im Browser ausgeführt werden kann. Hierfür wird die ebenfalls im Applikations- und Webserver Tomcat eingebettete Camunda Tasklist WebApp eingesetzt. In einer produktiven Lösung würde diese sicher separat deployed oder sogar durch eine spezifisch für den Kanton Bern entwickelte Tasklist-Applikation ersetzt, welche über REST mit der Process Engine kommuniziert.
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
    2. Aufgrund der **Komplexität** - es sollen nebst Mail-Benachrichtigung z.B. auch SMS-Benachrichtigung ermöglicht werden -, ist eine Implementation nur als JavaDelegate nicht sinnvoll.
    3. Auch wäre eine in die Umzugsplattform eingebettete Komponente wenig sinnvoll, weil wohl nicht bloss im "Umzug melden"-Prozess Mitteilungen zu versenden sind, sondern **auch in anderen Prozessen des Kantons Bern**.
    4. Umgekehrt ist der Benachrichtigungs**prozess trivial**, so dass eine eigene Process Engine nicht sinnvoll erscheint. Zumal ja schon im Hauptprozess nachvollziehbar persistiert wird, wer über was benachrichtigt wurde.
    5. Aus diesem Grund wird eine "einfache" **Spring Boot-Applikation ohne Camunda als Microservice** entwickelt.
    6. Die Kommunikation mit diesem Microservice geschieht analog zu den EKS über das **External Task Pattern**.
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
    1. Personenregister: ??????????????
    2. Gebäude- und Wohnungsregister: ??????????????

### VeKa-Center-Auskunftsdienst
1. Es gelten die Punkte 1 und 2 des vorherigen Kapitels.
2. Im Unterschied dazu erfolgt hier die Kommunikation mit der Aussenwelt über REST.
3. Die detaillierte Dokumentation ist zu finden in folgender **Github-Repository**: ??????????????

### Stripe-Online-Bezahldienst
1. Mit **Stripe** existiert ein Bezahldienst, der für Entwickler **zum Testen gratis** genutzt werden kann.
2. Entsprechend müssen wir hier nichts selbst implementieren, sondern können den **realen Bezahldienst** out-of-the-box nutzen.
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
2. Für unsere Demo-Zwecke mocken wir aber **nur den für die Umzugsplattform relevanten Teil**.
2. Der Microservice ist aufgrund seiner Komplexität ebenfalls **als Camunda Spring Boot-Applikation implementiert**, wird vom Hauptprozess jedoch nicht aufgerufen, sondern über das **External Task Pattern** eingebunden, wie weiter oben erläutert.
    4. Die Dokumentation des Microservices für die Umzugsplattform ist zu finden in folgendem **Github-Repository**: ??????????????

## Technische Komponenten und Funktionalitaeten der Umzugsplattform
1. Spring Boot 2.0.2 konfiguriert für Tomcat
2. Camunda Spring Boot Starter 3.0.0
3. Camunda Process Engine, REST API und Webapps (Tasklist, Cockpit, Admin) in der Version 7.9.2 (Enterprise Edition)
4. H2-Datenbank-Unterstützung (von Camunda Engine benötigt)
5. "Sinnvolle" Grundkonfiguration in application.properties für Camunda, Datenbank und Tomcat

## Erforderliche Schritte für das Testen der Applikation
### Voraussetzungen
1. **Laufende Umsysteme**: Die Maven-Projekte Personenregister, Gebäude- und Wohnungsregister, Einwohnerkontrollsysteme, EKS-Kommunikationsservice sowie VeKa-Center-Auskunftsdienst müssen gestartet sein. Details hierzu siehe in den ReadMe der verlinkten Github-Repositories.
2. **Camunda Enterprise**: Wenn man die Enterprise Edition von Camunda verwenden will, benötigt man die Zugangsdaten zum Nexus Repository und eine gültige Lizenz. Wie man diese "installiert", steht in den Kommentaren im pom.xml.
3. **Stripe-Konto und -Konfiguration**: Damit man den Stripe-Online-Bezahldienst nutzen kann, benötigt man einen Public Key und einen Secret Key. Beides erhält man nur, wenn man ein kostenloses Konto bei Stripe eröffnet. Details stehen etwa [hier](https://medium.com/oril/easy-way-to-integrate-payments-with-stripe-into-your-spring-boot-angular-application-c4d03c7fc6e) beschrieben. Im Wesentlichen sind die Schritte:
    1. Konto erstellen mit einer gültigen E-Mail-Adresse.
    2. Den Bestätigungslink in der erhaltenen Mail ausführen
    3. Sich anmelden bei https://dashboard.stripe.com/
    4. NICHT auf "Activate your account" klicken, solange man es nur zum Testen verwenden will.
    5. In der [API Keys-Seite](https://dashboard.stripe.com/account/apikeys) die beiden Schlüssel herauskopieren
    6. Den Publishable Key in /src/main/ressources/static.forms/ErfassenDerZahlungsdetailsForm.html im Code unter `var stripeCheckoutHandler = StripeCheckout.configure({` beim Parameter key einfügen
    7. Den Secret Key als Umgebungsvariable hinzufügen, wie dies in /src/main/ressources/application.properties beim Eintrag `stripe.apiKey` im Kommentar beschrieben ist.

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
        2. Für jede PLZ im Kanton Bern (z.B. 1797 Murten oder 3084 Köniz) sind zwei Adressen erfasst:
            1. Bahnhofstrasse 1
            2. Dorfstrasse 13a
    3. **E-Mail-Adresse bei Kontaktangaben**: Wenn man wirklich eine Mail erhalten möchte und mail.debug nicht auf true gesetzt ist, dann muss hier die eigene Mail-Adresse angegeben werden.
    4. **Zahlungsdetails**:
        1. Für das Testen von Stripe Checkout kann man eine beliebige CVC verwenden und je nach gewünschtem Testergebnis ein Datum in der Zukunft oder Vergangenheit.
        2. Je nachdem, welches Testergebnis man wünscht, gibt es die verschiedensten Kartnnummern, welche [hier](https://stripe.com/docs/testing#cards) aufgelistet sind. Die wohl am häufigsten benötigten sind:
            1. 4000007560000009: Gültige in der Schweiz ausgestellte Visa-Karte
            2. 5555555555554444: Gültige amerikanische Mastercard-Karte
            3. 4100000000000019: Aus Sicht des Kartenherausgebers gültige Karte, die aber von Stripe aufgrund Betrugsverdacht blockierte Karte
            4. 4000000000000002: Vom Kartenherausgeber abgelehnte Karte ohne Angabe von Gründen
    5. **Zahlungserfolg prüfen**: Um zu sehen, ob eine Zahlung auch "wirklich" durchgeführt wurde, bei Stripe anmelden und die [Events-Seite](https://dashboard.stripe.com/test/events) aufrufen.
    6. **Persistierung prüfen**: Um zu sehen, ob ein Status im Transaktionslog der Umzugsdatenbank persistiert wurde, einen SELECT auf die Tabelle TRANSACTIONLOG durchführen wie beschrieben in ???????
    7. **Prozessverlauf überwachen/nachvollziehen**: Um zu sehen, wo der Prozess momentan steckt (Runtime) oder wo abgeschlossene Instanzen durchliefen (History) kann die Cockpit-Webapplikation genutzt werden unter http://localhost:8081.

### Semi-Manuelles Testen

### Automatisiertes Testen


## Wie können Studierende Bonus-Punkte sammeln
1. **Zahlungsprozess fortgeschritten**: Statt bloss Kreditkarten soll auch SEPA unterstützt werden, was aber einen Rattenschwanz an Konsequenzen mit sich bringt aufgrund des asynchronen Zahlungsvorgangs:
    1. Der Zahlungsvorgang muss rausgelöst werden aus "Umzugsmeldung erfassen und bezahlen" und parallel stattfinden mit "Mit EK-Systemen kommunizieren".
    2. Es muss ein REST-Endpoint registriert werden, um die Stripe Webhook-Notifications empfangen zu können.
    3. Falls von Stripe eine Ablehnung kommt, dann muss der Meldepflichtige benachrichtigt werden und ihm erneut ein User Task für einen neuen Zahlungsversuch angezeigt werden.
    4. (Falls von den Einwohnergemeinden eine Ablehnung kommt, dann muss per Stripe eine Rückerstattung veranlasst werden)
2. **Persistierung fortgeschritten und WebApp für Mitarbeitende**: Wir könnten aber natürlich das auch etwas ausweiten, indem eine separate Applikation diese Persistierung vornimmt mit separater Datenbank (nicht Process Engine-DB) und dafür noch einem kleinen WebGUI, wo Angestellte der Verwaltung jederzeit für einen Meldepflichtigen seinen aktuellen Status anschauen können. Anderseits: Warum hierzu nicht einfach das Camunda Cockpit nutzen mit dem Filter nach einem bestimmten BusinessKey, welcher dem Benutzer auf dem "Abschlussbestätigung anzeigen"-Dialog angezeigt wird, damit er bei Telefonaten die richtige Id nennt?