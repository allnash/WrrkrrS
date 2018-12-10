#!/bin/bash
rm -rf target
./build_public_directory.sh
cp -r public_dev/images public
git fetch --all && git pull
cp conf/application.conf.production conf/application.conf
cp conf/logback.xml.production conf/logback.xml
echo "+ APP READY FOR STAGING +"
echo "+ ATTEMPTING TO BUILD CURRENT .TAR FOR UNIVERSAL SYSTEMS+"
./sbt universal:packageBin
echo "+ STAGING BUILD COMPLETED +"
