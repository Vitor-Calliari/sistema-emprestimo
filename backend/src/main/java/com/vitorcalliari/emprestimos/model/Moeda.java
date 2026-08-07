package com.vitorcalliari.emprestimos.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "moeda")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Moeda {

    @Id
    private String codigo;
    private String nome;
}