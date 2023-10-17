plugins {
  java
}

repositories {
  mavenLocal()
  mavenCentral()
  maven { url = uri("https://s01.oss.sonatype.org/content/repositories/snapshots") }
}

dependencies {
  compileOnly("io.github.jinganix.webpb:webpb-proto:0.0.1-SNAPSHOT")
}
