#!/usr/bin/env bash

serverDir=servers/paper-1.21.11
pluginPath=$(readlink -f ../test-plugin/build/libs/scoreboard-library-test-plugin-*-all.jar)

mkdir -p $serverDir/plugins
echo "eula=true" > $serverDir/eula.txt
echo "level-type=minecraft:flat" > $serverDir/server.properties
ln -s $pluginPath $serverDir/plugins/test.jar
curl -L --output $serverDir/server.jar https://fill-data.papermc.io/v1/objects/4a558a00005d33dafa4c4d5f9e47b3bd47d92311fceccd9c9754ee6b913f8649/paper-1.21.11-100.jar
