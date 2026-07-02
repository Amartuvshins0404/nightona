# Packaging Guidelines for Nightona

The Nightona team appreciates any efforts to make our software more accessible to users on various platforms.

## Critical Naming Guideline

**Important**: While you are free to package and distribute our software, you **MUST NOT** name your package `nightona` or, in any way, suggest that, the package you distribute, is an official distribution of `nightona`. This restriction is to prevent confusion and maintain the integrity of our project identity.

- Acceptable: "unofficial-nightona-package", "unofficial-nightona-distribution", etc.
- Not Acceptable: "nightona", "official-nightona", etc.

## General Guidelines

1. **License Compliance**: Ensure that the AGPL 3.0/Apache 2.0 license is included with the package and that all copyright notices are preserved.

2. **Version Accuracy**: Use the exact version number of Nightona that you are packaging. Do not modify the version number or add custom suffixes without explicit permission.

3. **Dependencies**: Include all necessary dependencies as specified in our project documentation. Do not add extra dependencies without consulting the project maintainers.

4. **Modifications**: If you need to make any modifications to the source code for packaging purposes, please document these changes clearly and consider submitting them as pull requests to the main project.

5. **Standard Note**: Please include the following standard note in your package description or metadata:

   ```
   This package contains an unofficial distribution of Nightona,
   developed by Nightona Platforms Inc. This package is not officially supported or endorsed
   by the Nightona project. For the official version, please visit https://github.com/Amartuvshins0404/nightona.
   ```

## Feedback and Questions

If you have any questions about packaging Nightona or need clarification on these guidelines, especially regarding naming conventions, please open an issue in our GitHub repository.

We appreciate your contribution to making Nightona more accessible to users across different platforms, while respecting our project's identity!
