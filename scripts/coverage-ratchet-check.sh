#!/usr/bin/env bash
# coverage-ratchet-check.sh — Per-module JaCoCo coverage ratchet report
#
# Reads each module's JaCoCo CSV report and outputs a table showing:
#   module | current LINE% | gate minimum | gap to 0.90 target
#
# Run after `mvn clean verify` (jacoco.exec and CSV reports must exist).
#
# Usage:
#   ./scripts/coverage-ratchet-check.sh [target-coverage]
#
# Arguments:
#   $1  Target coverage (default: 0.90)
#
# Example:
#   ./scripts/coverage-ratchet-check.sh          # default 0.90 target
#   ./scripts/coverage-ratchet-check.sh 0.95     # custom 0.95 target

set -euo pipefail

TARGET="${1:-0.90}"
REPO_ROOT="$(cd "$(dirname "$0")/.." && pwd)"

# Find all jacoco.csv files
CSV_FILES=$(find "$REPO_ROOT" -path "*/target/site/jacoco/jacoco.csv" 2>/dev/null)

if [[ -z "$CSV_FILES" ]]; then
    echo "ERROR: No JaCoCo CSV reports found. Run 'mvn clean verify' first." >&2
    exit 1
fi

# Print header
printf "%-40s %12s %12s %12s\n" "MODULE" "LINE%" "GATE" "GAP"
printf "%-40s %12s %12s %12s\n" "$(printf '%.0s-' {1..40})" "$(printf '%.0s-' {1..12})" "$(printf '%.0s-' {1..12})" "$(printf '%.0s-' {1..12})"

TOTAL_MISSED=0
TOTAL_COVERED=0
GATE_MIN="0.92"  # Current BUNDLE gate from pom.xml

while IFS= read -r csv; do
    # Extract module name from path (e.g., .../easydoc-core/target/...)
    MODULE=$(echo "$csv" | sed -n 's|.*/\([^/]*\)/target/.*|\1|p')

    # Parse JaCoCo CSV: GROUP,PACKAGE,CLASS,INSTRUCTION_MISSED,INSTRUCTION_COVERED,
    #   BRANCH_MISSED,BRANCH_COVERED,LINE_MISSED,LINE_COVERED,COMPLEXITY_MISSED,...
    # Skip header, sum LINE_MISSED and LINE_COVERED across all rows
    LINE_MISSED=$(tail -n +2 "$csv" | awk -F',' '{sum += $8} END {print sum+0}')
    LINE_COVERED=$(tail -n +2 "$csv" | awk -F',' '{sum += $9} END {print sum+0}')

    TOTAL_MISSED=$((TOTAL_MISSED + LINE_MISSED))
    TOTAL_COVERED=$((TOTAL_COVERED + LINE_COVERED))

    TOTAL_LINES=$((LINE_MISSED + LINE_COVERED))
    if [[ "$TOTAL_LINES" -gt 0 ]]; then
        LINE_PCT=$(awk "BEGIN {printf \"%.4f\", $LINE_COVERED / $TOTAL_LINES}")
        LINE_DISPLAY=$(awk "BEGIN {printf \"%.2f%%\", $LINE_COVERED / $TOTAL_LINES * 100}")
    else
        LINE_PCT="0.0000"
        LINE_DISPLAY="N/A"
    fi

    # Calculate gap to target
    GAP=$(awk "BEGIN {g = $TARGET - $LINE_PCT; if (g < 0) g = 0; printf \"%.2f%%\", g * 100}")

    # Mark modules below gate
    BELOW_GATE=$(awk "BEGIN {print ($LINE_PCT < $GATE_MIN) ? \" <<<\" : \"\"}")

    printf "%-40s %12s %12s %12s%s\n" "$MODULE" "$LINE_DISPLAY" "$GATE_MIN" "$GAP" "$BELOW_GATE"
done <<< "$CSV_FILES"

# Print aggregate
printf "%-40s %12s %12s %12s\n" "$(printf '%.0s-' {1..40})" "$(printf '%.0s-' {1..12})" "$(printf '%.0s-' {1..12})" "$(printf '%.0s-' {1..12})"

TOTAL_ALL=$((TOTAL_MISSED + TOTAL_COVERED))
if [[ "$TOTAL_ALL" -gt 0 ]]; then
    AGG_PCT=$(awk "BEGIN {printf \"%.4f\", $TOTAL_COVERED / $TOTAL_ALL}")
    AGG_DISPLAY=$(awk "BEGIN {printf \"%.2f%%\", $TOTAL_COVERED / $TOTAL_ALL * 100}")
    AGG_GAP=$(awk "BEGIN {g = $TARGET - $AGG_PCT; if (g < 0) g = 0; printf \"%.2f%%\", g * 100}")
else
    AGG_DISPLAY="N/A"
    AGG_GAP="N/A"
fi

printf "%-40s %12s %12s %12s\n" "AGGREGATE" "$AGG_DISPLAY" "$GATE_MIN" "$AGG_GAP"

echo ""
echo "Gate minimum: $GATE_MIN (BUNDLE-level, from pom.xml jacoco check)"
echo "Ratchet target: $TARGET (per-release tightening goal)"
echo "Modules marked '<<<' are below the current gate."
