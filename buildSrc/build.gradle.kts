import org.gradle.api.JavaVersion.*
import org.jetbrains.kotlin.konan.properties.Properties
import java.io.FileInputStream

plugins {
  `kotlin-dsl`
  `maven-publish`
}

val javaVersion = VERSION_21

java {
  sourceCompatibility = javaVersion
  targetCompatibility = javaVersion
}

tasks.compileKotlin {
  kotlinOptions {
    jvmTarget = javaVersion.toString()
  }
}

repositories {
  gradlePluginPortal()
  mavenCentral()
}

val properties = Properties()
FileInputStream(file("../gradle.properties")).use(properties::load)

for (key in properties.stringPropertyNames()) {
  ext.set(key, properties.getProperty(key))
}

val versionCoverallsGradlePlugin: String by project
val versionDependencyManagementPlugin: String by project
val versionGradleVersionsPlugin: String by project
val versionJacocoAgent: String by project
val versionProtobufGradlePlugin: String by project
val versionSpotlessPluginGradle: String by project
val versionSpringBootGradlePlugin: String by project

dependencies {
  implementation("com.diffplug.spotless:spotless-plugin-gradle:${versionSpotlessPluginGradle}")
  implementation("com.github.ben-manes:gradle-versions-plugin:${versionGradleVersionsPlugin}")
  implementation("com.github.kt3k.coveralls:com.github.kt3k.coveralls.gradle.plugin:${versionCoverallsGradlePlugin}")
  implementation("com.google.protobuf:protobuf-gradle-plugin:${versionProtobufGradlePlugin}")
  implementation("io.spring.gradle:dependency-management-plugin:${versionDependencyManagementPlugin}")
  implementation("org.jacoco:org.jacoco.agent:${versionJacocoAgent}")
  implementation("org.springframework.boot:spring-boot-gradle-plugin:${versionSpringBootGradlePlugin}")
  implementation(kotlin("script-runtime"))
}
