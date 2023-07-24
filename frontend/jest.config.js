// eslint-disable-next-line @typescript-eslint/no-var-requires
const _require = require;
const { pathsToModuleNameMapper } = _require("ts-jest");
const { compilerOptions } = _require("./tsconfig");

module.exports = {
  bail: true,
  collectCoverage: true,
  collectCoverageFrom: ["src/**/*.ts"],
  coverageThreshold: {
    global: {
      branches: 100,
      functions: 100,
      lines: 100,
      statements: 100,
    },
  },
  errorOnDeprecated: true,
  moduleNameMapper: pathsToModuleNameMapper(compilerOptions.paths, {
    prefix: "<rootDir>/",
  }),
  preset: "ts-jest",
  rootDir: "./",
  testEnvironment: "node",
  testMatch: ["<rootDir>/src/**/*(*.)@(test).ts"],
};
