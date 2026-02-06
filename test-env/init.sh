#!/usr/bin/env bash
set -e

create_server() {
  local name=$1
  local port=$2
  local nixshell=$3

  local root=$(pwd)

  local plugin_path=$(readlink -f ../test-plugin/build/libs/scoreboard-library-test-plugin-*-all.jar)
  local jar_path="$root/cache/$name.jar"

  local server_dir="servers/$name"
  mkdir -p "$server_dir/plugins"
  pushd "$server_dir" &> /dev/null

  echo "eula=true" > eula.txt
  printf "server-port=$port\ngamemode=creative\ndifficulty=peaceful\ngenerate-structures=false\nlevel-type=minecraft:flat" > server.properties
  cp "$root/bukkit.yml" .
  ln -sf "$plugin_path" plugins/test.jar
  ln -sf "$jar_path" server.jar

  local flags="-DPaper.IgnoreJavaVersion=true -Xms512M -Xmx512M -XX:+UseG1GC -XX:+ParallelRefProcEnabled -XX:MaxGCPauseMillis=200 -XX:+UnlockExperimentalVMOptions -XX:+DisableExplicitGC -XX:+AlwaysPreTouch -XX:G1HeapWastePercent=5 -XX:G1MixedGCCountTarget=4 -XX:InitiatingHeapOccupancyPercent=15 -XX:G1MixedGCLiveThresholdPercent=90 -XX:G1RSetUpdatingPauseTimePercent=5 -XX:SurvivorRatio=32 -XX:+PerfDisableSharedMem -XX:MaxTenuringThreshold=1 -Dusing.aikars.flags=https://mcflags.emc.gs -Daikars.new.flags=true -XX:G1NewSizePercent=30 -XX:G1MaxNewSizePercent=40 -XX:G1HeapRegionSize=8M -XX:G1ReservePercent=20"

  printf "#!/bin/sh\nnix develop .#$nixshell -c sh -c 'java $flags -jar server.jar nogui'" > start.sh
  chmod +x start.sh

  popd &> /dev/null
}

create_spigot_server() {
  local version=$1
  local port=$2
  local nixshell=$3

  if [ ! -f "cache/spigot-$version.jar" ]; then
    echo Running BuildTools for Spigot $version

    local root=$(pwd)

    pushd cache &> /dev/null

    mkdir -p "spigot-$version"
    pushd "spigot-$version" &> /dev/null
    nix develop ".#$nixshell" -c java -jar ../buildtools.jar --disable-java-check --nogui --rev "$version"

    if [ -f "$root/patches/spigot-$version-server.patch" ]; then
      echo Applying spigot-$version-server.patch
      pushd Spigot/Spigot-Server &> /dev/null
      git apply "$root/patches/spigot-$version-server.patch"
      popd &> /dev/null

      pushd Spigot &> /dev/null
      bash ../apache-maven-*/bin/mvn package
      mv Spigot-Server/target/spigot-*.jar "../spigot-$version.jar"
      popd &> /dev/null
    fi

    mv "spigot-$version.jar" ..
    popd &> /dev/null
    rm -rf "spigot-$version"

    popd &> /dev/null
  fi

  create_server "spigot-$version" "$port" "$nixshell"
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

download_file "buildtools.jar" "https://hub.spigotmc.org/jenkins/job/BuildTools/197/artifact/target/BuildTools.jar" "9a5b059ace22eaef28fb62f96970f0abe1490f06c860229b94627cd72eaf6ea5"

# --- 1.21.11 ---
download_file "paper-1.21.11.jar" "https://fill-data.papermc.io/v1/objects/4a558a00005d33dafa4c4d5f9e47b3bd47d92311fceccd9c9754ee6b913f8649/paper-1.21.11-100.jar" "4a558a00005d33dafa4c4d5f9e47b3bd47d92311fceccd9c9754ee6b913f8649"
create_server "paper-1.21.11" "25560" "jdk25"

create_spigot_server "1.21.11" "25561" "jdk25"

# --- 1.21.9 ---
download_file "paper-1.21.9.jar" "https://fill-data.papermc.io/v1/objects/aec002e77c7566e49494fdf05430b96078ffd1d7430e652d4f338fef951e7a10/paper-1.21.9-59.jar" "aec002e77c7566e49494fdf05430b96078ffd1d7430e652d4f338fef951e7a10"
create_server "paper-1.21.9" "25562" "jdk25"

# spigot doesnt have 1.21.9
create_spigot_server "1.21.10" "25563" "jdk25"

# --- 1.21.6 ---
download_file "paper-1.21.6.jar" "https://fill-data.papermc.io/v1/objects/35e2dfa66b3491b9d2f0bb033679fa5aca1e1fdf097e7a06a80ce8afeda5c214/paper-1.21.6-48.jar" "35e2dfa66b3491b9d2f0bb033679fa5aca1e1fdf097e7a06a80ce8afeda5c214"
create_server "paper-1.21.6" "25564" "jdk25"

create_spigot_server "1.21.6" "25565" "jdk21"

# --- 1.21.5 ---
download_file "paper-1.21.5.jar" "https://fill-data.papermc.io/v1/objects/2ae6ae22adf417699746e0f89fc2ef6cb6ee050a5f6608cee58f0535d60b509e/paper-1.21.5-114.jar" "2ae6ae22adf417699746e0f89fc2ef6cb6ee050a5f6608cee58f0535d60b509e"
create_server "paper-1.21.5" "25566" "jdk25"

create_spigot_server "1.21.5" "25567" "jdk21"

# --- 1.21.3 ---
download_file "paper-1.21.3.jar" "https://fill-data.papermc.io/v1/objects/87e973e1d338e869e7fdbc4b8fadc1579d7bb0246a0e0cf6e5700ace6c8bc17e/paper-1.21.3-83.jar" "87e973e1d338e869e7fdbc4b8fadc1579d7bb0246a0e0cf6e5700ace6c8bc17e"
create_server "paper-1.21.3" "25568" "jdk25"

create_spigot_server "1.21.3" "25569" "jdk21"

# --- 1.20.5 ---
download_file "paper-1.20.5.jar" "https://fill-data.papermc.io/v1/objects/3cd7da2f8df92e082a501a39c674aab3c0343edd179b86f5baccaebfc9974132/paper-1.20.5-22.jar" "3cd7da2f8df92e082a501a39c674aab3c0343edd179b86f5baccaebfc9974132"
create_server "paper-1.20.5" "25570" "jdk25"

# spigot doesn't have 1.20.5
create_spigot_server "1.20.6" "25571" "jdk21"

# --- 1.20.4 ---
download_file "paper-1.20.4.jar" "https://fill-data.papermc.io/v1/objects/e84aa4943cc51d7545b1c9b669bb1e0b143323d248ebb89012182f5554bc13d7/paper-1.20.4-499-mojang.jar" "e84aa4943cc51d7545b1c9b669bb1e0b143323d248ebb89012182f5554bc13d7"
create_server "paper-1.20.4" "25572" "jdk25"

create_spigot_server "1.20.4" "25573" "jdk21"

# --- 1.20.2 ---
download_file "paper-1.20.2.jar" "https://fill-data.papermc.io/v1/objects/07f57cdc5948dcf3cb3358e43b9a46fbbe8b595fa30c8749cfc102bf44897b69/paper-1.20.2-318-mojang.jar" "07f57cdc5948dcf3cb3358e43b9a46fbbe8b595fa30c8749cfc102bf44897b69"
create_server "paper-1.20.2" "25574" "jdk25"

create_spigot_server "1.20.2" "25575" "jdk21"

# --- 1.17 ---
download_file "paper-1.17.jar" "https://fill-data.papermc.io/v1/objects/760a93b94a58d619bd647d71af84688617d0444d22b716500bc6b343858dc871/paper-1.17-79.jar" "760a93b94a58d619bd647d71af84688617d0444d22b716500bc6b343858dc871"
create_server "paper-1.17" "25576" "jdk25"

create_spigot_server "1.17" "25577" "jdk17"
