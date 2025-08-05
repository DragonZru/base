# readme:
# docker network create mysql_default
# docker inspect mysql_default
# --> "Config": [
                {
                    "Subnet": "172.21.0.0/16",
                    "Gateway": "172.21.0.1"
                }
            ]
# docker inspect -f '{{range.NetworkSettings.Networks}}{{.IPAddress}}{{end}}' container-id

docker compose -f mgr.yaml up -d

exec sql(see repl.sql)