import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.3.20"
}

val versionJUnit5 = "5.3.1"
val versionTruth = "0.42"

val jUnit5Api by extra { "org.junit.jupiter:junit-jupiter-api:$versionJUnit5" }
val jUnit5Engine by extra { "org.junit.jupiter:junit-jupiter-engine:$versionJUnit5" }
val truthJava8 by extra { "com.google.truth.extensions:truth-java8-extension:$versionTruth" }
val okio by extra { "com.squareup.okio:okio:1.15.0" }
val jimfs by extra { "com.google.jimfs:jimfs:1.1" }
val mockito by extra { "org.mockito:mockito-core:2.21.0" }
val findbugsAnnotations by extra { "com.google.code.findbugs:jsr305:3.0.2" }

group = "com.volkhart.sphinx"
version = "1.0-SNAPSHOT"

allprojects {
    repositories {
        mavenCentral()
    }

    tasks.withType<KotlinCompile> {
        kotlinOptions.jvmTarget = "1.8"
    }
}
