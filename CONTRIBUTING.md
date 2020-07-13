# Contributing

This section contains instructions for setting up developer enviroment in order to contribute to the code of **jard**.

## Required
- `git` - with hooks enabled (they should be enabled by default).
- `JDK 11` - I use Oracle version, but OpenJDK should be fine too.
- `Maven` - at least version 3.5 (alternatively use Maven wrapper builded with the project).
- `Lombok plugin` - this project uses Lombok for boilerplate code generation. In order for IDE to stop yelling at you for "missing" methods and fields, install Lombok plugin - [most used IDEs are supported](https://projectlombok.org/setup/overview).

## Recommended
- `google-java-format plugin` - this project uses [Google Java style conventions](https://github.com/google/google-java-format) . Altough there are git hooks set up in *pom.xml* which pre-format the changed code before committing, it is nice to have it incorporated into your IDE's default code format function.
- `Save Actions plugin`- convenient plugin which enables you to set your IDE to format code (for example with aforementioned google plugin) everytime you save the file.
-  `Sonar Lint plugin` - static code analyzer, helps you with discovering code smells, bugs, vulnerabilities etc. [Integrations with most used IDEs exist](https://www.sonarlint.org).

## Contribution flow 
- Fork the base repository
- Clone your fork with `git clone` 
- Install dependencies with `mvn clean install`
- Make changes in code
- Commit and push to your fork
- Open pull request on base repository (optionally - link issue this PR relates to)
	- Allow code changes in PR from base repository maintainers
- Wait for PR approval/feedback/merge

Thanks for all the future contributions!
