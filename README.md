# PG6100 - Eksamen

Master | Develop | Coverage | License
------ | ------- | -------- | -------
[![Build Status](https://travis-ci.com/kissorjeyabalan/pg6100-eksamen.svg?token=USPSPUpuhkXXxhxgdmM4&branch=master)](https://travis-ci.com/kissorjeyabalan/pg6100-eksamen) | [![Build Status](https://travis-ci.com/kissorjeyabalan/pg6100-eksamen.svg?token=USPSPUpuhkXXxhxgdmM4&branch=develop)](https://travis-ci.com/kissorjeyabalan/pg6100-eksamen) | [![codecov](https://codecov.io/gh/kissorjeyabalan/pg6100-eksamen/branch/develop/graph/badge.svg?token=HInyhbQ9f1)](https://codecov.io/gh/kissorjeyabalan/pg6100-eksamen) | [![MIT Licensed](https://img.shields.io/badge/license-MIT-blue.svg)](LICENSE)

Link to repo: https://github.com/kissorjeyabalan/pg6100-eksamen

## KNOWN ISSUE
The application as a whole doesn't work, due CORS, spring-security-gateway and spring-security not working well together. We assume this is some kind of bug that is on spring side, and have exhausted all options of trying to resolve this issue in time.

Therefore, unless web security is disabled - it is not possible to do any POST requests on the website. This makes the frontend pretty useless, however, everything is working -just fine- on the backend side of things (and when calling everything through postman and restassured (in i.e. e2e tests))

This is unfortunate for us, but it is what it is. LocalApplicationRunner still mocks all dependencies, so the API can be tested there, or by running it all in docker and calling the following endpoints in postman:
- localhost/api/v1/auth/
- localhost/api/v1/movies/
- localhost/api/v1/kino/shows/
- localhost/api/v1/kino/theaters/
- localhost/api/v1/kino/theaters/
- localhost/api/v1/kino/users/
- localhost/api/v1/kino/tickets/

First a login post must be done to auth/login, to acquire session cookie - then the session can be used with all other endpoints.
Again, everything works on the backend, it's just an unfortunate situation with spring-gateway-cloud at its current state.

## Starting the application
Prerequisites to start the application is docker and docker-compose.
To start the application, navigate to the root directory and run:
```mvn install -DskipTests && docker-compose up```. This will skip the tests and start the application.
Frontend is available at `localhost:80`. It is recommended to give docker more than 2gb memory, as it might take a while to start all the services otherwise.

Admin user with username `admin` and password `admin` has been added to the authentication system, and can be used to access administrative tools in the frontend
and backend.

## Tests
Tests can be ran with the command `mvn test` in the root directory.

## Swagger
Swagger is not accessibly by default, due to the gateway being in the way.
Each application with the suffix `-service` has a `LocalApplicationRunner` which can be used to start a local instance of the application on port 8080. All dependencies have been mocked away, so services communicating with others can not be tested with swagger - but the swagger documentation is still available.

Swagger is accessible at `localhost:8080/swagger-ui.html` when running the `LocalApplicationRunner`.
Username and password to access swagger is `admin` and `admin`.


##  About the project

### Contributions:
**Andreas Ritter:**

- Git username: andreasholteritter

The main rest service Andreas was responsible for is kino-service (which is our largest api). 
He also wrote the end to end tests for the project.
As for the shared code, he has worked on the frontend.


**Christian Thorby:**
- Git username:  ChristianThorby

The main rest service Christian was resposible for is ticket-service.
Major parts of the frontend was contributed by Christian, as well as the flyway and database schemas for:
- movie-service
- kino-service


**Kissor Jeyabalan:**:
- Git username: kissorjeyabalan


The main rest service Kissor was responsible for is user-service.
He mostly spent time on setting up spring security, as well as the gateway and connecting these together with eureka.
He made the flyway migrations for auth-service, as well as setting up the LocalApplicationRunner for most of the modules.


---
The rest of the code was written by us together, and we mostly pair-programmed those.
We ended up not making any extra endpoints for the shared modules, other than the minimum ones for he individual modules.


### Choices
The project didn't exactly end up where we wanted it to, as it got a bit chaotic around the start. We made some poor design choices, which we have tried our best to alleviate.

The way we have set it up, we have tried to split the important parts into their own module.
Due to the way this is set up, there is minimal talk between the modules and they are mostly linked through the frontend.

Due to this limitation, there isn't really any verification on foreign keys - but it's a choice we have made, and we've stuck with it.

The booking service is the only service talking to others, as it has to coordinate purchases.
When a user clicks on a seat, a POST is sent to reserve the seat, so it isn't available to others. Due to some odd choices, we had to end up pessimistically lock the show database on read (to avoid two people doing the same change at the same time).

Here we have just hardcoded a price inside the booking api, but ideally, we would query an external database to get the ticket prices. We have simulated the fact of receiving a payment token from the frontend, which the backend can then verify before actually doing a POST for an order.

We have permitAll() access for creating and deleting ticket, and removing and adding show seats. This is a huge -no- in production, and this is something we have to absolutely fix. We had to do it this way, as we didn't have enough time to figure out how to make the RestTemplate work as intended.
We could for the life of us not manage to forward the session, so the user always ended up unauthenticated and hitting a 401, since the RestTemplate was not authenticated when talking to the other systems.

Other then that, the API's work as intended between each other.

We have tried to stick to what Spring Boot has to offer us, and don't really have any other external dependencies.
We decided to use react as frontend, so we have a build job in maven for compiling that. 
We are using Thymeleaf to serve the react page, as it felt fitting to use it since the backend is running on Spring.

As for flyway and persistent database such as postgres, we have added it to auth-service, movie-service and kino-service -- as
these are the ones containing some default data that are a pain to add each time.

As the system is set up now, it consists of one cinema, several theaters in such cinema and many shows in a theater. It works, but isn't very scalable. Ideally, we would also like to create an cinema level, so it can support several cinemas.
That shouldn't be too hard to add support for, when required.

As for optimizations, we haven't spent time and resources on this (as we haven't hit any walls, requiring this yet). 

---

## Features
- Log in
- Sign up
- Log out
- List of featured movies on homepage
- Individual movie page with details, with screenings for that movie
- Reservation of seats: Used to reserve a seat
- Placing an order: Buying tickets
- Look at tickets you have bought
- Admin: Create new screenings and edit existing ones
- Admin: Add new theater



## Sources
- Most code that is copied is from
https://github.com/arcuri82/testing_security_development_enterprise_systems

- Cors issue with spring-cloud-gateway: https://github.com/spring-cloud/spring-cloud-gateway/issues/229
- Spring security: https://www.baeldung.com/spring-security-basic-authentication
- Thymeleaf: https://spring.io/guides/tutorials/react-and-spring-data-rest/
- Flyway: https://flywaydb.org/documentation/
- Load balancing: https://spring.io/guides/gs/client-side-load-balancing/
- Optimization: https://openliberty.io/blog/2018/06/29/optimizing-spring-boot-apps-for-docker.html
- Antmatchers: https://stackoverflow.com/questions/12569308/spring-difference-of-and-with-regards-to-paths
- Status codes: https://developer.mozilla.org/en-US/docs/Web/HTTP/Status/
- Debugging h2 issues: https://stackoverflow.com/questions/1710476/how-to-print-a-query-string-with-parameter-values-when-using-hibernate
- Ribbon: https://o7planning.org/en/11739/undertanding-load-balancing-in-spring-cloud-with-ribbon-and-example
- Ribbon: http://javaonfly.blogspot.com/2017/08/microservices-tutorial-ribbon-as-load.html
- Available repository methods: https://docs.spring.io/spring-data/data-commons/docs/1.6.1.RELEASE/reference/html/repositories.html
- XMLHttpRequest - bypass www-authenticate: https://developer.mozilla.org/en-US/docs/Web/API/XMLHttpRequest/withCredentials
