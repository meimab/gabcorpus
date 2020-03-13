# gab.ai Sentiment Analysis

Dieses Projekt hat das Ziel, den Gab-Corpus von über 34 Millionen Posts mithilfe von Watson NLU nach Sentiment und Emotionen zu analysieren. Außerdem soll die hierfür eingerichtete und verwendete Datenbank als Grundlage für weitere Analysen in der Zukunft dienen. Im Folgenden werden die Klassen des Projekts erklärt:

### MySQLconnect
Ist dazu in der Lage, eine Verbindung zur Datenbank aufzubauen, um neue Daten einzuspeichern sowie Existierende Daten zu aktualisieren und auszulesen.

### JsonImport
Liest JSON-Dateien Zeile für Zeile aus, um sie zusammen mit MySQLconnect in die Datenbank zu importieren.

### Watson
Verbindet sich mit dem Watson NLU-Service und lässt Text auf Daten zu Sentiment und Emotionen analysieren. Es stehen 30.000 Watson-NLU-Items pro Monat zur Verfügung und die Verbindung bleibt bestehen, solange sie mindestens einmal im Monat verwendet wird.
### Verarbeitung
Benutzt Methoden aus MySQLconnect und Watson, um Daten aus der Datenbank auszulesen, analysieren zu lassen und die neuen jeweiligen Zeilen um die neuen Daten zu erweitern. 
# Wie wird dieses Projekt benutzt?
Die meisten Methoden sind so geschrieben, dass sie problemlos auf weitere Datensätze anwendbar sind sowie die Ergebnisse in einer Vielzahl von Anwendungsfällen eingesetzt werden können. MySQLconnect.getText liefert eine HashMap mit Post-ID und Text, MySQLconnect.getWatson gibt ein double Array aus und bei MySQLconnect.getExamplePosts wird ein ResultSet übergeben. Für das Frontend erstellt der ApplicationController daraus Models, die an die HTML-Templates weitergegeben werden. Watson.klassifizierer gibt ein AnalysisResult aus, welches direkt in Verarbeitung.classify verarbeitet wird.

Die Benutzung vom Projekt ist jedoch von der Datenbank abhängig und bei einer Weiterverwendung müssten ggfs. Parameter verändert werden.

Die Datenbank-Tabelle posts wurde folgendermaßen erstellt:
```
create table posts (
post_id int unsigned primary key,
text text,
date datetime,
likes smallint unsigned,
dislikes smallint unsigned,
gab_score smallint signed,
nsfw tinyint(1),
userid int,
watson_sentiment varchar(10),
watson_sentiment_score double(7,6),
watson_sadness double(7,6),
watson_joy double(7,6),
watson_fear double(7,6),
watson_disgust double(7,6),
watson_anger double(7,6)
);
```
Fast jede Methode greift in einer oder anderen Art auf diese Datenbank zu. Die Verbindung wird dabei (gleiches gilt für Watson) hergestellt, indem Daten wie Username und Passwort aus der credentials.txt ausgelesen werden.

Um das Projekt mit anderen Daten verwenden zu können, müssen die Werte im Code also gemäß den Vorstellungen angepasst werden. Falls beispielsweise eine neue Spalte hinzugefügt wird, muss nicht nur JsonImport.importCorpus, sondern jede Methode, für die die neuen Daten relevant sind, angepasst werden.
