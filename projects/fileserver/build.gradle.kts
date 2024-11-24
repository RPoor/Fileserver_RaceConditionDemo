plugins {
    java
    alias(libs.plugins.lombok)
    alias(libs.plugins.jlink)
    alias(libs.plugins.modules)
}

application {
    mainClass = "fileserver.Main"
    mainModule = "fileserver"
}

tasks.jar {
    manifest {
        attributes(
            "Implementation-Title" to "fileserver",
            "Main-Class" to "fileserver.Main"
            )
    }
}

tasks.named<JavaExec>("run") {
    workingDir = rootProject.projectDir
}

jlink {
    launcher {
        name = "fileserver"
    }
    jpackage {
        val os = org.gradle.internal.os.OperatingSystem.current()
        if (os.isWindows) {
            imageOptions.add("--win-console")
        }
    }
}

repositories {
    mavenCentral()
}

dependencies {
    compileOnly(libs.jetbrains.annotations)
    implementation(libs.picocli)
    implementation(libs.fileupload)
}