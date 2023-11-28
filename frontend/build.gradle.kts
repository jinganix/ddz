import com.google.protobuf.gradle.id
import com.google.protobuf.gradle.remove
import org.apache.tools.ant.taskdefs.condition.Os

plugins {
  id("com.google.protobuf") version "0.9.4"
  java
}

repositories {
  mavenCentral()
}

val npm = if (Os.isFamily(Os.FAMILY_WINDOWS)) "npm.cmd" else "npm"

task<Exec>("npmInstall") {
  val nodeModules = file("./node_modules")
  if (nodeModules.exists()) {
    commandLine(npm, "--version")
  } else {
    commandLine(npm, "install", "--verbose")
  }
}

task<Exec>("npmStart") {
  commandLine(npm, "run", "dev")

  dependsOn("npmInstall")
}

task<Exec>("npmCheck") {
  commandLine(npm, "run", "lint")
  commandLine(npm, "run", "test")

  dependsOn("npmInstall")
}

tasks.check {
  dependsOn("npmCheck")
}

dependencies {
  implementation("io.github.jinganix.webpb:webpb-proto:0.0.1")
  protobuf(project(":proto"))
}

tasks.clean {
  enabled = false
}


protobuf {
  protoc {
    artifact = "com.google.protobuf:protoc:3.25.1"
  }
  plugins {
    id("ts") {
      artifact = "io.github.jinganix.webpb:webpb-protoc-ts:0.0.1:all@jar"
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
        id("ts")
      }
    }
  }
}
