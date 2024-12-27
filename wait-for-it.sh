#!/bin/bash
# wait-for-it.sh

set -e

MYSQL_HOST="mysql"
MYSQL_PORT="3306"

until mysql -h"$MYSQL_HOST" -P"$MYSQL_PORT" -uroot -proot -e 'SELECT 1'; do
  >&2 echo "MySQL is unavailable - sleeping"
  sleep 1
done

>&2 echo "MySQL is up - executing command"
exec java -jar app.jar 