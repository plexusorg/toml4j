plugins {
    java
    `maven-publish`
}

group = "dev.plex"
version = "0.7.3"

repositories {
    mavenCentral()
}

dependencies {
    implementation("com.google.code.gson:gson:2.9.0")
    implementation("org.projectlombok:lombok:1.18.24")
    implementation("org.jetbrains:annotations:23.0.0")
    annotationProcessor("org.projectlombok:lombok:1.18.24")
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            from(components["java"])
        }
        repositories {
            maven {
                val releasesRepoUrl = uri("https://nexus.telesphoreo.me/repository/plex-releases")
                val snapshotsRepoUrl = uri("https://nexus.telesphoreo.me/repository/plex-snapshots")
                url = uri(if (version.toString().endsWith("SNAPSHOT")) snapshotsRepoUrl else releasesRepoUrl)
                credentials {
                    username = System.getenv("plexUser")
                    password = System.getenv("plexPassword")
                }
            }
        }
    }
}