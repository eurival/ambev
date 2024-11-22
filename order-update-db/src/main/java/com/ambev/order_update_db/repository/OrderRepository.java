package com.ambev.order_update_db.repository;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.ambev.order_update_db.domain.enumeration.OrderStatus;
import com.ambev.order_update_db.service.dto.OrderDTO;

import jakarta.transaction.Transactional;

@Repository
public class OrderRepository {

    private JdbcTemplate jdbcTemplate;

    public OrderRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    /**
     * Atualiza um lote de pedidos no banco de dados.
     *
     * Este método recebe uma lista de objetos {@link OrderDTO} e atualiza os registros correspondentes na tabela "pedidos".
     * A operação de atualização é realizada usando atualização em lote do JDBC, o que melhora o desempenho ao reduzir o número
     * de idas e voltas ao banco de dados.
     *
     * @param orders Uma lista de objetos {@link OrderDTO} representando os pedidos a serem atualizados. Cada objeto
     *               {@link OrderDTO} contém o ID do pedido, status e valor total.
     *
     * @return Este método não retorna nenhum valor. No entanto, ele atualiza os registros do banco de dados com base na lista
     *         fornecida de objetos {@link OrderDTO}.
     */
    @Transactional
    public void atualizarEmLote(List<OrderDTO> orders) {
        String sql = "UPDATE tbl_order SET status = ?, total_value = ? WHERE order_id = ?";

        List<Object[]> batchArgs = orders.stream()
            .map(order -> new Object[]{"PROCESSED", order.getTotalValue(), order.getOrderId()})
            .collect(Collectors.toList());

        jdbcTemplate.batchUpdate(sql, batchArgs);
    }

    
}
