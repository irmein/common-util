# GitHub Release Plan

This document outlines the strategy and implementation plan for automating project releases for the `data-morph` (`common-util`) project using GitHub Actions and GitHub Releases.

## 1. Release Strategy & Trigger

We will adopt a **Tag-based Release Strategy**. 
- A release will be automatically triggered whenever a developer pushes a Git tag that starts with `v` (e.g., `v1.0.0`, `v1.0.1`).
- This ensures that code pushed to `main` is not automatically released until explicitly tagged, giving developers control over the release cadence.

## 2. Versioning Scheme

We will follow **Semantic Versioning (SemVer)**: `Major.Minor.Patch` (e.g., `1.0.0`).
- **Major:** Breaking changes or significant new features.
- **Minor:** Backward-compatible new features.
- **Patch:** Backward-compatible bug fixes.

### Current State
Currently, the project is versioned as `1.0-SNAPSHOT` in `pom.xml` and `App.java`. 

### Release Process
1. Before tagging a release, manually update `pom.xml` and `App.java` to the target release version (e.g., `1.0.0`) and remove the `-SNAPSHOT` suffix.
2. Commit and push these changes to `main`.
3. Create and push the Git tag (e.g., `v1.0.0`).
4. After the release is successful, bump the version in `pom.xml` and `App.java` to the next development snapshot (e.g., `1.0.1-SNAPSHOT`) and push to `main`.

## 3. GitHub Actions Release Workflow (`.github/workflows/release.yml`)

We will create a GitHub Actions workflow with the following steps:

1. **Trigger Definition:**
   ```yaml
   on:
     push:
       tags:
         - 'v*'
   ```
2. **Environment Setup:** Check out the repository and configure Java (Eclipse Temurin 25).
3. **Build & Test:** Run `mvn clean package` to execute all tests and build the fat JAR (`target/data-morph-<version>.jar`) using the `maven-shade-plugin`.
4. **Create GitHub Release:** Use the `softprops/action-gh-release` action. This will create a Release draft or published release on the GitHub repository page, automatically utilizing the pushed tag.
5. **Upload Artifacts:** Configure the release action to upload the compiled fat JAR as a downloadable asset attached to the release. This allows users to easily download the executable without building it from source.

## 4. Changelog Generation

We will use GitHub's **Automatically generated release notes** feature. 
- When the GitHub Action creates the release, it can be configured to auto-generate the changelog based on the Pull Requests merged since the previous release tag.
- This provides users with a clear overview of what changes are included in the new executable.

## Next Steps for Implementation

1. Create the `.github/workflows/release.yml` file with the configuration described above.
2. Prepare the codebase for the initial `v1.0.0` release by standardizing versions across `pom.xml` and `App.java`.
3. Perform a test release by pushing a tag.
