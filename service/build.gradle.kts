plugins {
    java
    jacoco
    checkstyle
    id("org.springframework.boot") version "3.1.1"
    id("io.spring.dependency-management") version "1.1.2"
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
    implementation("org.springframework.boot:spring-boot-starter")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
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
    violationRules {
        rule {
            limit {
                minimum = "1".toBigDecimal()
            }
        }
    }
}

jacoco {
    toolVersion = "0.8.9"
}

checkstyle {
    toolVersion = "10.12.1"
    isIgnoreFailures = false
    maxErrors = 0
    maxWarnings = 0
    configFile = file("${rootDir}/service/checkstyle/checkstyle.xml")
    configProperties = mapOf(
        "org.checkstyle.google.suppressionfilter.config" to "${rootDir}/service/checkstyle/suppressions.xml"
    )
}
