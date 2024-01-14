/*
 * Copyright (c) 2020 https://github.com/jinganix/ddz, All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package utils

import org.gradle.api.Project
import java.util.*
import kotlin.reflect.KMutableProperty
import kotlin.reflect.full.memberProperties

object Vers {
  private var initialized = false

  lateinit var versionArchunit: String
  lateinit var versionAssertj: String
  lateinit var versionAuthorizationServer: String
  lateinit var versionAwaitility: String
  lateinit var versionCaffeine: String
  lateinit var versionCommonsLang3: String
  lateinit var versionCoverallsGradlePlugin: String
  lateinit var versionDependencyManagementPlugin: String
  lateinit var versionJacocoAgent: String
  lateinit var versionJupiter: String
  lateinit var versionJwt: String
  lateinit var versionLombok: String
  lateinit var versionMapstruct: String
  lateinit var versionMockitoCore: String
  lateinit var versionPeashooter: String
  lateinit var versionProtobuf: String
  lateinit var versionProtobufGradlePlugin: String
  lateinit var versionReactorTest: String
  lateinit var versionSpotlessPluginGradle: String
  lateinit var versionSpringBootGradlePlugin: String
  lateinit var versionWebpb: String

  fun initialize(project: Project, override: Properties = Properties()) {
    if (initialized) {
      return
    }
    this::class.memberProperties.forEach {
      if (it !is KMutableProperty<*>) {
        return
      }
      val key = it.name
      if (override.containsKey(key)) {
        it.setter.call(this, override.getProperty(key))
      } else if (project.hasProperty(key)) {
        it.setter.call(this, project.property(key))
      }
    }
    initialized = true
  }
}
