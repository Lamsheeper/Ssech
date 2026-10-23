#!/usr/bin/env bash
# Build and launch Ssech. Requires only a JDK (17+).
#   ./play.sh        build and play
#   ./play.sh test   build and run the test suite
set -euo pipefail

ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
BUILD="$ROOT/build"
MIN_JAVA=17

die() { echo "error: $*" >&2; exit 1; }

command -v javac >/dev/null 2>&1 || die "javac not found. Install a JDK $MIN_JAVA or newer (e.g. 'brew install openjdk' or https://adoptium.net)."
command -v java >/dev/null 2>&1 || die "java not found. Install a JDK $MIN_JAVA or newer."

version="$(javac -version 2>&1 | sed -E 's/^javac ([0-9]+).*/\1/')"
[[ "$version" =~ ^[0-9]+$ ]] || die "could not determine javac version from: $(javac -version 2>&1)"
(( version >= MIN_JAVA )) || die "JDK $MIN_JAVA or newer is required (found $version)."

compile() {
  local out="$1"; shift
  rm -rf "$out"
  mkdir -p "$out"
  local sources=()
  while IFS= read -r -d '' f; do sources+=("$f"); done < <(find "$@" -name '*.java' -print0)
  javac -encoding UTF-8 -d "$out" "${sources[@]}"
}

case "${1:-play}" in
  play)
    compile "$BUILD/classes" "$ROOT/src/main/java"
    cp -R "$ROOT/src/main/resources/." "$BUILD/classes/"
    exec java -cp "$BUILD/classes" ssech.Main
    ;;
  test)
    compile "$BUILD/test-classes" "$ROOT/src/main/java" "$ROOT/src/test/java"
    cp -R "$ROOT/src/main/resources/." "$BUILD/test-classes/"
    exec java -ea -cp "$BUILD/test-classes" ssech.TestRunner
    ;;
  *)
    echo "usage: $0 [play|test]" >&2
    exit 2
    ;;
esac
