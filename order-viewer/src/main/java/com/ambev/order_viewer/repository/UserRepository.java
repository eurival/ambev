package com.ambev.order_viewer.repository;

 
import org.springframework.data.jpa.repository.JpaRepository;

import com.ambev.order_viewer.domain.UserEntity;

import java.util.Optional;

public interface UserRepository extends JpaRepository<UserEntity, Long> {
    Optional<UserEntity> findByUsername(String username);
}
