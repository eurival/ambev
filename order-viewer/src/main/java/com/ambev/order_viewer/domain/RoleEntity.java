package com.ambev.order_viewer.domain;


import java.io.Serializable;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Table(name = "tbl_role")
@Entity
public class RoleEntity implements Serializable{
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name; 
}
