#!/usr/bin/env bash

set -e

pushd index-builder
./start.bash
mv index.json ../../resources/public
popd

pushd tz-splitter
./process-tz.js
rm -rf ../../resources/public/tz
mv tz ../../resources/public
popd
