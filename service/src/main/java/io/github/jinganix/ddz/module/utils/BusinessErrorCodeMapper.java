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

package io.github.jinganix.ddz.module.utils;

import io.github.jinganix.ddz.helper.exception.BusinessErrorCode;
import io.github.jinganix.ddz.proto.error.ErrorCode;
import io.github.jinganix.ddz.setup.exception.ErrorCodeMapper;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface BusinessErrorCodeMapper extends ErrorCodeMapper {

  ErrorCode mapErrorCode(io.github.jinganix.ddz.module.utils.ErrorCode errorCode);

  default ErrorCode map(BusinessErrorCode errorCode) {
    if (errorCode instanceof io.github.jinganix.ddz.module.utils.ErrorCode) {
      return mapErrorCode((io.github.jinganix.ddz.module.utils.ErrorCode) errorCode);
    }
    return ErrorCode.ERROR;
  }
}
