# ambev
# **Order Processing Service**
> Sistema de processamento de pedidos em microsserviços utilizando RabbitMQ e Spring Boot.

## **Descrição do Projeto**

Este projeto consiste em um conjunto de microsserviços para gerenciar o fluxo de processamento de pedidos. Ele utiliza RabbitMQ para comunicação entre serviços e inclui um serviço dedicado para atualização em lote de pedidos processados, garantindo consistência e alta disponibilidade.

---

## **Arquitetura**

A arquitetura consiste nos seguintes microsserviços:

1. **Order Service**:
   - Recebe os pedidos via API REST.
   - Valida e salva os pedidos no banco de dados.
   - Envia mensagens para a fila `pedidos-pending`.

2. **Sumarize Service**:
   - Consome mensagens da fila `pedidos-pending`.
   - Calcula o valor total dos pedidos.
   - Atualiza o status para "processado".
   - Publica os pedidos processados na fila `pedidos-processados`.

3. **Order Update Service**:
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
   git clone https://github.com/seu-usuario/order-processing-service.git
   cd order-processing-service
