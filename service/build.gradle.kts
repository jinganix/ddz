import com.google.protobuf.gradle.id
import com.google.protobuf.gradle.remove

plugins {
  id("com.diffplug.spotless") version "6.22.0"
  id("com.github.kt3k.coveralls") version "2.12.2"
  id("com.google.protobuf") version "0.9.4"
  id("io.spring.dependency-management") version "1.1.4"
  id("org.springframework.boot") version "3.1.5"
  jacoco
  java
}

group = "io.github.jinganix.ddz"
version = "0.0.1-SNAPSHOT"

java {
  sourceCompatibility = JavaVersion.VERSION_20
}

repositories {
  mavenCentral()
  maven { url = uri("https://s01.oss.sonatype.org/content/repositories/snapshots") }
}

dependencies {
  annotationProcessor("io.github.jinganix.webpb:webpb-processor:0.0.1-SNAPSHOT")
  annotationProcessor("org.mapstruct:mapstruct-processor:1.5.5.Final")
  annotationProcessor("org.projectlombok:lombok:1.18.30")
  compileOnly("org.projectlombok:lombok:1.18.30")

  implementation("com.auth0:java-jwt:4.4.0")
  implementation("com.github.ben-manes.caffeine:caffeine:3.1.8")
  implementation("com.google.protobuf:protobuf-gradle-plugin:0.9.4")
  implementation("io.github.jinganix.webpb:webpb-proto:0.0.1-SNAPSHOT")
  implementation("io.github.jinganix.webpb:webpb-runtime:0.0.1-SNAPSHOT")
  implementation("org.apache.commons:commons-lang3:3.13.0")
  implementation("org.mapstruct:mapstruct:1.5.5.Final")
  implementation("org.springframework.boot:spring-boot-starter")
  implementation("org.springframework.boot:spring-boot-starter-aop")
  implementation("org.springframework.boot:spring-boot-starter-rsocket")
  implementation("org.mapstruct:mapstruct:1.5.5.Final")
  implementation("org.springframework.boot:spring-boot-starter-security")
  implementation("org.springframework.boot:spring-boot-starter-validation")
  implementation("org.springframework.boot:spring-boot-starter-webflux")
  implementation("org.springframework.security:spring-security-messaging")
  implementation("org.springframework.security:spring-security-oauth2-authorization-server:1.1.3")
  implementation("org.springframework.security:spring-security-rsocket")

  protobuf(project(":proto"))
  testAnnotationProcessor("io.github.jinganix.webpb:webpb-processor:0.0.1-SNAPSHOT")
  testAnnotationProcessor("org.mapstruct:mapstruct-processor:1.5.5.Final")
  testAnnotationProcessor("org.projectlombok:lombok:1.18.30")
  testCompileOnly("org.projectlombok:lombok:1.18.30")
  testImplementation("com.tngtech.archunit:archunit:1.2.0")
  testImplementation("io.projectreactor:reactor-test:3.6.0")
  testImplementation("org.assertj:assertj-core:3.24.2")
  testImplementation("org.awaitility:awaitility:4.2.0")
  testImplementation("org.junit.jupiter:junit-jupiter-params:5.10.1")
  testImplementation("org.mockito:mockito-core:5.7.0")
  testImplementation("org.springframework.boot:spring-boot-starter-test")
  testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

tasks.withType<Test> {
  useJUnitPlatform()
  jvmArgs("--enable-preview")
}

tasks.test {
  finalizedBy(tasks.jacocoTestReport)
}

val jacocoExcludes = listOf("io/github/jinganix/ddz/module/utils/BusinessErrorCodeMapperImpl.class")

tasks.jacocoTestReport {
  dependsOn(tasks.test)

  reports {
    html.required.set(true)
    xml.required.set(true)
  }

  // TODO: exclude with mapstruct 1.6 @AnnotateWith(Generated.class)
  classDirectories.setFrom(classDirectories.files.map {
    fileTree(it).matching { exclude(jacocoExcludes) }
  })
}

tasks.jacocoTestCoverageVerification {
  dependsOn(tasks.jacocoTestReport)
  violationRules {
    rule {
      limit {
        minimum = "1".toBigDecimal()
      }
    }
  }

  // TODO: exclude with mapstruct 1.6 @AnnotateWith(Generated.class)
  classDirectories.setFrom(classDirectories.files.map {
    fileTree(it).matching { exclude(jacocoExcludes) }
  })
}

tasks.withType<JavaCompile> {
  options.compilerArgs.add("--enable-preview")
}

tasks.check {
  dependsOn(tasks.jacocoTestCoverageVerification)
}

jacoco {
  toolVersion = "0.8.11"
}

spotless {
  java {
    googleJavaFormat()

    targetExclude("build/**/*.java")
  }
}

tasks.check {
  dependsOn(tasks.spotlessCheck)
}

coveralls {
  jacocoReportPath = "build/reports/jacoco/test/jacocoTestReport.xml"
}

protobuf {
  protoc {
    artifact = "com.google.protobuf:protoc:3.25.1"
  }
  plugins {
    id("webpb") {
      artifact = "io.github.jinganix.webpb:webpb-protoc-java:0.0.1-SNAPSHOT:all@jar"
    }
  }
  generateProtoTasks {
    ofSourceSet("main").forEach {
      it.doFirst {
        delete(it.outputBaseDir)
      }
      it.builtins {
        remove("java")
      }
      it.plugins {
        id("webpb")
      }
    }
  }
}
