package com.ambev.sumarize_worker.domain;


import java.io.Serializable;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Table(name = "tbl_role")
@Entity
public class RoleEntity implements Serializable{
    private static final long serialVersionUID = 1;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name; 
}
