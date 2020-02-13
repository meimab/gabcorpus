# ba-service-backend
[![Codacy Badge](https://api.codacy.com/project/badge/Grade/cdd08acab3044766b0d89200af331169)](https://www.codacy.com/app/6hauptvo/ba-service-backend?utm_source=github.com&amp;utm_medium=referral&amp;utm_content=6hauptvo/ba-service-backend&amp;utm_campaign=Badge_Grade)

This project contains a web service based on the Spring Boot framework.
It features a RESTful endpoint that can retrieve word expansions.

- Check out and run the ApplicationController
- default port is 8080
- for deployment, port can be edited in /resources/application.properties
- check with "curl localhost:8080/expansions?word=IBM&format=json"
- for textual output: "curl localhost:8080/expansions?word=IBM&format=text"

