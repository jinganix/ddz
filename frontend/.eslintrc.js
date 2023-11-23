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

module.exports = {
  env: {
    es6: true,
    jest: true,
    node: true,
  },
  extends: [
    "plugin:@typescript-eslint/recommended",
    "plugin:prettier/recommended",
    "plugin:react/recommended",
  ],
  parser: "@typescript-eslint/parser",
  plugins: ["import", "import-quotes", "no-null", "sort-keys-fix", "react"],
  root: true,
  rules: {
    "@typescript-eslint/camelcase": "off",
    "@typescript-eslint/member-delimiter-style": "off",
    "@typescript-eslint/no-use-before-define": "off",
    "@typescript-eslint/semi": "off",
    "import-quotes/import-quotes": [1, "double"],
    "import/newline-after-import": "error",
    "import/order": [
      "error",
      {
        groups: [["builtin", "external"], "internal", ["index", "parent", "sibling"]],
      },
    ],
    "max-len": [
      "error",
      {
        code: 100,
        ignoreStrings: true,
        ignoreUrls: true,
      },
    ],
    "require-jsdoc": [
      "error",
      {
        require: {
          ArrowFunctionExpression: false,
          ClassDeclaration: false,
          FunctionDeclaration: false,
          FunctionExpression: false,
          MethodDefinition: false,
        },
      },
    ],
    semi: "off",
    "sort-keys": ["error"],
    "sort-keys-fix/sort-keys-fix": "warn",
  },
};
