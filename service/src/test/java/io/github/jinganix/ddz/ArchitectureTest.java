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

package io.github.jinganix.ddz;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.core.importer.ImportOption;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@DisplayName("Architecture")
public class ArchitectureTest {

  String packageName = this.getClass().getPackageName();
  JavaClasses importedClasses =
      new ClassFileImporter()
          .withImportOption(ImportOption.Predefined.DO_NOT_INCLUDE_TESTS)
          .importPackages(packageName);

  @Nested
  @DisplayName("when classes reside in helper")
  class WhenClassesResideInHelper {

    @Nested
    @DisplayName("when check access to module")
    class WhenCheckAccessToModule {

      @Test
      @DisplayName("then should not access")
      void thenShouldNotAccess() {
        noClasses()
            .that()
            .resideInAPackage(packageName + ".helper..")
            .should()
            .accessClassesThat()
            .resideInAPackage(packageName + ".module..")
            .check(importedClasses);
      }
    }

    @Nested
    @DisplayName("when check access to setup")
    class WhenCheckAccessToSetup {

      @Test
      @DisplayName("then should not access")
      void thenShouldNotAccess() {
        noClasses()
            .that()
            .resideInAPackage(packageName + ".helper..")
            .should()
            .accessClassesThat()
            .resideInAPackage(packageName + ".setup..")
            .check(importedClasses);
      }
    }
  }

  @Nested
  @DisplayName("when classes reside in setup")
  class WhenClassesResideInSetup {

    @Nested
    @DisplayName("when check access to module")
    class WhenCheckAccessToModule {

      @Test
      @DisplayName("then should not access module")
      void thenShouldNotAccessModule() {
        noClasses()
            .that()
            .resideInAPackage(packageName + ".setup..")
            .should()
            .accessClassesThat()
            .resideInAPackage(packageName + ".module..")
            .check(importedClasses);
      }
    }
  }
}
