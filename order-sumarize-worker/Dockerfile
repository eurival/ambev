# Usar uma imagem base do Java
FROM eclipse-temurin:17-jdk-alpine

# Diretório de trabalho dentro do contêiner
WORKDIR /app

# Copiar o arquivo JAR gerado pelo Maven para dentro do contêiner
COPY target/order-sumarize-worker-0.0.1-SNAPSHOT.jar app.jar

# Adicionar variáveis de ambiente para Liquibase
ENV DB_URL=jdbc:postgresql://postgres:5432/orderdb
ENV DB_USERNAME=user01
ENV DB_PASSWORD=password

 
# Comando para rodar o aplicativo Spring Boot
ENTRYPOINT ["java", "-jar", "app.jar"]
