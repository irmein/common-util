#!/bin/bash

# Detect if the script is being sourced or executed
(return 0 2>/dev/null) && SOURCED=1 || SOURCED=0

echo "--- NVD API Key Setup ---"

# Check if key is provided as argument or prompt interactively
if [ -n "$1" ]; then
    nvdkey="$1"
else
    read -p "Enter your NVD API Key: " nvdkey
fi

# Trim whitespace
nvdkey=$(echo "$nvdkey" | xargs)

if [ -z "$nvdkey" ]; then
    echo "❌ Error: NVD API Key cannot be empty."
    if [ "$SOURCED" -eq 1 ]; then
        return 1
    else
        exit 1
    fi
fi

# Detect current user shell configuration file
SHELL_NAME=$(basename "${SHELL:-zsh}")
SHELL_RC=""

if [ "$SHELL_NAME" = "zsh" ]; then
    SHELL_RC="$HOME/.zshrc"
elif [ "$SHELL_NAME" = "bash" ]; then
    if [ -f "$HOME/.bash_profile" ]; then
        SHELL_RC="$HOME/.bash_profile"
    else
        SHELL_RC="$HOME/.bashrc"
    fi
else
    # Fallback detection
    if [ -f "$HOME/.zshrc" ]; then
        SHELL_RC="$HOME/.zshrc"
    elif [ -f "$HOME/.bash_profile" ]; then
        SHELL_RC="$HOME/.bash_profile"
    elif [ -f "$HOME/.bashrc" ]; then
        SHELL_RC="$HOME/.bashrc"
    fi
fi

# Set for current shell session
export NVD_API_KEY="$nvdkey"

# Attempt to persist to shell RC file if detected and writable
PERSISTED=0
if [ -n "$SHELL_RC" ]; then
    if touch "$SHELL_RC" 2>/dev/null && [ -w "$SHELL_RC" ]; then
        if grep -q "export NVD_API_KEY=" "$SHELL_RC" 2>/dev/null; then
            if [[ "$OSTYPE" == "darwin"* ]]; then
                sed -i '' "s|export NVD_API_KEY=.*|export NVD_API_KEY=\"$nvdkey\"|" "$SHELL_RC" 2>/dev/null && PERSISTED=1
            else
                sed -i "s|export NVD_API_KEY=.*|export NVD_API_KEY=\"$nvdkey\"|" "$SHELL_RC" 2>/dev/null && PERSISTED=1
            fi
            if [ "$PERSISTED" -eq 1 ]; then
                echo "🔄 Updated existing NVD_API_KEY in $SHELL_RC"
            fi
        else
            if printf "\n# NVD API Key for OWASP Dependency-Check (\${env.NVD_API_KEY})\nexport NVD_API_KEY=\"%s\"\n" "$nvdkey" >> "$SHELL_RC" 2>/dev/null; then
                PERSISTED=1
                echo "✅ Added NVD_API_KEY to $SHELL_RC"
            fi
        fi
    fi
fi

if [ "$PERSISTED" -eq 1 ]; then
    if [ "$SOURCED" -eq 1 ]; then
        echo "🚀 NVD_API_KEY is active in this current shell session!"
    else
        echo "💡 Run 'source $SHELL_RC' or restart your terminal to activate it in existing open sessions."
    fi
else
    echo "ℹ️  Exported NVD_API_KEY for the current session."
    echo "To make it permanent in your shell, add this line to your profile ($SHELL_RC):"
    echo "    export NVD_API_KEY=\"$nvdkey\""
fi

echo ""
echo "✅ Configuration complete! Maven will read this via \${env.NVD_API_KEY}."
