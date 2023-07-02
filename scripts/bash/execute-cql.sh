#!/bin/bash
USER_NAME='cassandra'
PASSWORD='cassandra'


for cql_file in ./tmp/cql/*.cql;
do
  cqlsh review-service-cassandra -u "${USER_NAME}" -p "${PASSWORD}" -f "${cql_file}" ;
  echo "Script ""${cql_file}"" executed"
done

