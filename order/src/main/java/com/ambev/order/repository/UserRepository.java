package com.ambev.order.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ambev.order.domain.UserEntity;

import java.util.Optional;

public interface UserRepository extends JpaRepository<UserEntity, Long> {
    Optional<UserEntity> findByUsername(String username);
}
