#!/bin/bash

# Clean old execution socket
rm -f /tmp/gh-ost.testdb.*.sock

set -x

./gh-ost \
  --host localhost \
  --database testdb \
  --user root \
  --password root \
  --allow-on-master \
  --initially-drop-ghost-table \
  --postpone-cut-over-flag-file=/tmp/ghost.cut \
  "$@"
