package com.udemy.curso.springboot.cursomc.dto;

import java.io.Serializable;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;

import org.hibernate.validator.constraints.Length;

import com.udemy.curso.springboot.cursomc.domain.Cliente;
import com.udemy.curso.springboot.cursomc.services.validation.ClienteUpdate;

//anotação @ClienteUpdate para o update de email
@ClienteUpdate
public class ClienteDTO implements Serializable {
	private static final long serialVersionUID = 1L;

	private Integer id;
	
	@NotEmpty (message = "Preenchimento obrigatório")
	@Length (min = 5, max = 120, message = "O tamanho deve ser entre 5 e 120 caracteres")
	private String nome;
	
	@NotEmpty (message = "Preenchimento obrigatório")
	@Email (message = "Email inválido")
	private String email;

	// Costrutores

	public ClienteDTO() {
		super();
	}

	// Esse construtor é responsavel por instanciar o DTO
	// para mim
	public ClienteDTO(Cliente obj) {
		id = obj.getId();
		nome = obj.getNome();
		email = obj.getEmail();
	}

	// Acessores

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

}
