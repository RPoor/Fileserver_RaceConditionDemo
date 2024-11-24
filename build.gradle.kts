import org.beryx.jlink.JPackageImageTask

plugins {
    `lifecycle-base`
    alias(libs.plugins.jlink) apply false
}

tasks {
    register<Delete>("package_pre").configure {
        delete(layout.buildDirectory.dir("fileserver"))
        delete(layout.buildDirectory.dir("attacker"))
    }
    register<Copy>("package").configure {
        for (proj in arrayOf("fileserver", "attacker")) {
            val jpkg = project(":$proj").tasks.named<JPackageImageTask>("jpackageImage")
            from(jpkg.map { it.imageOutputDir })
            into(layout.buildDirectory)
            dependsOn("package_pre")
        }
    }
    named("build") {
        dependsOn("package")
    }
}