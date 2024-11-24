# Compiling and running the project

Dependencies:
- Java 21 in PATH

Run `./gradlew build` to compile both the file server and the attacker files. The binaries will be put inside `./projects/fileserver/build/distributions` and `./projects/attacker/build/distributions`, respectively.
After this, you can launch the file server using the `./server.sh` script and the attacker using `./attack.sh`.

# Project structure

The project is divided up into 2 subprojects:

- `fileserver`: The file server with the naive, blocking, and non-blocking IO backends
- `attacker`: the attacker which attempts to inject malicious files into the file server

The following assumption is made for the attack scenario :

The file upload site has a consistent number of users who upload files at a high enough rate that a short burst of malicious file from the attacker has a chance of hitting.
This is emulated by the attacker process, with one thread doing "legitimate" requests while the other thread tries to hijack these with bad files.