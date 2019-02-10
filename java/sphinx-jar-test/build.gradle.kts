import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm")
}

val jUnit5Api: String by rootProject.extra
val jUnit5Engine: String by rootProject.extra
val truthJava8: String by rootProject.extra
val okio: String by rootProject.extra

dependencies {
    testCompile(jUnit5Api)
    testRuntime(jUnit5Engine)
    testImplementation(truthJava8)
    testImplementation(kotlin("stdlib-jdk8"))
    testImplementation(okio)
    testCompile(project(":sphinx"))
}

tasks {
    // Use the built-in JUnit support of Gradle.
    "test"(Test::class) {
        useJUnitPlatform()
    }
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}