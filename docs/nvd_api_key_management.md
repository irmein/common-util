# NVD API Key Security and Management

The National Vulnerability Database (NVD) API key is required to accelerate dependency vulnerability scanning (e.g., using the OWASP Dependency-Check Maven plugin) and prevent rate-limiting. As a sensitive credential, the API key must be managed securely to prevent unauthorized access.

This document outlines the recommended ways to securely store and use the NVD API key across different environments.

## 1. Environment Variables (Local Development & CI)

The most universal and secure way to handle secrets is through environment variables. This prevents hardcoding the key in configuration files.

### How to configure:
**macOS/Linux:**
Add the following to your `~/.zshrc` or `~/.bash_profile`:
```bash
export NVD_API_KEY="your_api_key_here"
```

**Windows (PowerShell):**
```powershell
[System.Environment]::SetEnvironmentVariable('NVD_API_KEY', 'your_api_key_here', 'User')
```

### Usage in Maven:
In your `pom.xml`, reference the environment variable within the `dependency-check-maven` plugin configuration:
```xml
<plugin>
    <groupId>org.owasp</groupId>
    <artifactId>dependency-check-maven</artifactId>
    <version>10.0.2</version> <!-- Use the latest version -->
    <configuration>
        <nvdApiKey>${env.NVD_API_KEY}</nvdApiKey>
    </configuration>
</plugin>
```

## 2. Maven `settings.xml` (Local Development)

If you prefer to keep Maven-specific secrets within Maven's ecosystem, you can store the key in your local `~/.m2/settings.xml` file.

### How to configure:
Edit or create `~/.m2/settings.xml`:
```xml
<settings>
    <profiles>
        <profile>
            <id>nvd-config</id>
            <properties>
                <nvd.api.key>your_api_key_here</nvd.api.key>
            </properties>
        </profile>
    </profiles>
    <activeProfiles>
        <activeProfile>nvd-config</activeProfile>
    </activeProfiles>
</settings>
```

### Usage in Maven:
Reference the property in your `pom.xml`:
```xml
<configuration>
    <nvdApiKey>${nvd.api.key}</nvdApiKey>
</configuration>
```
> **Note:** Ensure your `~/.m2/settings.xml` file has strict file permissions (`chmod 600 ~/.m2/settings.xml`) to prevent other users on the same machine from reading it.

## 3. GitHub Actions Secrets (CI/CD)

For automated builds and CI/CD pipelines, the API key must never be checked into the repository. Instead, use the CI platform's secrets manager.

### How to configure in GitHub:
1. Navigate to your repository on GitHub.
2. Go to **Settings** > **Secrets and variables** > **Actions**.
3. Click **New repository secret**.
4. Name the secret `NVD_API_KEY` and paste your key in the value field.

### Usage in GitHub Actions Workflow (`.github/workflows/maven.yml`):
Pass the secret as an environment variable to the Maven build step:
```yaml
jobs:
  build:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v4
      - name: Set up JDK
        uses: actions/setup-java@v4
        with:
          java-version: '25'
          distribution: 'temurin'
      - name: Build and run dependency check
        env:
          NVD_API_KEY: ${{ secrets.NVD_API_KEY }}
        run: mvn verify dependency-check:check
```

## 4. Local Properties File (Alternative for Local Dev)

You can use a local properties file to load configuration, provided the file is **strictly ignored** by Git.

### How to configure:
1. Create a file named `nvd-secrets.properties` in the project root.
2. Add your key: `nvd.api.key=your_api_key_here`.
3. **Crucial:** Add `nvd-secrets.properties` to your `.gitignore` file immediately.

### Usage in Maven:
Use the Properties Maven Plugin to load this file during the build, allowing you to reference `${nvd.api.key}`. However, using Environment Variables or `settings.xml` is generally preferred over this method to avoid accidental commits.

---

## 🔒 Security Best Practices Summary
- **Never commit your NVD API key to Git.**
- Use `.gitignore` for any local files containing secrets.
- Use CI/CD secret management tools (like GitHub Secrets) for build pipelines.
- Rotate the API key immediately if you suspect it has been compromised.
