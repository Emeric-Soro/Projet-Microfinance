#!/bin/sh
set -eu

WORKSPACE_DIR="/opt/cloudbeaver/workspace"
GLOBAL_DBEAVER_DIR="$WORKSPACE_DIR/GlobalConfiguration/.dbeaver"
SOURCE_DATASOURCES="/opt/cloudbeaver/conf/initial-data-sources.conf"
TARGET_DATASOURCES="$GLOBAL_DBEAVER_DIR/data-sources.json"

mkdir -p "$GLOBAL_DBEAVER_DIR" "$WORKSPACE_DIR/.data"

# Re-apply the canonical datasource config at every startup.
if [ -f "$SOURCE_DATASOURCES" ]; then
  cp "$SOURCE_DATASOURCES" "$TARGET_DATASOURCES"
fi

# Remove ALL persisted H2 databases and credentials to prevent stale logins.
rm -f "$WORKSPACE_DIR/.data/.database-credentials.dat"
rm -f "$WORKSPACE_DIR/.data/cb.h2v2.dat.mv.db" "$WORKSPACE_DIR/.data/cb.h2v2.dat.trace.db"
# Also remove any other H2 files (CBSettings, security DB) that can cause "no password" errors
rm -f "$WORKSPACE_DIR/.data/CBSettings.h2.mv.db" "$WORKSPACE_DIR/.data/CBSettings.h2.trace.db"
rm -f "$WORKSPACE_DIR/.data/security.h2.mv.db" "$WORKSPACE_DIR/.data/security.h2.trace.db"

exec /opt/cloudbeaver/launch-product.sh
