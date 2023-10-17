/*
 * Copyright (c) 2020 jinganix@qq.com, All Rights Reserved.
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

package io.github.jinganix.ddz.tests;

import io.github.jinganix.ddz.helper.auth.token.TokenService;
import io.github.jinganix.ddz.helper.uid.UidGenerator;
import io.github.jinganix.ddz.helper.utils.UtilsService;
import io.github.jinganix.ddz.module.player.PlayerRepository;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;

/** Tests for spring component. */
@ContextConfiguration
@ActiveProfiles("test")
@AutoConfigureWebTestClient
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public abstract class SpringIntegrationWithSpiedBeansTests {

  @SpyBean protected TokenService tokenService;

  @SpyBean protected UidGenerator uidGenerator;

  @SpyBean protected UtilsService utilsService;

  @SpyBean protected PlayerRepository playerRepository;
}
