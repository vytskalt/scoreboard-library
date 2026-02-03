#!/usr/bin/env bash
set -e

create_server() {
  local name=$1
  local port=$2

  local root=$(pwd)

  local plugin_path=$(readlink -f ../test-plugin/build/libs/scoreboard-library-test-plugin-*-all.jar)
  local jar_path="$root/cache/$name.jar"

  local server_dir="servers/$name"
  mkdir -p "$server_dir/plugins"
  cd "$server_dir"

  echo "eula=true" > eula.txt
  printf "server-port=$port\ngamemode=creative\ndifficulty=peaceful\ngenerate-structures=false\nlevel-type=minecraft:flat" > server.properties
  cp "$root/bukkit.yml" .
  ln -s "$plugin_path" plugins/test.jar
  ln -s "$jar_path" server.jar
}

download_file() {
  local name=$1
  local url=$2
  local expected_sha256_hash=$3

  local path="cache/$name"
  mkdir -p cache

  if [ ! -f "$path" ]; then
    echo "Downloading $name..."
    curl --silent --location --output "$path" "$url"
  fi

  local actual_hash=$(sha256sum "$path" | cut -f 1 -d ' ')
  if [ "$expected_sha256_hash" != "$actual_hash" ]; then
    echo "ERROR: hashes for $name do not match! expected: $expected_sha256_hash, but got $actual_hash"
    rm "$path"
    exit 1
  fi
}

download_file "paper-1.21.11.jar" "https://fill-data.papermc.io/v1/objects/4a558a00005d33dafa4c4d5f9e47b3bd47d92311fceccd9c9754ee6b913f8649/paper-1.21.11-100.jar" "4a558a00005d33dafa4c4d5f9e47b3bd47d92311fceccd9c9754ee6b913f8649"
create_server "paper-1.21.11" "25560"

