# jard
## Basic FAQ
### What is jard?
**jard** (pronounced like *yard*)  is a software for running an anonymous imageboard. Citing from Wikipedia:
> An imageboard is a type of Internet forum that revolves around the posting 
> of images, often alongside related text and discussion. 

Most notable examples of imageboards include 2channel, 4chan, 8chan, Krautchan etc.

This repository contains backend exposing REST API, which is consumed by frontend client written in React (see [jard-client](https://github.com/njuro/jard-client)).

### Is jard based on existing software?
Code-wise is jard written entirely from scratch and is not a fork of any existing software. Design- and feature-wise it is heavily influenced by aforementioned imageboards.

### Why make another imageboard software?
**Short answer:** I don't know, why not?
**Long answer:** I identified 3 main motivation factors behind the creation of jard:

 1. Many of the software behind popular imageboards is either closed-source (4chan) and/or aged and no longer actively developed or maintained (example of this is vichan - fork of Tinyboard - software which powered 8chan). Also from the rumors and [code leaks](https://gist.github.com/dvliman/11264471), they are often undocumented monolithic PHP applications hacked together throughout the years with little attention to code quality, testability and maintainability. While I don't want to bash them (they are time-tested and work +- reliably most of the time), my vision was to create open-source, documented software which can be easily customized and extended by almost anyone willing to put effort into it. There is a long way to go to achieve this goal, but you need to start somewhere. Note: I am also aware that similiar attempts to produce "modern" imageboard software exist, however from my short research most of them are merely proofs of concept and lack many important functionality and polishing.
 2. Similarly, I found that given its enterprise nature not many medium-size real-world open-source applications written in Spring Boot (framework jard's backend is written in) exist. There are many tutorial level examples on GitHub, demonstrating different aspects of Spring, but I am not aware of many up-to-date Spring apps the beginner/intermediate developer could easily contribute to (except the framework itself).
 3. I wanted to try and learn new concepts, technologies, approaches and try to create and maintain my very own project bigger than regular TicTacToe / FizzBuzz app. This is actually the biggest motivation factor of them all and the only one I am sure is true, because it already gave me much.

### What technologies is jard built with?
The backend is written in Java 11, with Spring Boot 2.x framework (powered by Spring 5.x), using PostgreSQL as relational database (tests use in-memory H2 database) and Hibernate as ORM. Files uploaded by users are stored in Amazon S3 bucket.  Authentication is handled with JWT cookies (using jjwt library). Backend exposes REST API with protected endpoints. Several other libraries are used for different functionality, as described in *pom.xml* file.

Travis CI is enabled for the project and master branch is regulary deployed to Heroku.
For frontend technologies see [jard-client](https://github.com/njuro/jard-client).  

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
- [x] Using [SAGE](https://knowyourmeme.com/memes/sage) when posting
- [x] Using secure [tripcode](https://en.wiktionary.org/wiki/tripcode) when posting
- [x] Using [code] tags for block of formatted code
- [x] Using [spoiler] tags for - duh - spoilers, revealed on mouse hover
- [x] Using greentext (>)
- [x] Crosslinking to different posts/threads/boards (e.g. >>>/g/123#4)
- [x] Quoting post by selecting content and clicking on its number
- [x] Both manual and auto updating of opened threads

From **admin** perspective:

- [x] Creating / editing / deleting boards
	- [x] Specifying NSFW status, thread and bump limits, default poster name etc.
- [x] Creating / editing / deleting users
	- [x]  Assigning predefined roles (janitor/moderator/admin) each with set of permissions
- [x] Banning IP addresses from posting temporarily or permanently, editing of bans / unbanning
- [x] Locking of threads
- [x] Stickying of threads
- [x] Deleting of posts and threads

### Planned

- [ ] Multiple attachments per post
- [ ] Youtube / Vimeo embedding as attachment
- [ ]  Allow to define user roles with custom set of permissions
- [ ]  Warning instead of ban
- [ ]  Post report system
- [ ] (You) to highlight replies to one's own posts
- [ ] Password reset system
- [ ] Custom user themes
- [ ]  Better logo
- [ ] Many more...

## Contributing
The software is in early state, so contributing / deploying guidelines are not developed yet. I will try to create them ASAP. For now, share the word and if you somehow manage to get it running by yourself and have something to contribute, feel free to open issue / pull request, it will make my day.
