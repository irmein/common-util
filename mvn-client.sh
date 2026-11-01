#!/usr/bin/env bash

# mvn-client.sh - Convenient wrapper client for Maven and OWASP NVD scanning
# Ensures Java, SDKMAN, and NVD_API_KEY environment variables are properly initialized.

# 1. Resolve JAVA_HOME if unset or pointing to a non-existent directory
if [ -z "$JAVA_HOME" ] || [ ! -d "$JAVA_HOME" ]; then
    if [ -d "$HOME/.sdkman/candidates/java/current" ]; then
        export JAVA_HOME="$HOME/.sdkman/candidates/java/current"
        export PATH="$JAVA_HOME/bin:$PATH"
    elif [ -s "$HOME/.sdkman/bin/sdkman-init.sh" ]; then
        # shellcheck disable=SC1090
        source "$HOME/.sdkman/bin/sdkman-init.sh" >/dev/null 2>&1 || true
    elif command -v /usr/libexec/java_home >/dev/null 2>&1; then
        DETECTED_JAVA=$(/usr/libexec/java_home 2>/dev/null || true)
        if [ -n "$DETECTED_JAVA" ] && [ -d "$DETECTED_JAVA" ]; then
            export JAVA_HOME="$DETECTED_JAVA"
            export PATH="$JAVA_HOME/bin:$PATH"
        fi
    fi
fi

# 2. Check and load NVD API Key if not already exported in current shell
check_nvd_key() {
    if [ -z "$NVD_API_KEY" ]; then
        # Check ~/.zshrc or ~/.bashrc for existing definition
        if [ -f "$HOME/.zshrc" ]; then
            KEY_FROM_RC=$(grep -E '^\s*export NVD_API_KEY=' "$HOME/.zshrc" | tail -n 1 | cut -d'=' -f2 | tr -d '"'"'")
            if [ -n "$KEY_FROM_RC" ]; then
                export NVD_API_KEY="$KEY_FROM_RC"
            fi
        fi
    fi

    if [ -z "$NVD_API_KEY" ]; then
        echo "⚠️  NVD_API_KEY is not set."
        read -r -p "Do you want to configure it now? (y/N): " answer
        if [[ "$answer" =~ ^[Yy]$ ]]; then
            if [ -f "./set-nvd-key.sh" ]; then
                # shellcheck disable=SC1091
                source ./set-nvd-key.sh
            fi
        fi
    fi
}

open_report() {
    local REPORT_FILE="target/dependency-check-report.html"
    if [ -f "$REPORT_FILE" ]; then
        echo "📄 Opening report: $REPORT_FILE"
        if command -v open >/dev/null 2>&1; then
            open "$REPORT_FILE"
        elif command -v xdg-open >/dev/null 2>&1; then
            xdg-open "$REPORT_FILE"
        else
            echo "Report generated at: $(pwd)/$REPORT_FILE"
        fi
    else
        echo "❌ Report not found at $REPORT_FILE. Run a scan first."
    fi
}

run_scan() {
    check_nvd_key
    echo "🔍 Running OWASP NVD Dependency Scan (Build fails if CVSS >= 7)..."
    mvn dependency-check:check "$@"
}

run_scan_report_only() {
    check_nvd_key
    echo "📊 Running OWASP NVD Scan for report generation (Build won't fail)..."
    mvn dependency-check:check -DfailBuildOnCVSS=11 "$@"
    open_report
}

# 3. Direct argument execution
if [ "$#" -gt 0 ]; then
    case "$1" in
        scan|nvd)
            shift
            run_scan "$@"
            ;;
        report|scan-report)
            shift
            run_scan_report_only "$@"
            ;;
        open-report|view)
            open_report
            ;;
        build|package)
            shift
            mvn clean package "$@"
            ;;
        test)
            shift
            mvn test "$@"
            ;;
        *)
            # Forward directly to mvn
            mvn "$@"
            ;;
    esac
    exit 0
fi

# 4. Interactive menu if run without arguments
echo "=============================================="
echo "          Maven & NVD Scanner Client          "
echo "=============================================="
echo "1) Run NVD Vulnerability Scan (mvn dependency-check:check)"
echo "2) Generate Vulnerability Report (Report only, no build failure)"
echo "3) Open Vulnerability HTML Report"
echo "4) Build Project (mvn clean package)"
echo "5) Run Tests (mvn test)"
echo "6) Custom Maven Command"
echo "7) Exit"
echo "=============================================="
read -r -p "Enter choice [1-7]: " choice

case "$choice" in
    1)
        run_scan
        ;;
    2)
        run_scan_report_only
        ;;
    3)
        open_report
        ;;
    4)
        mvn clean package
        ;;
    5)
        mvn test
        ;;
    6)
        read -r -p "Enter maven goals/args: " custom_args
        # shellcheck disable=SC2086
        mvn $custom_args
        ;;
    7)
        echo "Goodbye!"
        exit 0
        ;;
    *)
        echo "Invalid option."
        exit 1
        ;;
esac
