# SENG302 Template Project Overview

Welcome to the template project for SENG302-2022, in this README file we've included some useful information to help you get started. We advise you to take some time reading through this entire document, as doing so may save you many headaches down the line!

## Dependencies
This project requires Java version >= 17, [click here to get the latest stable OpenJDK release (as of writing this README)](https://jdk.java.net/17/)

## Technologies

Across this project there are many technologies and dependencies in use, but here some helpful links for big ones:

- [Spring Boot](https://spring.io/projects/spring-boot) - Used in both the IdentityProvider and Portfolio modules
- [gRPC](https://grpc.io/docs/languages/java/quickstart/) - gRPC is the procedure we use to allow our modules to communicate with each other (e.g instead of REST)
- [Protobuf](https://developers.google.com/protocol-buffers/docs/javatutorial) - The protocol the gRPC uses for communication, this is also used for specifying contracts that different modules must comply with
- [Thymeleaf](https://www.thymeleaf.org/) - Templating engine used to render HTML for the browser, from the server (as opposed to having a separate client application such as a VueJS app)
- [Gradle](https://gradle.org/) - Gradle is a build automation tool that greatly simplifies getting applications up and running, it even manages our dependencies for us!

## Project structure

Inside this repository, you will see a number of directories, here's what each one is for:

- `systemd/` - This folder includes the systemd service files that will be present on your VM when you receive that (sprint 2), these are provided just for your reference, and can be safely ignored.
- `runner/` - These are the bash scripts used by the VM to execute your application. The `.gitlab-ci.yml` file is set up to copy these files to the VM when deploying, so you can keep your deployment scripts inside this code repo, rather than just saved on your VM. If none of the previous two sentences made any sense to you, don't worry - you'll find out more about that in sprint 2.
- `shared/` - Here we have a Java class library project - that is, not a project that is 'run' per se, but rather it contains (initially) some `.proto` contracts that are used to generate Java classes and stubs that the following modules will import and build on.
- `identityprovider/` - This is the first main code project in the repo. The Identity Provider (IdP) is built with Spring Boot, and uses gRPC to communicate with other modules. The IdP is where we will store user information (such as usernames, passwords, names, ids, etc.), and manage authentication. By having a separate IdP rather than, for example, building the authentication and user information into the Portfolio module, we are able to share this user information and authentication over multiple different software modules (i.e other applications within the LENS ecosystem). At the moment there are only two modules, the IdP and the Portfolio, so it may seem a little unnecessary to separate them out, but when you begin adding more modules, it will make a lot more sense. The IdP does not have any form of user interface, and at this stage should be kept as such. You can find more info about the importance of the IdP [below](#the-lens-authentication-dance)
- `portfolio/` - Following on from the IdP, the Portfolio module is another fully fledged Java application running Spring Boot. It also uses gRPC to communicate with other modules, and is initially configured to be able to log in and check authentication with the IdP (albeit with just a dummy user account at the moment, you'll be implementing some real user functionality). Because we've already implemented much of the background authentication configuration (in the way the LENS expects), **you should think twice before modifying anything in the `Authentication`  package** - more on this [below](#the-lens-authentication-dance). The Portfolio module uses Thymeleaf for server-side rendering of HTML.

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


# The LENS Authentication Dance
Given that the applications you are building this year will (hopefully!) one day be integrated into the wider LENS ecosystem, it is important that they perform authentication in the way that LENS modules are expected to. LENS expects modules to conform to its SSO (Single Sign On) authentication scheme. So to save you the trouble of guessing exactly which way you need to configure your authentication (because there are many different options), we've done a lot of the set up for you already. 

___While it's important for you all to understand what authentication steps are happening in your application - if the following explanation is a bit daunting, don't stress, it'll make more sense as time goes on and you develop further on the app.___

## The IdentityProvider (IdP)
In order for multiple software modules to share the same user accounts and authentication information, there must be a single source of truth somewhere that they all rely on - the IdP is this source of truth. In the case of your applications, users must first register an account and log in with the IdP before they may proceed - in this way, we are creating from scratch a 'new identity' for each user of the application. 

But what about if we wanted to be able to use an already existing identity source instead of creating them all from scratch - for example, being able to sign in using our existing UC credentials. If we did this, we could also get additional information about users like what courses they are enrolled in, whether they are students or staff, and much more. In fact, this is how the *'official'* LENS IdP works! That's right, somewhere out there is a LENS IdP that already exists - **this is why it is important make sure you correctly implement using the provided `.proto` contracts**. When we integrate your products into the official LENS instance, they'll be using our Identity Provider!

As the IdP is the single source of truth for our identity, we also have it be the single source of truth for our authentication. In this way, it is the IdP the generates and signs the session tokens we give to users when they log in - and it is only the IdP that can validate these session tokens to ensure they have not been tampered with or expired. This is why modules like `portfolio` send a `checkAuthState()` request to the IdP whenever a user attempts to do something that requires authentication.

## Logging in
In order to become authenticated with the system, a user must log in. Currently, there is a single 'user' hardcoded into the identity provider that we can log in as. For the teams that are implementing the user stories related to registering and logging in, this will clearly need to be changed - for the other teams, keeping this hardcoded user should be fine. The process of logging in is as follows:

1. Firstly, both `identityprovider` and `portfolio` modules must be running as per [this section](#building-and-running-the-project-with-gradle)
2. A user navigates to the `/login` page - (probably at `http://localhost:9000/login`)
3. The browser sends a `GET /login` request which Spring forwards to the `LoginController.login()` endpoint
4. The `GET /login` endpoint has some default credentials set as parameters, so the controller immediately sends a message to the IdP using the Portfolio's `AuthenticateClientService.authenticate(username, password)` method. **If you are one of the teams implementing the logging in story, you will need to change how credentials are supplied to the endpoint**.
5. The `AuthenticateClientService` request is received by the IdP's `AuthenticateServerService`
and the `AuthenticateServerService.authenticate()` method is run.
6. If the username and password match the hardcoded user, we create a JWT session token for that user and return it (along with some other useful information about the log in attempt). If the username and password did not match, we return information saying the log in attempt failed. **Teams implementing the registering and logging in stories will need to extend this to work for registered users, not a hardcoded user.**
7. The IdP's response is received by the Portfolio's `AuthenticateClientService.Authenticate(username, password)` method, and returned to the `LoginController`.
8. If the log in attempt was successful, the `LoginController` creates a new Cookie with the session token given by IdP, and responds to the browser's `GET /login` request (step 3) with a success message and the Cookie. If the log in attempt failed, no Cookie is created, and a response with error message is sent instead.
9. Along with the Cookie (if log in succeeded), the Thymeleaf `login.html` template is rendered using the success message (or error message), and shown to the user. The session token Cookie is stored by the browser, and automatically included in future requests to the application.

## Checking authentication
Once the user has logged in, they will have a Cookie in their browser that they can use to prove their identity and authentication to the system. To make sure that the session token is valid, has not been tampered with, and has not expired, the Portfolio module (and any other module) must send it to the IdP so that it can check these things and tell the Portfolio module whether the user is authenticated. This process happens as follows:

1. The user navigates to the `/greeting` page on the Portfolio (which requires authentication). *By default, all endpoints except those explicitly permitted to allow unauthenticated users in the Portfolio's `SecurityConfig` - such as the `/login` endpoint - will require authentication. **Any authenticated endpoint will automatically trigger the following steps to occur, these steps are described here for you information.***
2. The browser sends a `GET /greeting` request which Spring forwards to the `GreeterController.greeting()` endpoint. If user has logged in to the application and been given a Cookie with their session token, this Cookie will be included in the `GET /greeting` request.
3. Because this endpoint is configured by Spring as requiring authentication, and in Portfolio's `SecurityConfig` class we configure Spring Security to use our custom `JWTAuthFilter` class to check if a user is authenticated, the `JWTAuthFilter.doFilterInternal()` method is called.
4. `doFilterInternal()` then calls `JWTAuthFilter.getAuthentication()` - this method in turn checks to see if a session token Cookie is present. If the Cookie is not present, clearly the user is not authenticated, so we return an object stating the user is not authenticated to `doFilterInternal()` which will inform Spring's `SecurityContextHolder` that they are not authenticated, which will in turn block the user from accessing the `/greeting` endpoint. If however a Cookie is present, then we call the `AuthenticateClientService.checkAuthState()` method to check with the IdP if the user has valid authentication.
5. The `AuthenticateClientService.checkAuthState()` method fires an empty gRPC request off to the IdP, but before the request is sent, our `AuthenticationClientInterceptor` grabs the request and adds in the session token as metadata. *Note: The `AuthenticationClientInterceptor` automatically adds the session token as metadata to all gRPC requests we send, so that we don't have to manually include it everytime we send a request!*
6. When the IdP receives the gRPC request, the `AuthenticationServerInterceptor` class pulls the session token out of the request, and calls the `AuthenticationValidatorUtil.validateTokenForAuthState(sessionToken)` method to get the user's authentication state from this token.
7. The `AuthenticationServerInterceptor` then adds the validated authentication state to a context which all gRPC services can access, and allows the message to continue on to its final destination: the `AuthenticateServerService`.
8. The `AuthenticateServerService` then simply pulls out the authentication state which was just added to the current context, and returns it back to the Portfolio.
9. The authentication state is received back at the Portfolio's `JWTAuthFilter.getAuthentication()` method it was originally called from (step 4). We then pass the information about whether or not the IdP says the user is authenticated back to `doFilterInternal()`, which in turns informs Spring's `SecurityContextHolder`. Here, as in step 4, if the IdP says the user is not properly authenticated, they are blocked from accessing the `/greeting` endpoint. But if the IdP says they are authenticated, then finally the `GreeterController.greeting()` is run, returning its result back to the user.
10. Because we have added the authentication state information to Spring's `SecurityContextHolder`, it means we can painlessly access this authentication information in our endpoints by simply adding a parameter annotated with the `@SecurityPrincipal` annotation. (See `GreeterController.greeting()`).

# Frequently encountered questions / issues
## Q: *"Where does **\<some-software-entity>** go? Does it belong in the IdP? The Portolio? It's so confusing!"*
One of the drawbacks to a multi-part system like LENS, is that it can sometimes be unclear where certain entities should reside. To keep the 302 project as simple as possible, we'll try to make it clear here. 

### What does belong in the IdP
The IdP is responsible for knowing what user accounts exist, how the can log in, what their role is (e.g User, Admin, ...), and managing authentication. As the IdP is shared by all modules, it should only contain the information about a user that is needed by *all* software modules. 

Now, there are a few pieces of information about a user that all (or most) modules probably need to know, and modules will probably want to keep a local copy of some of this information within their own data storage, to prevent the need to constantly query the IdP for something like the currently logged in user's name.

Examples of common data that all modules should have:
* The user account's ID
    * The IdP assigns each user account a unique ID - in your case this is done arbitrarily, but consider that this ID could also be a 'business key' like your UC student ID number
    * Other modules should avoid generating their own unique IDs for user accounts, and should instead use the ID provided by the IdP to ensure they all keep the same source of truth
    * **You can assume this ID will never change**
* The user account's username
    * Similarly to account IDs, in the 302 project we will allow the user to choose this themselves, but consider how this replaced by something like a UC usercode (abc123)
    * **You can assume this username will never change**
* The name of the user
    * Unlike the previous two fields, we must be prepared for the name of a user to change. So if you are keeping a local 'copy' of a user account in your non-IdP modules, which you probably will, make sure that at some point (e.g when they log in) you are checking if this field needs to be updated on your local copy.
* The 'global' role(s) of the user within LENS as a whole
    * E.g Student, teacher, course administrator
    * This is another field that may change at any time, but because this is encoded in the authentication checks, you can access this on demand and shouldn't store it locally
* The user's email address
    * Expect that this may change
* The nickname / alias of a user
    * Expect that this may change
* Personal pronouns for the user
    * Expect that these may change
* A user's bio
    * Expect that this may change
* A profile photo of the user
    * Expect that this may change

**As the above traits are needed by all (or most) modules, they belong in the IdP, the central source of truth.**

### What does not belong in the IdP
Each LENS module will probably want to store some additional information about users that is specific to their module, these should not be added to what data the IdP stores about a user account, but rather kept contained within the module's own data storage.

Such examples would include:
* Portfolio projects
    * This is something that will probably only be used by the portfolio, so doesn't need to be included in the IdP
    * In the case that there was another module that wanted to access information about portfolio projects, then we would define a (gRPC) contract that the portfolio module would implement that other modules may use to communicate directly with the portfolio module.
* Teams within a module
    * In cases where teams are specific to a particular module, that module should manage the grouping of users itself
    * There are cases where a team would be the same across multiple modules. For example, your SENG302 team would be the same team modules (if they existed) like: a ScrumBoard module, a Peer Feedback module, a Code Analysis module, etc. While it may seem like these teams should be managed at the IdP level (and one day they may well be), this is outside the scope of what you're working on in the SENG302 project, so just keep each module managing their own teams.
* Gamification achievements, badges, etc.


