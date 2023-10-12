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

package demo.ddz.module.auth.model;

import demo.ddz.helper.enumeration.EnumMapper;
import demo.ddz.helper.enumeration.Enumeration;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

@Getter
public enum GrantedRole implements Enumeration<String> {

  /** VISITOR */
  VISITOR("visitor"),

  /** USER */
  PLAYER("player");

  private static final EnumMapper<String, GrantedRole> mapper =
      new EnumMapper<>(values(), GrantedRole::getValue);

  private final String value;

  private final GrantedAuthority authority;

  GrantedRole(String value) {
    this.value = value;
    this.authority = new SimpleGrantedAuthority("ROLE_" + value);
  }

  public static GrantedRole fromValue(String value) {
    return mapper.fromValue(value);
  }

  @Override
  public String getValue() {
    return value;
  }
}
