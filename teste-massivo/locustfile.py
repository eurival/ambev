from locust import HttpUser, task, between
import random
import string
import datetime
from locust.clients import HttpSession

class OrderServiceUser(HttpUser):
    wait_time = between(1, 3)  # Intervalo entre as requisições
    host = "http://localhost"  # Necessário, mas não será usado diretamente

    def on_start(self):
        """Inicializa os clientes para os serviços e autentica."""
        self.order_service_client = HttpSession(
            base_url="http://localhost:8080", 
            request_event=self.environment.events.request,
            user=self
        )
        self.order_viewer_client = HttpSession(
            base_url="http://localhost:8083", 
            request_event=self.environment.events.request,
            user=self
        )
        self.authenticate()

    def authenticate(self):
        """Realiza a autenticação e armazena os tokens JWT."""
        # Autenticação para o order-service
        response = self.order_service_client.post(
            "/order-service/api/auth/login", 
            json={"username": "admin", "password": "admin"}
        )
        if response.status_code == 200:
            self.token = response.text
            print("Autenticação bem-sucedida no order-service.")
        else:
            print(f"Falha ao autenticar no order-service: {response.status_code} {response.text}")

        # Autenticação para o order-viewer
        response = self.order_viewer_client.post(
            "/order-viewer/api/auth/login", 
            json={"username": "admin", "password": "admin"}
        )
        if response.status_code == 200:
            self.tokenviewer = response.text
            print("Autenticação bem-sucedida no order-viewer.")
        else:
            print(f"Falha ao autenticar no order-viewer: {response.status_code} {response.text}")

    def handle_403(self, client, retry_func):
        """Reautentica e repete a operação em caso de código 403."""
        print("Token expirado ou inválido. Reautenticando...")
        self.authenticate()
        retry_func(client)  # Tenta a operação novamente após autenticar

    def create_order_request(self, client):
        """Lógica para criação de pedido (reutilizável em reautenticação)."""
        random_timestamp = random.uniform(0, datetime.datetime.now().timestamp())
        order_data = {
            "orderId": self.random_string(8),
            "orderDate": random_timestamp,
            "totalValue": 0,
            "status": "PENDING",
            "items": [
                {"productId": self.random_string(4), "quantity": random.randint(1, 100), "productName": self.random_string(8), "unitPrice": random.randint(12, 50000)}
                for _ in range(random.randint(1, 35))
            ]
        }
        headers = {"Authorization": f"Bearer {self.token}"}
        response = client.post(
            "/order-service/api/v1/orders", 
            json=order_data, 
            headers=headers
        )
        return response

    @task(1)
    def create_order(self):
        if not self.token:
            print("Token não encontrado. Abortando `create_order`.")
            return
        response = self.create_order_request(self.order_service_client)
        if response.status_code == 403:
            self.handle_403(self.order_service_client, self.create_order_request)
        elif response.status_code != 201:
            print(f"Erro ao criar pedido: {response.status_code} {response.text}")

    def get_orders_request(self, client):
        """Lógica para buscar pedidos (reutilizável em reautenticação)."""
        params = {"page": random.randint(0, 500), "size": 20}
        headers = {"Authorization": f"Bearer {self.tokenviewer}"}
        response = client.get(
            "/order-viewer/api/v1/orders", 
            params=params, 
            headers=headers
        )
        return response

    @task(2)
    def get_orders(self):
        if not self.tokenviewer:
            print("Token não encontrado. Abortando `get_orders`.")
            return
        response = self.get_orders_request(self.order_viewer_client)
        if response.status_code == 403:
            self.handle_403(self.order_viewer_client, self.get_orders_request)
        elif response.status_code != 200:
            print(f"Erro ao buscar pedidos: {response.status_code} {response.text}")

    def random_string(self, length):
        """Gera uma string aleatória com letras maiúsculas."""
        return ''.join(random.choices(string.ascii_uppercase, k=length))
