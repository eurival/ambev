# **Order Processing Service**
> Sistema de processamento de pedidos em microsserviços utilizando RabbitMQ e Spring Boot.

## **Descrição do Projeto**

Este projeto consiste em um conjunto de microsserviços para gerenciar o fluxo de processamento de pedidos. Ele utiliza RabbitMQ para comunicação entre serviços e inclui um serviço dedicado para atualização em lote de pedidos processados, garantindo consistência e alta disponibilidade.

---

## **Arquitetura**

A arquitetura consiste nos seguintes microsserviços:

1. **Order Service** `order`:
   - Recebe os pedidos via API REST.
   - Valida e salva os pedidos no banco de dados.
   - Envia mensagens para a fila `pedidos-pendentes`.

2. **Sumarize Service** `order-sumarize-worker`:
   - Consome mensagens da fila `pedidos-pendentes`.
   - Calcula o valor total dos pedidos.
   - Atualiza o status para "processado".
   - Publica os pedidos processados na fila `pedidos-processados`.

3. **Order Update Service** `order-update-db`:
   - Consome mensagens da fila `pedidos-processados`.
   - Atualiza os pedidos no banco de dados em lote.
   - Garante resiliência com Dead Letter Queue (DLQ) em caso de falha.

---

## **Tecnologias Utilizadas**

- **Linguagem:** Java 17
- **Framework:** Spring Boot 3.x
- **Mensageria:** RabbitMQ
- **Banco de Dados:** PostgreSQL
- **Gerenciamento de Dependências:** Maven

---

## **Configuração e Execução**

### **Pré-requisitos**

- Java 17+
- Maven 3.8+
- Docker (para RabbitMQ e PostgreSQL)

### **Instalação**

1. Clone o repositório:
   ```bash
   git clone https://github.com/eurival/ambev.git
   cd ambev
2. Configure o banco de dados no arquivo application-dev.yml no `order` e no `order-update-db`:
   ```bash
   spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/order
    username: seu_usuario
    password: sua_senha
3. Configurar o RabbitMQ em `src/main/resources/config/rabbitMQ/docker-compose.yml`:
   ```bash
   services:
     rabbitmq:
       image: rabbitmq:3-management
       container_name: rabbitmq
       ports:
         - "5672:5672"  # Porta para o protocolo AMQP
         - "15672:15672" # Porta para a interface de gerenciamento
       environment:
         RABBITMQ_DEFAULT_USER: admin  # Usuário padrãol
         RABBITMQ_DEFAULT_PASS: admin  # Senha padrão
       volumes:
         - rabbitmq_data:/var/lib/rabbitmq  # Persistência de dados
         - rabbitmq_logs:/var/log/rabbitmq  # Persistência de logs
   
   volumes:
     rabbitmq_data:
     rabbitmq_logs:
4.

