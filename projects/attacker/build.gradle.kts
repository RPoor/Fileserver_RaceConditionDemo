plugins {
    java
    application
    alias(libs.plugins.lombok)
}

application {
    mainClass = "bit.attacker.Main"
}

tasks.jar {
    manifest {
        attributes(
            "Main-Class" to "bit.attacker.Main"
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
    implementation(libs.apache.httpclient)
    implementation(libs.slf4j.api)
    implementation(libs.slf4j.simple)
}