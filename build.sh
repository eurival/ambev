#!/bin/bash
echo "Compilando projetos Maven ..."

# Navega para cada subdiretório e executa o Maven
echo "Compilando projetos order ..."
mvn -f ./order/pom.xml clean package -DskipTests || exit 1
echo "Compilando projetos sumarize-worker ..."
mvn -f ./order-sumarize-worker/pom.xml clean package -DskipTests || exit 1
echo "Compilando projetos order-update-db ..."
mvn -f ./order-update-db/pom.xml clean package -DskipTests || exit 1
echo "Compilando projetos order-viewer ..."
mvn -f ./order-viewer/pom.xml clean package -DskipTests || exit 1


echo "Build concluído em todos os microserviços."
docker compose up -d
