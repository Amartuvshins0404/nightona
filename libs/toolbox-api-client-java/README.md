# Nightona Toolbox API Client for Java

Auto-generated Java client for the [Nightona](https://daytona.io) Toolbox API (file system, process, git, LSP, and other sandbox-internal operations). This library is used internally by the [Nightona Java SDK](https://central.sonatype.com/artifact/io.nightona/sdk) and is not intended for direct use.

## Usage

If you're building applications with Nightona, use the [Nightona Java SDK](https://central.sonatype.com/artifact/io.nightona/sdk) instead — it provides a higher-level, idiomatic Java interface.

```kotlin
dependencies {
    implementation("io.nightona:sdk:<version>")
}
```

## Generation

This client is generated from the Nightona Toolbox OpenAPI specification using [OpenAPI Generator](https://openapi-generator.tech):

```bash
yarn nx run toolbox-api-client-java:generate:api-client
```

Do not edit the generated source files manually — changes will be overwritten on regeneration.

## License

Apache License 2.0 — see [LICENSE](https://github.com/Amartuvshins0404/nightona/blob/main/libs/sdk-java/LICENSE) for details.
