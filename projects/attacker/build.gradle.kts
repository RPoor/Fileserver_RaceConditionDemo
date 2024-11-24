plugins {
    java
    alias(libs.plugins.lombok)
    alias(libs.plugins.jlink)
    alias(libs.plugins.modules)
}

application {
    mainClass = "attacker.Main"
    mainModule = "attacker"
}

tasks.jar {
    manifest {
        attributes(
            "Implementation-Title" to "attacker",
            "Main-Class" to "attacker.Main"
            )
    }
}



jlink {
    launcher {
        name = "attacker"
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
    implementation(libs.apache.httpclient)
}