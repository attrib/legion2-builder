#!/usr/bin/env bash

git fetch
git rebase origin master

docker build -f Dockerfile-steamcmd . -t attrib/steamcmd

docker run -i --rm -v `pwd`/data:/data -v `pwd`/steam:/root/Steam attrib/steamcmd \
    +@sSteamCmdForcePlatformType windows \
    +login $1 $2 \
    +force_install_dir /data/legion2-td \
    +app_update 469600 validate \
    +quit

docker build -f Dockerfile-apigen . -t attrib/legiontd2-api-gen

docker run -i --rm -v `pwd`:/home/gradle -v `pwd`/.gradle:/home/gradle/.gradle attrib/legiontd2-api-gen \
     /bin/bash -c "gradle api-gen:jar && java -jar api-gen/build/libs/api-gen.jar ./data/legion2-td/"

git diff-index --quiet HEAD api/src/main/kotlin/ltd2/ltd2.kt
RETURN=$?

if [ $RETURN -ne 0 ]; then
    git add api/src/main/kotlin/ltd2/ltd2.kt
    git commit -m "Updated to newest version (via autoupdater)"
    git push origin master
fi
