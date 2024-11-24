plugins {
    java
    application
    alias(libs.plugins.lombok)
}

application {
    mainClass = "bit.fileserver.Main"
}

tasks.jar {
    manifest {
        attributes(
            "Main-Class" to "bit.fileserver.Main"
            )
    }
}

tasks.named<JavaExec>("run") {
    workingDir = rootProject.projectDir
}

tasks.build {
    dependsOn("installDist")
}

repositories {
    mavenCentral()
}

dependencies {
    compileOnly(libs.jetbrains.annotations)
    implementation(libs.picocli)
    implementation(libs.fileupload)
}