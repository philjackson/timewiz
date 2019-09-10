#!/usr/bin/env bash

set -e

mkdir -p tmp
cd tmp

wget "https://raw.githubusercontent.com/moment/moment-timezone/develop/data/unpacked/latest.json"
wget "http://download.geonames.org/export/dump/cities15000.zip"

unzip cities15000.zip
rm cities15000.zip

cat cities15000.txt | awk -F '\t' '{print $18 "," $2}' > ../cities15000.txt
mv latest.json ..

cd ..
rm -rf tmp

./indexer.cljs
