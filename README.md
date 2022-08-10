# SENG302 - Team 400 Bad Request - LenPortfolio

Welcome to team 400's project for Seng302. This read me contains an overview of the 'Lensfolio' applicaiton. 


## Dependencies
This project requires Java version >= 17, [click here to get the latest stable OpenJDK release (as of writing this README)](https://jdk.java.net/17/)


## Technologies

Across this project there are many technologies and dependencies in use, but here some helpful links for big ones:

- [Spring Boot](https://spring.io/projects/spring-boot) - Used in both the IdentityProvider and Portfolio modules
- [gRPC](https://grpc.io/docs/languages/java/quickstart/) - gRPC is the procedure we use to allow our modules to communicate with each other (e.g instead of REST)
- [Protobuf](https://developers.google.com/protocol-buffers/docs/javatutorial) - The protocol the gRPC uses for communication, this is also used for specifying contracts that different modules must comply with
- [Thymeleaf](https://www.thymeleaf.org/) - Templating engine used to render HTML for the browser, from the server (as opposed to having a separate client application such as a VueJS app)
- [Gradle](https://gradle.org/) - Gradle is a build automation tool that greatly simplifies getting applications up and running, it even manages our dependencies for us!
- [Full Calendar](https://fullcalendar.io/) - Used in portfolio module for creating monthly planner.
- [GitLab4J](https://github.com/gitlab4j/gitlab4j-api) - Used in group settings page for repository access.


## Project structure

Inside this repository, you will see a number of directories, here's what each one is for:

- `systemd/` - This folder includes the systemd service files that are present on our VM
- `runner/` - These are the bash scripts used by the VM to execute the application. The `.gitlab-ci.yml` file is set up to copy these files to the VM when deploying, so they can be kept inside this code repo, rather than just saved on the VM. 
- `shared/` - Here we have a Java class library project - that is, not a project that is 'run' per se, but rather it contains some `.proto` contracts that are used to generate Java classes and stubs that the following modules will import and build on.
- `identityprovider/` - The Identity Provider (IdP) is built with Spring Boot, and uses gRPC to communicate with other modules. The IdP is where we store user information (such as usernames, passwords, names, ids, etc.), and manage authentication. By having a separate IdP rather than, for example, building the authentication and user information into the Portfolio module, we are able to share this user information and authentication over multiple different software modules (i.e other applications within the LENS ecosystem).
- `portfolio/` - The Portfolio module is another fully fledged Java application running Spring Boot. It also uses gRPC to communicate with other modules, and enables the main user functionality for the system. The Portfolio module uses Thymeleaf for server-side rendering of HTML.


# Quickstart guide

## Building and running the project with gradle
We'll give some steps here for building and running via the commandline, though IDEs such as IntelliJ will typically have a 'gradle' tab somewhere that you can use to perform the same actions with as well.


### 1 - Generating Java dependencies from the `shared` class library
The `shared` class library is a dependency of the two main applications, so before you will be able to build either `portfolio` or `identityprovider`, you must make sure the shared library files are available via the local maven repository.

Assuming we start in the project root, the steps are as follows...

On Linux:
```
cd shared
./gradlew clean
./gradlew publishToMavenLocal
```

On Windows:
```
cd shared
gradlew clean
gradlew publishToMavenLocal
```

*Note: The `gradle clean` step is usually only necessary if there have been changes since the last publishToMavenLocal.*


### 2 - Running the IdentityProvider module
In order to be able to log in through the Portfolio module, and access its protected routes, the IdP must first be up and running - check the `identityprovider\src\main\resources` and `portfolio\src\main\resources\application.properties` files to see how these two different modules know where to find each other.

Again, assuming we are starting in the root directory...

On Linux:
```
cd identityprovider
./gradlew bootRun
```

On Windows:
```
cd identityprovider
gradlew bootRun
```

Unlike in step 1, when you run this command, it won't 'finish'. This is because the shell (e.g windows / linux terminal) is kept busy by the process until it ends (Ctrl+C) to kill it. By default, the IdP will run on local port 9002 (`http://localhost:9002`).


### 3 - Running the Portfolio module
Now that the IdP is up and running, we will be able to use the Portfolio module (note: it is entirely possible to start it up without the IdP running, you just won't be able to get very far).

From the root directory (and likely in a second terminal tab / window)...
On Linux:
```
cd portfolio
./gradlew bootRun
```

On Windows:
```
cd portfolio
gradlew bootRun
```

By default, the Portfolio will run on local port 9000 (`http://localhost:9000`)


### 4 - Connect to the Portfolio UI through your web brower
Everything should now be up and running, so you can load up your preferred web browser and connect to the Portfolio UI by going to `http://localhost:9000` - though you will probably want to start at `http://localhost:9000/login` until you set up an automatic redirect, or a home page of sorts.


## User Manual
[User manual](https://eng-git.canterbury.ac.nz/seng302-2022/team-400/-/wikis/User-Manual) - Link to user manual within GitLab wiki.


## License

Copyright 2022 Team-400, 400 Bad Request

Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.

## Contributors

- SENG302 teaching team
- Amy Sloane
- Bede Skinner-Vennell
- Billy Sandri
- Connor Dunlop
- Danish Khursheed Jahangir
- Luke Garside
- Sam McMillan


## References
- [GitLab Wiki](https://eng-git.canterbury.ac.nz/seng302-2022/team-400/-/wikis/home)
- [Spring Boot Docs](https://docs.spring.io/spring-boot/docs/current/reference/htmlsingle/)
- [Spring JPA Docs](https://docs.spring.io/spring-data/jpa/docs/current/reference/html/)
- [Thymeleaf Docs](https://www.thymeleaf.org/documentation.html)
- [Full Calendar Docs](https://fullcalendar.io/docs)
- [Learn resources](https://learn.canterbury.ac.nz/course/view.php?id=13269&section=9)
