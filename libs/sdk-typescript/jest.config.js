// Copyright Nightona Platforms Inc.
// SPDX-License-Identifier: Apache-2.0

/** @type {import('jest').Config} */
module.exports = {
  displayName: 'sdk-typescript',
  preset: '../../jest.preset.js',
  testEnvironment: 'node',
  transform: {
    '^.+\\.[tj]sx?$': [
      'ts-jest',
      {
        tsconfig: '<rootDir>/tsconfig.spec.json',
      },
    ],
  },
  moduleFileExtensions: ['ts', 'tsx', 'js', 'jsx'],
  roots: ['<rootDir>/src'],
  moduleNameMapper: {
    '^@nightona/api-client$': '<rootDir>/../api-client/src/index.ts',
    '^@nightona/toolbox-api-client$': '<rootDir>/../toolbox-api-client/src/index.ts',
    '^@nightona/sdk$': '<rootDir>/src/index.ts',
  },
  coverageDirectory: '../../coverage/libs/sdk-typescript',
}
