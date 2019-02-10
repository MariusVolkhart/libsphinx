import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm")
}

val jUnit5Api: String by rootProject.extra
val jUnit5Engine: String by rootProject.extra
val truthJava8: String by rootProject.extra
val okio: String by rootProject.extra
val jimfs: String by rootProject.extra
val mockito: String by rootProject.extra
val findbugsAnnotations: String by rootProject.extra

dependencies {
    api(findbugsAnnotations)
    implementation(okio)

    testCompile(jUnit5Api)
    testRuntime(jUnit5Engine)
    testImplementation(truthJava8)
    testImplementation(kotlin("stdlib-jdk8"))
    testImplementation(okio)
    testImplementation(jimfs)
    testImplementation(mockito)
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