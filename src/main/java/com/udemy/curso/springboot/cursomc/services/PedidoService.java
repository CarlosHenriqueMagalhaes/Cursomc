package com.udemy.curso.springboot.cursomc.services;

import java.util.Date;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.udemy.curso.springboot.cursomc.domain.ItemPedido;
import com.udemy.curso.springboot.cursomc.domain.PagamentoComBoleto;
import com.udemy.curso.springboot.cursomc.domain.Pedido;
import com.udemy.curso.springboot.cursomc.domain.enums.EstadoPagamento;
import com.udemy.curso.springboot.cursomc.repositories.ItemPedidoRepository;
import com.udemy.curso.springboot.cursomc.repositories.PagamentoRepository;
import com.udemy.curso.springboot.cursomc.repositories.PedidoRepository;
import com.udemy.curso.springboot.cursomc.services.exceptions.ObjectNotFoundException;

@Service
public class PedidoService {

	@Autowired
	private PedidoRepository pedidoRepository;
	
	@Autowired
	private BoletoService boletoService;
	
	@Autowired
	private PagamentoRepository pagamentoRepository;
	
	@Autowired
	private ProdutoService produtoService;
	
	@Autowired
	private ItemPedidoRepository itemPedidoRepository;

	// buscar para o ID (GET do controller)
	public Pedido find(Integer id) {
		Optional<Pedido> obj = pedidoRepository.findById(id);
		return obj.orElseThrow(() -> new ObjectNotFoundException(
				"Objeto não encontrado! Id: " + id + ", Tipo: " + Pedido.class.getName()));
		// Retorna mensagem/ lança uma exceção quando o Id não for existir,
		// foi criado uma classe para esse método: ObjectNotFoundException
		// Além disso temos que mudar na camada de recurso para ela receber essa
		// exceção, go to PedidoResource
	}
	
	// Referente ao POST do controller
	@Transactional
	public Pedido insert (Pedido obj) {
		obj.setId(null);
		obj.setInstante(new Date());
		obj.getPagamento().setEstado(EstadoPagamento.PENDENTE);
		obj.getPagamento().setPedido(obj);
		if (obj.getPagamento() instanceof PagamentoComBoleto){
			PagamentoComBoleto pagto = (PagamentoComBoleto) obj.getPagamento();
			boletoService.preencherPagamentoComBoleto(pagto,obj.getInstante());
		}
		obj = pedidoRepository.save(obj);
		pagamentoRepository.save(obj.getPagamento());
		for (ItemPedido ip:obj.getItens()) {
			ip.setDesconto(0.0);
			ip.setPreco(produtoService.find(ip.getProduto().getId()).getPreco());
			ip.setPedido(obj);
		}
		itemPedidoRepository.saveAll(obj.getItens());
		return obj;
		
	}
}
