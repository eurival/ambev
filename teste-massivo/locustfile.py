from locust import HttpUser, task, between
import random
import string
import json
import datetime

class OrderServiceUser(HttpUser):
    wait_time = between(1, 3)  # Intervalo entre as requisições
    token = None  # Variável para armazenar o token JWT

    def on_start(self):
        """Autenticação ao iniciar o teste."""
        self.authenticate()

    def authenticate(self):
        """Realiza a autenticação e armazena o token JWT."""
        response = self.client.post(
            "/order-service/api/auth/login", 
            json={"username": "admin", "password": "admin"}
        )
        if response.status_code == 200:
            # Armazena o token JWT
            self.token = response.text
        else:
            print(f"Falha ao autenticar: {response.status_code} {response.text}")

    @task(1)
    def create_order(self):
        if not self.token:
            print("Token não encontrado. Abortando `create_order`.")
            return
        random_timestamp = random.uniform(0, datetime.datetime.now().timestamp())
        # Dados dinâmicos para o pedido
        order_data = {
            "orderId": self.random_string(8),
            "orderDate": random_timestamp,
            "totalValue": 0,
            "status": "PENDING",
            "items": [
                {"productId": self.random_string(4), "quantity": random.randint(1, 100),"productName": self.random_string(8),"unitPrice": random.randint(12,50000)}
                for _ in range(random.randint(1, 35))
            ]
        }

        # Cabeçalho com o token JWT
        headers = {"Authorization": f"Bearer {self.token}"}

        # Envia a requisição POST
        response = self.client.post(
            "/order-service/api/v1/orders", 
            json=order_data, 
            headers=headers
        )

        if response.status_code != 201:
            print(f"Erro ao criar pedido: {response.status_code} {response.text}")

    @task(2)
    def get_orders(self):
        if not self.token:
            print("Token não encontrado. Abortando `get_orders`.")
            return

        params = {"page": random.randint(0, 10), "size": 20}
        headers = {"Authorization": f"Bearer {self.token}"}

        # Envia a requisição GET
        response = self.client.get(
            "/order-service/api/v1/orders", 
            params=params, 
            headers=headers
        )

        if response.status_code != 200:
            print(f"Erro ao buscar pedidos: {response.status_code} {response.text}")

    def random_string(self, length):
        """Gera uma string aleatória com letras maiúsculas."""
        return ''.join(random.choices(string.ascii_uppercase, k=length))
