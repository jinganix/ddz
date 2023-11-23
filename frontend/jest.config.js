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

const _require = require;
const { pathsToModuleNameMapper } = _require("ts-jest");
const { compilerOptions } = _require("./tsconfig");

module.exports = {
  bail: true,
  collectCoverage: true,
  collectCoverageFrom: ["src/**/*.{js,jsx,ts,tsx}", "!src/**/*.d.ts"],
  coverageThreshold: {
    global: {
      branches: 100,
      functions: 100,
      lines: 100,
      statements: 100,
    },
  },
  errorOnDeprecated: true,
  moduleFileExtensions: [
    "web.js",
    "js",
    "web.ts",
    "ts",
    "web.tsx",
    "tsx",
    "json",
    "web.jsx",
    "jsx",
    "node",
  ],
  moduleNameMapper: {
    "^.+\\.module\\.(css|sass|scss)$": "identity-obj-proxy",
    "^react-native$": "react-native-web",
    ...pathsToModuleNameMapper(compilerOptions.paths, {
      prefix: "<rootDir>/",
    }),
  },
  modulePaths: [],
  preset: "ts-jest",
  resetMocks: true,
  rootDir: "./",
  roots: ["<rootDir>"],
  setupFiles: ["react-app-polyfill/jsdom"],
  setupFilesAfterEnv: ["<rootDir>/test/setup.tests.ts"],
  testEnvironment: "jsdom",
  testMatch: [
    "<rootDir>/src/**/__tests__/**/*.{js,jsx,ts,tsx}",
    "<rootDir>/src/**/*.{spec,test}.{js,jsx,ts,tsx}",
  ],
  transform: {
    "^(?!.*\\.(js|jsx|mjs|cjs|ts|tsx|css|json)$)": "<rootDir>/test/file.transform.js",
    "^.+\\.(js|jsx|mjs|cjs|ts|tsx)$": "<rootDir>/test/babel.transform.js",
    "^.+\\.css$": "<rootDir>/test/css.transform.js",
  },
  transformIgnorePatterns: [
    "[/\\\\]node_modules[/\\\\](?!url-join|.+\\.(js|jsx|mjs|cjs|ts|tsx))$",
    "^.+\\.module\\.(css|sass|scss)$",
  ],
  watchPlugins: ["jest-watch-typeahead/filename", "jest-watch-typeahead/testname"],
};
