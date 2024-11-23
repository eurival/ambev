#!/bin/bash
echo "Compilando projetos Maven sem executar testes..."

# Navega para cada subdiretório e executa o Maven
mvn -f ./order/pom.xml clean package -DskipTests || exit 1
mvn -f ./order-sumarize-worker/pom.xml clean package -DskipTests || exit 1
mvn -f ./order-update-db/pom.xml clean package -DskipTests || exit 1

echo "Build concluído em todos os microserviços."
docker compose up -d
