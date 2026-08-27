#!/usr/bin/env bash
# central-bundle-fix.sh — Fix Maven 4 central-publishing bundle for Central Portal upload
#
# Problem: central-publishing-maven-plugin 0.11.0 under Maven 4 produces bundles with:
#   1. *-consumer.pom* files (and their .md5/.sha1/.sha256/.sha512/.asc companions)
#   2. Main .pom files containing unresolved ${revision} placeholders
#
# This script fixes both issues: removes consumer poms, substitutes ${revision},
# recalculates checksums, and re-signs with GPG.
#
# Usage:
#   ./scripts/central-bundle-fix.sh <bundle-zip> <version> <output-zip>
#
# Arguments:
#   $1  Path to the original central-bundle.zip
#   $2  Release version (e.g., 3.0.x.20260831)
#   $3  Path for the fixed output zip
#
# Prerequisites:
#   - GPG key available (default: AF1B6E00, override via GPG_KEY_ID env var)
#   - Standard Unix tools: unzip, zip, sed, md5, shasum, gpg
#
# Example:
#   ./scripts/central-bundle-fix.sh target/central-publishing/central-bundle.zip 3.0.x.20260831 /tmp/central-bundle-fixed.zip

set -euo pipefail

BUNDLE_ZIP="${1:?Usage: $0 <bundle-zip> <version> <output-zip>}"
VERSION="${2:?Usage: $0 <bundle-zip> <version> <output-zip>}"
OUTPUT_ZIP="${3:?Usage: $0 <bundle-zip> <version> <output-zip>}"
GPG_KEY_ID="${GPG_KEY_ID:-AF1B6E00}"

# Validate inputs
if [[ ! -f "$BUNDLE_ZIP" ]]; then
    echo "ERROR: Bundle zip not found: $BUNDLE_ZIP" >&2
    exit 1
fi

if [[ "$VERSION" == *"-SNAPSHOT"* ]]; then
    echo "ERROR: Version must not contain -SNAPSHOT: $VERSION" >&2
    exit 1
fi

# Create temp working directory
WORK_DIR=$(mktemp -d)
trap 'rm -rf "$WORK_DIR"' EXIT

echo "=== Central Bundle Fix ==="
echo "  Input:  $BUNDLE_ZIP"
echo "  Version: $VERSION"
echo "  Output: $OUTPUT_ZIP"
echo "  GPG Key: $GPG_KEY_ID"
echo ""

# Step 1: Unzip bundle
echo "[1/5] Extracting bundle..."
unzip -q "$BUNDLE_ZIP" -d "$WORK_DIR"

# Step 2: Delete consumer.pom files and companions
echo "[2/5] Removing *-consumer.pom* files..."
CONSUMER_COUNT=$(find "$WORK_DIR" -name '*-consumer.pom*' | wc -l | tr -d ' ')
if [[ "$CONSUMER_COUNT" -gt 0 ]]; then
    find "$WORK_DIR" -name '*-consumer.pom*' -delete
    echo "  Removed $CONSUMER_COUNT consumer.pom files"
else
    echo "  No consumer.pom files found"
fi

# Step 3: Replace ${revision} in all .pom files
echo "[3/5] Substituting \${revision} -> $VERSION..."
POM_COUNT=0
while IFS= read -r -d '' pom; do
    if grep -q '${revision}' "$pom" 2>/dev/null; then
        sed -i '' "s|\\\${revision}|${VERSION}|g" "$pom"
        POM_COUNT=$((POM_COUNT + 1))
    fi
done < <(find "$WORK_DIR" -name '*.pom' -print0)
echo "  Updated $POM_COUNT pom files"

# Step 4: Recalculate checksums for all modified .pom files
echo "[4/5] Recalculating checksums and GPG signatures..."
FIXED_COUNT=0
while IFS= read -r -d '' pom; do
    # Remove old checksums and signatures
    rm -f "$pom.md5" "$pom.sha1" "$pom.sha256" "$pom.sha512" "$pom.asc" "$pom.sig"

    # Generate new checksums
    md5 -q "$pom" | tr -d '\n' > "$pom.md5"
    shasum -a 1   "$pom" | awk '{printf $1}' > "$pom.sha1"
    shasum -a 256 "$pom" | awk '{printf $1}' > "$pom.sha256"
    shasum -a 512 "$pom" | awk '{printf $1}' > "$pom.sha512"

    # GPG sign (explicit --output to produce .asc, not .sig)
    gpg --batch --yes --default-key "$GPG_KEY_ID" --output "$pom.asc" -b "$pom"

    FIXED_COUNT=$((FIXED_COUNT + 1))
done < <(find "$WORK_DIR" -name '*.pom' -print0)
echo "  Fixed checksums/signatures for $FIXED_COUNT pom files"

# Step 5: Repackage
echo "[5/5] Creating fixed bundle..."
(cd "$WORK_DIR" && zip -q -r "$OUTPUT_ZIP" .)

echo ""
echo "=== Done ==="
echo "Fixed bundle: $OUTPUT_ZIP"
echo "Consumer poms removed: $CONSUMER_COUNT"
echo "Pom files updated: $POM_COUNT"
echo "Checksums recalculated: $FIXED_COUNT"
