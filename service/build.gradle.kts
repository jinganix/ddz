import com.google.protobuf.gradle.id
import com.google.protobuf.gradle.remove
import utils.Vers.versionArchunit
import utils.Vers.versionAssertj
import utils.Vers.versionAuthorizationServer
import utils.Vers.versionAwaitility
import utils.Vers.versionCaffeine
import utils.Vers.versionCommonsLang3
import utils.Vers.versionJacocoAgent
import utils.Vers.versionJupiter
import utils.Vers.versionJwt
import utils.Vers.versionLombok
import utils.Vers.versionMapstruct
import utils.Vers.versionMockitoCore
import utils.Vers.versionProtobuf
import utils.Vers.versionProtobufGradlePlugin
import utils.Vers.versionReactorTest
import utils.Vers.versionWebpb

plugins {
  id("com.diffplug.spotless")
  id("com.github.kt3k.coveralls")
  id("com.google.protobuf")
  id("conventions.common")
  id("io.spring.dependency-management")
  id("org.springframework.boot")
  jacoco
  java
}

group = "io.github.jinganix.ddz"
version = "${versionWebpb}-SNAPSHOT"

java {
  sourceCompatibility = JavaVersion.VERSION_20
}

dependencies {
  annotationProcessor("io.github.jinganix.webpb:webpb-processor:${versionWebpb}")
  annotationProcessor("org.mapstruct:mapstruct-processor:${versionMapstruct}")
  annotationProcessor("org.projectlombok:lombok:${versionLombok}")
  compileOnly("org.projectlombok:lombok:${versionLombok}")

  implementation("com.auth0:java-jwt:${versionJwt}")
  implementation("com.github.ben-manes.caffeine:caffeine:${versionCaffeine}")
  implementation("com.google.protobuf:protobuf-gradle-plugin:${versionProtobufGradlePlugin}")
  implementation("io.github.jinganix.webpb:webpb-proto:${versionWebpb}")
  implementation("io.github.jinganix.webpb:webpb-runtime:${versionWebpb}")
  implementation("org.apache.commons:commons-lang3:${versionCommonsLang3}")
  implementation("org.mapstruct:mapstruct:${versionMapstruct}")
  implementation("org.springframework.boot:spring-boot-starter")
  implementation("org.springframework.boot:spring-boot-starter-aop")
  implementation("org.springframework.boot:spring-boot-starter-rsocket")
  implementation("org.mapstruct:mapstruct:${versionMapstruct}")
  implementation("org.springframework.boot:spring-boot-starter-security")
  implementation("org.springframework.boot:spring-boot-starter-validation")
  implementation("org.springframework.boot:spring-boot-starter-webflux")
  implementation("org.springframework.security:spring-security-messaging")
  implementation("org.springframework.security:spring-security-oauth2-authorization-server:${versionAuthorizationServer}")
  implementation("org.springframework.security:spring-security-rsocket")

  protobuf(project(":proto"))
  testAnnotationProcessor("io.github.jinganix.webpb:webpb-processor:${versionWebpb}")
  testAnnotationProcessor("org.mapstruct:mapstruct-processor:${versionMapstruct}")
  testAnnotationProcessor("org.projectlombok:lombok:${versionLombok}")
  testCompileOnly("org.projectlombok:lombok:${versionLombok}")
  testImplementation("com.tngtech.archunit:archunit:${versionArchunit}")
  testImplementation("io.projectreactor:reactor-test:${versionReactorTest}")
  testImplementation("org.assertj:assertj-core:${versionAssertj}")
  testImplementation("org.awaitility:awaitility:${versionAwaitility}")
  testImplementation("org.junit.jupiter:junit-jupiter-params:${versionJupiter}")
  testImplementation("org.mockito:mockito-core:${versionMockitoCore}")
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
  toolVersion = versionJacocoAgent
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
    artifact = "com.google.protobuf:protoc:${versionProtobuf}"
  }
  plugins {
    id("webpb") {
      artifact = "io.github.jinganix.webpb:webpb-protoc-java:${versionWebpb}:all@jar"
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
