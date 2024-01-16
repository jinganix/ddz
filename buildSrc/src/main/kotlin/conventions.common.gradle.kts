import utils.Vers

plugins {
  id("conventions.versioning")
  idea
}

Vers.initialize(project)

repositories {
  mavenCentral()
}
