#!/usr/bin/env bash
# ─────────────────────────────────────────────────────────
#  Build & Run CPy IDE
# ─────────────────────────────────────────────────────────

set -e

SRC_DIR="src"
OUT_DIR="out"

echo "🔨 Compiling CPy IDE..."

mkdir -p "$OUT_DIR"

# Collect all .java files
find "$SRC_DIR" -name "*.java" > sources.txt

javac -d "$OUT_DIR" @sources.txt

rm sources.txt

echo "✅ Build successful."
echo "🚀 Launching CPy IDE..."

java -cp "$OUT_DIR" main.Main