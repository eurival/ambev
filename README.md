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
  
4. **API Busca de Order** `order-viewer`:
   - API REST de busca de Orders,
   - Autentição por JWT usuario inicial
   - Filtragem 

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

2. Compile e execute o projeto:
   No diretorio ambev, execute ./build.sh, será instalado todos os componentes, postgresql, RabbitMQ e os microserviços
   todos em contanies
3. Executar testes
   Na pasta teste-massivo, pode ser feito teste utilizando scritp do framework python Locusts,
   execute: python3 locustfile.py e siga as instruções.

   Tambem pode ser feito teste no postman, mas primeiramente deve efetuar autenticação em:
   ```bash
   http://localhost:8080/order-service/api/auth/login
   body: { "username": "admin", "password": "admin"}

  Para simular o SISTEMA EXTERNO A:
  ```bash

        POST: http://localhost:8080/order-viewer/api/order-server
        BUDY:
            {
          "orderId": "2233",
          "orderDate": "2024-11-20T15:00:00Z",
          "totalValue": 0,
          "status": "PENDING",
          "items": [
              { "productId": "P001", "productName": "Product 1", "unitPrice": null, "quantity": null, "totalPrice": 50.00 },
              { "productId": "P002", "productName": "Product 2", "unitPrice": 0, "quantity": 63, "totalPrice": 200.00 },
              { "productId": "P003", "productName": "Product 3", "unitPrice": 75.50, "quantity": 0, "totalPrice": 226.50 },
              { "productId": "P004", "productName": "Product 4", "unitPrice": 0, "quantity": 2, "totalPrice": 120.50 },
              { "productId": "P005", "productName": "Product 5", "unitPrice": 85.00, "quantity": 4, "totalPrice": 340.00 },
              { "productId": "P006", "productName": "Product 6", "unitPrice": 45.75, "quantity": 5, "totalPrice": 228.75 },
              { "productId": "P007", "productName": "Product 7", "unitPrice": 365.00, "quantity": 1, "totalPrice": 110.00 },
              { "productId": "P008", "productName": "Product 8", "unitPrice": 22.00, "quantity": 1, "totalPrice": 120.00 },
              { "productId": "P009", "productName": "Product 9", "unitPrice": 90.50, "quantity": 3, "totalPrice": 271.50 },
              { "productId": "P010", "productName": "Product 10", "unitPrice": 150.00, "quantity": 1, "totalPrice": 150.00 },
              { "productId": "P011", "productName": "Product 11", "unitPrice": 35.00, "quantity": 2, "totalPrice": 70.00 },
              { "productId": "P012", "productName": "Product 12", "unitPrice": 65.00, "quantity": 2, "totalPrice": 130.00 },
              { "productId": "P013", "productName": "Product 13", "unitPrice": 40.50, "quantity": 5, "totalPrice": 202.50 },
              { "productId": "P014", "productName": "Product 14", "unitPrice": 95.00, "quantity": 3, "totalPrice": 285.00 },
              { "productId": "P015", "productName": "Product 15", "unitPrice": 55.00, "quantity": 4, "totalPrice": 220.00 },
              { "productId": "P016", "productName": "Product 16", "unitPrice": 80.75, "quantity": 2, "totalPrice": 161.50 },
              { "productId": "P017", "productName": "Product 17", "unitPrice": 47.50, "quantity": 6, "totalPrice": 285.00 },
              { "productId": "P018", "productName": "Product 18", "unitPrice": 30.25, "quantity": 3, "totalPrice": 90.75 },
              { "productId": "P019", "productName": "Product 19", "unitPrice": 105.00, "quantity": 1, "totalPrice": 105.00 },
              { "productId": "P020", "productName": "Product 20", "unitPrice": 50.50, "quantity": 3, "totalPrice": 151.50 }
          ]
         }
```
   Para buscar os pedidos: Sistema Externo B
   Atenticação:
   ```bash
   http://localhost:8083/order-viewer/api/auth/login
   body: { "username": "admin", "password": "admin"}

   Busca:
   GET: http://localhost:8083/order-viewer/api/v1/orders
   Lista todas as orders
   Utilize filtro: http://localhost:8083/order-viewer/api/v1/orders?orderBy=2233
```


---
## **Soluções para possiveis gargalos**
Pode-se implementar cluster Kubernet, dos microserviços, utilizar loadbalance, autoscaling horizontal.
