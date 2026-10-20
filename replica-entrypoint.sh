
#!/bin/bash
set -e

DATA=/var/lib/postgresql/data

# 1. Wait for the primary
until pg_isready -h primary -U replicator; do
  echo "waiting for primary..."
  sleep 2
done

# 2. Clone the primary only if the data directory is empty
if [ -z "$(ls -A "$DATA")" ]; then
  echo "empty data dir, taking base backup"

  pg_basebackup \
    -h primary \
    -p 5432 \
    -U replicator \
    -D "$DATA" \
    -Fp \
    -Xs \
    -P \
    -R \
    -S replica_1
fi

# 3. Set permissions and start PostgreSQL
chown -R postgres:postgres "$DATA"
chmod 700 "$DATA"

exec gosu postgres postgres
