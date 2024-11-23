# Usar uma imagem oficial do OpenJDK
FROM openjdk:17-jdk-slim

# Define o diretório de trabalho no contêiner
WORKDIR /app

# Copiar o arquivo JAR do serviço para o contêiner
COPY target/*.jar app.jar

# Comando para iniciar o serviço
ENTRYPOINT ["java", "-jar", "app.jar"]
