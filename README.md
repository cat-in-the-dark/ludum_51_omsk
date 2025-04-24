# ALYOEP

## Credits

From Siberia 🇷🇺 with love ❤️ and bears 🐻!

## Platforms

- `core`: Main module with the application logic shared by all platforms.
- `lwjgl3`: Primary desktop platform using LWJGL3; was called 'desktop' in older docs.
- `teavm`: Web backend that supports most JVM languages.
- `lwjgl2`: Legacy desktop platform using LWJGL2.

## Gradle

This project uses [Gradle](https://gradle.org/) to manage dependencies.
The Gradle wrapper was included, so you can run Gradle tasks using `gradlew.bat` or `./gradlew` commands.
Useful Gradle tasks and flags:

- `--continue`: when using this flag, errors will not stop the tasks from running.
- `--daemon`: thanks to this flag, Gradle daemon will be used to run chosen tasks.
- `--offline`: when using this flag, cached dependency archives will be used.
- `--refresh-dependencies`: this flag forces validation of all dependencies. Useful for snapshot versions.
- `build`: builds sources and archives of every project.
- `cleanEclipse`: removes Eclipse project data.
- `cleanIdea`: removes IntelliJ project data.
- `clean`: removes `build` folders, which store compiled classes and built archives.
- `eclipse`: generates Eclipse project data.
- `idea`: generates IntelliJ project data.
- `lwjgl2:jar`: builds application's runnable jar, which can be found at `lwjgl2/build/libs`.
- `lwjgl2:run`: starts the application.
- `lwjgl3:jar`: builds application's runnable jar, which can be found at `lwjgl3/build/libs`.
- `lwjgl3:run`: starts the application.
- `teavm:build`: builds the JavaScript application into the build/dist/webapp folder.
- `teavm:run`: serves the JavaScript application at http://localhost:8080 via a local Jetty server.
- `test`: runs unit tests (if any).

## Deployment
- Mac M1 `lwjgl3:packageMacM1`
- Mac OSX `lwjgl3:packageMacX64`
- Linux `lwjgl3:packageLinuxX64`
- Windows `lwjgl3:packageWinX64`
- Web `teavm:build`

This creates a zip file in lwjgl3/build/construo/dist containing your game and the minimized JRE for the target platform. See this section in the GDX-Liftoff video

Note that most tasks that are not specific to a single project can be run with `name:` prefix, where the `name` should be replaced with the ID of a specific project.
For example, `core:clean` removes `build` folder only from the `core` project.
