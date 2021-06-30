# jard

![jard](https://raw.githubusercontent.com/njuro/jard-client/master/public/assets/jard-logo-name.png)

[![Build Status](https://travis-ci.org/njuro/jard.svg?branch=master)](https://travis-ci.org/njuro/jard)
[![Sonarcloud Status](https://sonarcloud.io/api/project_badges/measure?project=njuro_jard&metric=alert_status)](https://sonarcloud.io/dashboard?id=njuro_jard)
[![Sonarcloud Coverage](https://sonarcloud.io/api/project_badges/measure?project=njuro_jard&metric=coverage)](https://sonarcloud.io/dashboard?id=njuro_jard)

## Basic FAQ

### What is jard?

**jard** (pronounced like *yard*)  is a software for running an anonymous imageboard. Citing from Wikipedia:
> An imageboard is a type of Internet forum that revolves around the posting
> of images, often alongside related text and discussion.

Most notable examples of imageboards include 2channel, 4chan, 8chan, Krautchan etc.

This repository contains backend exposing REST API, which is consumed by frontend client written in React (
see [jard-client](https://github.com/njuro/jard-client)).

### Is jard based on existing imageboard software?

Code-wise is jard written "from scratch" and is not a fork of any existing imageboard. Design- and feature-wise it is
heavily influenced by aforementioned imageboards.

### Why make another imageboard software?

**Short answer:** I don't know, why not?

**Long answer:** I identified 3 main motivation factors behind the creation of jard:

1. Many of the software behind popular imageboards is either closed-source (4chan) and/or aged and no longer actively
   developed or maintained (example of this is vichan - fork of Tinyboard - software which powered 8chan). Also from the
   rumors and [code leaks](https://gist.github.com/dvliman/11264471), they are often undocumented monolithic PHP
   applications hacked together throughout the years with little attention to code quality, testability and
   maintainability. While I don't want to bash them (they are time-tested and work +- reliably most of the time), my
   vision was to create open-source, documented software which can be easily customized and extended by almost anyone
   willing to put effort into it. There is a long way to go to achieve this goal, but you need to start somewhere. Note:
   I am also aware that similiar attempts to produce "modern" imageboard software exist, however from my short research
   most of them are merely proofs of concept and lack many important functionality and polishing.
2. Similarly, I found that given its enterprise nature not many medium-size real-world open-source applications written
   in Spring Boot (framework jard's backend is written in) exist. There are many tutorial level examples on GitHub,
   demonstrating different aspects of Spring, but I am not aware of many up-to-date Spring apps the
   beginner/intermediate developer could easily contribute to (except the framework itself).
3. I wanted to try and learn new concepts, technologies, approaches and try to create and maintain my very own project
   bigger than regular TicTacToe / FizzBuzz app. This is actually the biggest motivation factor of them all and the only
   one I am sure is true, because it already gave me much.

### What technologies is jard built with?

The backend is written in `Java 11`, with `Spring Boot 2.x` framework (powered by `Spring 5.x`), using `PostgreSQL` as
relational database and `Hibernate` as ORM framework. Files uploaded by users are stored in `Amazon S3` bucket.
Authentication is handled with JWT cookies (using `jjwt` library). Backend exposes REST API with protected endpoints.
Full-text search is provided by `Hibernate Search` which makes use of `Apache Lucene`. CAPTCHA protection by `hCaptcha`.
Several other libraries are used for different functionality, as described in *pom.xml* file.

Tests are written in `Kotlin` with `JUnit 5` framework using `Kotest` matchers and `Mockk`, `Mockwebserver`, `Greenmail`
libraries for mocking and stubbing various components. Test PostgreSQL database is running as Docker container (
provided by `Testcontainers`).

`Circle CI` is used for building and testing pipelines and master branch is regularly deployed to `Heroku`. For frontend
technologies see [jard-client](https://github.com/njuro/jard-client).

## Features

### Implemented

From **user** perspective:

- [x] Viewing threads either as paginated list, or as catalog
- [x] Sorting / Filtering threads in catalog
- [x] Posting new threads to boards
- [x] Replying to existing threads
- [x] Uploading attachments
    - [x] image/video/pdf files with generated thumbnails
    - [x] audio/txt/word files with placeholder thumbnails (based on file type)
    - [x] metadata available (duration, resolution, file size, checksum etc.)
- [x] Embedding content from providers with [oEmbed](https://oembed.com) endpoints:
    - [x] YouTube, Twitter, SoundCloud, CodePen, CodeSandbox, Vimeo, Scribd, TikTok, Spotify...
- [x] Using [SAGE](https://knowyourmeme.com/memes/sage) when posting
- [x] Using secure [tripcode](https://en.wiktionary.org/wiki/tripcode) when posting
- [x] Using [code] tags for block of formatted code
- [x] Using [spoiler] tags for - duh - spoilers, revealed on mouse hover
- [x] Using greentext (>)
- [x] Crosslinking to different posts/threads/boards (e.g. >>>/g/123#4)
- [x] Quoting post by selecting content and clicking on its number
- [x] Highlighting own posts and replies to them (`(You)`)
- [x] Deleting own post within time limit
- [x] Both manual and auto updating of opened threads
- [x] (Optional) posts with country flag based on poster IP
- [x] (Optional) unique poster ID per thread per IP
- [x] (Optional) required CAPTCHA before posting

From **admin** perspective:

- [x] Creating / editing / deleting boards
    - [x] Specifying NSFW status, thread and bump limits, default poster name etc.
- [x] Creating / editing / deleting users
    - [x]  Assigning predefined roles (janitor/moderator/admin) each with set of permissions
- [x] Banning IP addresses from posting temporarily or permanently, editing of bans / unbanning
- [x] Locking of threads
- [x] Stickying of threads
- [x] Deleting of posts and threads
- [x] Fulltext search across all posts

### Planned

- [ ] Multiple attachments per post
- [ ]  Allow to define user roles with custom set of permissions
- [ ]  Warning instead of ban
- [ ]  Post report system
- [ ] Password reset system
- [ ] Custom user themes
- [ ] User-managed boards
- [ ] Real-time posting
- [ ] Many more...

## Deploying your own instance

jard aims to allow anyone to quickly and painlessly set up and manage their very own anonymous imageboard. Currently the
following ways of deploying are supported:

### Heroku

Probably the most convenient way is to deploy straight to Heroku (PaaS) with this button:

[![Deploy](https://www.herokucdn.com/deploy/button.svg)](https://heroku.com/deploy?template=https://github.com/njuro/jard)

**Deployment notes for Heroku**

- Heroku has *[ephemeral filesystem](https://help.heroku.com/K1PPS2WM/why-are-my-file-uploads-missing-deleted)*, meaning
  that altough it is possible to set `USER_CONTENT_STORAGE` variable to `LOCAL`, the uploaded attachments will disappear
  after a while, so recommended value for production is `AMAZON_S3` and providing credentials to S3 buckets.
- Due to `*.herokuapp.com` being listed
  in *[public suffix list](https://devcenter.heroku.com/articles/cookies-and-herokuapp-com)*, in order to share
  cookies (used for authentication etc.) between jard server and client, the CSRF protection must be disabled via
  enviroment variable `DISABLE_CSRF_PROTECTION=true`. This causes two thing:
    - CSRF protection via `XSRF-TOKEN` cookie is disabled
    - Cookies set by server have `SameSite` attribute set to `None` instead of  `Strict`
- It is not needed to disable CSRF protection as described above, if you plan to use custom domain for your server
  instance.
- By default deploying via the button will provide you with free (as in free beer) instance of Heroku dyno, meaning that
  it will hibernate after 30 minutes of inactivity. This can be upgraded in Heroku panel at any time.

### Docker  (client, server, database)

If you prefer to deploy locally on your own computer/VPS, you can use `docker-compose` to deploy the whole jard stack at
once.

**Steps**

1. Download / copy the [docker-compose.yml](https://github.com/njuro/jard/blob/master/docker-compose.yml) file.
2. Download / copy the [.env-template](https://github.com/njuro/jard/blob/master/.env-template) file, rename it
   to `.env` and store it in the same directory as docker-compose file.
3. Fill in the enviroment variables in `.env` file - be careful to use `DOCKER_COMPOSE_DATABASE_*` variables, instead
   of `JDBC_DATABASE_*` variables. There is also no need to set `CLIENT_BASE_URL`or `SERVER_BASE_URL`.
4. Run commands described in [docker-compose_run.sh](https://github.com/njuro/jard/blob/master/docker-compose_run.sh)
   while being in the same directory.
5. Wait ~2 minutes, afterwards you should have jard server running at port `8081`, jard client running at port `3000`
   and PostgresSQL database at port `5433` (all of the ports can be changed in docker compose file)

### Docker (server only)

Alternatively, you could deploy just the server container. This requires to have set up PostgresSQL database and linking
it via enviroment variables.

1. Download / copy the [.env-template](https://github.com/njuro/jard/blob/master/.env-template) file, rename it
   to `.env` and fill in the enviroment variables.
2. Run commands described in [docker_run.sh](https://github.com/njuro/jard/blob/master/docker_run.sh)
3. Wait ~2 minutes, afterwards you should have jard server running at port defined in `PORT` variable.

### After deployment (all ways)

It is recommended to configure root user via `ROOT_USER_*` enviroment variables. This will provide you with initial
admin user. After setting up everything, head to client `/login` page and use the root user credentials. This will allow
you to access the Dashboard and set up additional users/create boards etc.

There is also running [Spring Boot Admin](https://github.com/codecentric/spring-boot-admin) instance available at `/sba`
endpoint on your server instance. This can be used for checking logs and various metrics, such as memory consumption of
application. To access you need to be logged in as user with `ACTUATOR_ACCESS` permission (root user has it).

## Contributing

There are several ways for contributing to the project. I will be thankful for all of them.

- Star the repository and share the word
- Open a new issue if you encounter a bug or have feature/improvement request
- Contribute to the code - see [CONTRIBUTING.md](https://github.com/njuro/jard/blob/master/CONTRIBUTING.md) for details
