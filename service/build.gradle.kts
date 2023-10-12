plugins {
  java
  jacoco
  id("com.diffplug.spotless") version "6.22.0"
  id("com.github.kt3k.coveralls") version "2.12.2"
  id("io.spring.dependency-management") version "1.1.3"
  id("org.springframework.boot") version "3.1.4"
}

group = "demo.ddz"
version = "0.0.1-SNAPSHOT"

java {
  sourceCompatibility = JavaVersion.VERSION_20
}

repositories {
  mavenCentral()
}

dependencies {
  annotationProcessor("org.projectlombok:lombok:1.18.30")
  compileOnly("org.projectlombok:lombok:1.18.30")
  implementation("com.github.ben-manes.caffeine:caffeine:3.1.8")
  implementation("org.springframework.boot:spring-boot-starter")
  implementation("org.springframework.boot:spring-boot-starter-aop")
  implementation("org.springframework.boot:spring-boot-starter-webflux")
  testAnnotationProcessor("org.projectlombok:lombok:1.18.30")
  testCompileOnly("org.projectlombok:lombok:1.18.30")
  testImplementation("io.projectreactor:reactor-test:3.5.11")
  testImplementation("org.assertj:assertj-core:3.24.2")
  testImplementation("org.awaitility:awaitility:4.2.0")
  testImplementation("org.junit.jupiter:junit-jupiter-params:5.10.0")
  testImplementation("org.mockito:mockito-core:5.6.0")
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

tasks.jacocoTestReport {
  dependsOn(tasks.test)

  reports {
    html.required.set(true)
    xml.required.set(true)
  }
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

tasks.withType<JavaCompile> {
  options.compilerArgs.add("--enable-preview")
}

tasks.check {
  dependsOn(tasks.jacocoTestCoverageVerification)
}

jacoco {
  toolVersion = "0.8.10"
}

spotless {
  java {
    googleJavaFormat()
  }
}

tasks.check {
  dependsOn(tasks.spotlessCheck)
}

coveralls {
  jacocoReportPath = "build/reports/jacoco/test/jacocoTestReport.xml"
}
