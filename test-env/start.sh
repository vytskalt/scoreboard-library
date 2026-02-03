#!/usr/bin/env bash

if ! command -v tmux >/dev/null 2>&1; then
  echo ERROR: tmux not found, install it >&2
  exit 67
fi

sesh="sl"

if tmux has-session -t "$sesh" 2>/dev/null; then
  echo already exists
  #tmux a -t "$sesh"
  exit 0
fi

tmux new-session -d -s "$sesh" -n "shell"

for dir in servers/*; do
  local base=$(basename $dir)

  echo $dir
  (cd "$dir"; tmux new-window -t "$sesh" -n "$base")

  local flags="-Xms512M -Xmx512M --add-modules=jdk.incubator.vector -XX:+UseG1GC -XX:+ParallelRefProcEnabled -XX:MaxGCPauseMillis=200 -XX:+UnlockExperimentalVMOptions -XX:+DisableExplicitGC -XX:+AlwaysPreTouch -XX:G1HeapWastePercent=5 -XX:G1MixedGCCountTarget=4 -XX:InitiatingHeapOccupancyPercent=15 -XX:G1MixedGCLiveThresholdPercent=90 -XX:G1RSetUpdatingPauseTimePercent=5 -XX:SurvivorRatio=32 -XX:+PerfDisableSharedMem -XX:MaxTenuringThreshold=1 -Dusing.aikars.flags=https://mcflags.emc.gs -Daikars.new.flags=true -XX:G1NewSizePercent=30 -XX:G1MaxNewSizePercent=40 -XX:G1HeapRegionSize=8M -XX:G1ReservePercent=20"

  tmux send-keys -t "$sesh:$base" "nix develop .#jdk25 -c sh -c 'java $flags -jar server.jar nogui'" C-m
done

tmux a -t "$sesh"
