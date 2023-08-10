plugins {
  java
  jacoco
  checkstyle
  id("com.diffplug.spotless") version "6.20.0"
  id("io.spring.dependency-management") version "1.1.3"
  id("org.springframework.boot") version "3.1.2"
}

group = "demo.ddz"
version = "0.0.1-SNAPSHOT"

java {
  sourceCompatibility = JavaVersion.VERSION_19
}

repositories {
  mavenCentral()
}

dependencies {
  annotationProcessor("org.projectlombok:lombok:1.18.28")
  compileOnly("org.projectlombok:lombok:1.18.28")
  implementation("org.springframework.boot:spring-boot-starter")
  testAnnotationProcessor("org.projectlombok:lombok:1.18.28")
  testCompileOnly("org.projectlombok:lombok:1.18.28")
  testImplementation("org.junit.jupiter:junit-jupiter-params:5.10.0")
  testImplementation("org.springframework.boot:spring-boot-starter-test")
  testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

tasks.withType<Test> {
  useJUnitPlatform()
}

tasks.test {
  finalizedBy(tasks.jacocoTestReport)
}

tasks.jacocoTestReport {
  dependsOn(tasks.test)
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
}

tasks.check {
  dependsOn(tasks.jacocoTestCoverageVerification)
}

jacoco {
  toolVersion = "0.8.10"
}

tasks.checkstyleMain {
  group = "verification"
}

tasks.checkstyleTest {
  group = "verification"
}

checkstyle {
  toolVersion = "10.12.2"
  isIgnoreFailures = false
  maxErrors = 0
  maxWarnings = 0
  configFile = file("${rootDir}/service/checkstyle/checkstyle.xml")
  configProperties = mapOf(
    "org.checkstyle.google.suppressionfilter.config" to "${rootDir}/service/checkstyle/suppressions.xml"
  )
}

spotless {
  java {
    googleJavaFormat()
  }
}

tasks.check {
  dependsOn(tasks.spotlessCheck)
}
