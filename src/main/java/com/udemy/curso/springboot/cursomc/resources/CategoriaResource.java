package com.udemy.curso.springboot.cursomc.resources;

import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.udemy.curso.springboot.cursomc.domain.Categoria;
import com.udemy.curso.springboot.cursomc.dto.CategoriaDTO;
import com.udemy.curso.springboot.cursomc.services.CategoriaService;

@RestController
@RequestMapping("/categorias")// padrão do ending point no plural
public class CategoriaResource {

	@Autowired
	private CategoriaService categoriaService;

	// Médodo GET - devolve um item da categoria de acordo com o id solicitado
	
	@GetMapping("/{id}")
	public ResponseEntity<Categoria> find(@PathVariable Integer id) {
		Categoria obj = categoriaService.find(id);
		return ResponseEntity.ok().body(obj);
	}
	
	// Médodo POST - insere uma nova Categoria em um novo Id!
	
//	//Lembrando que o Método para salvar fica na classe CategoriaService:
//	//O @RequestBody faz o Json ser convertido para o objeto java automáticamente
//	//(sem ele o retornava null o nome inserido no postman)
//	//@RequestMapping(method=RequestMethod.POST)
//	@PostMapping
//	public ResponseEntity<Void> insert(@RequestBody Categoria obj) {
//		obj = categoriaService.insert(obj);// essa linha garande que sera salvo o próximo id disponivel na Categoria
//		URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(obj.getId()).toUri();
//		//URI é o campo local host http://localhost:8099/categorias
//		//fromCurrentRequest() pega a URL que usamos para inserir nesse caso http://localhost:8099/categorias
//		//.path("/{id}" para o id a ser criado
//		//.buildAndExpand(obj.getId()) para atribuir o valor para ele
//		//.toUri() para converter para URI
//		//no return usamos o .created pois ele retorna o "201" que é o correto para essa aplicação
//		//basta pesquisar http status code que veremos uma lista com todos os códigos adequados
//		return ResponseEntity.created(uri).build();
//	}
	
	//Com a mudança para o uso do DTO nosso código ficou assim:
	
	//Adicionamos o @Valid para validação conforme abaixo:
	@PostMapping
	public ResponseEntity<Void> insert(@Valid @RequestBody CategoriaDTO objDto) {//para converter esse objDto fazemos 
		//o método na classe CategoriaService e o cod abaixo:
		Categoria obj = categoriaService.fromDTO(objDto);
		obj = categoriaService.insert(obj);
		URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(obj.getId()).toUri();
	return ResponseEntity.created(uri).build();
	}
	
	//Método PUT - Altera o conteudo de uma ID 
	
//	//Funciona como uma "mistura" do GET e POST, ele recebe o o objeto e o parametro no URL
//	@PutMapping ("/{id}")
//	public ResponseEntity<Void> update (@RequestBody Categoria obj , @PathVariable Integer id){
//		obj.setId(id);
//		obj = categoriaService.update(obj);
//		return ResponseEntity.noContent().build();
//	}
	
	//Com a mudança para o uso do DTO nosso código ficou assim:
	
	@PutMapping ("/{id}")
	public ResponseEntity<Void> update (@Valid @RequestBody CategoriaDTO objDto , @PathVariable Integer id){
		Categoria obj = categoriaService.fromDTO(objDto);
		obj.setId(id);
		obj = categoriaService.update(obj);
		return ResponseEntity.noContent().build();
	}
	
	//Médodo DELETE - Deleta uma Categoria

	@DeleteMapping ("/{id}")
	public ResponseEntity<Void> delete (@PathVariable Integer id){
		categoriaService.delete(id);
		return ResponseEntity.noContent().build();	
	}
	
	// Médodo GET - devolve todas as cadegorias
	
//	@GetMapping
//	public ResponseEntity<List<Categoria>> findAll() {
//		List<Categoria> list = categoriaService.findAll();
//		return ResponseEntity.ok().body(list);
//	}
		//Problema encontrado! com o método acima, ele traz além das Categorias,
		//Todos os itens "penturados" nele, ou seja, todos os produtos,
		//para evitar que isso aconteça usamos o DTO, que é um tipo de 
		//projeção de dados
	
	@GetMapping
	public ResponseEntity<List<CategoriaDTO>> findAll() {
		//Assim preciso converter um Lista de Categorias para CategoriaDTO
		//Essa pratica é feita no DTO
		List<Categoria> list = categoriaService.findAll();
		//Assim percorro a lista e para cada elemento dessa lista (cod acima)
		//Instancio o DTO correspondente (cod  abaixo)
		List<CategoriaDTO> listDto = list.stream()
				.map(obj -> new CategoriaDTO(obj))
				.collect(Collectors.toList());
        //.stream() percorre a lista
		//.map(obj -> new CategoriaDTO(obj)) efetua uma operação para cada 
		//elemento da lista apelidado aqui como obj , assim crio uma
		//nova lista DTO passando "obj" como argumento
		//.collect(Collectors.toList()); preciso voltar o stream para o
		//tipo list por isso o Collectors.toList
		return ResponseEntity.ok().body(listDto);
	}
	
	// Método paginação
	
	@GetMapping ("/page")
	public ResponseEntity<Page<CategoriaDTO>> findpage(
			@RequestParam(value="page",defaultValue="0") Integer page,
			@RequestParam(value="linesPerPage",defaultValue="24")Integer linesPerPage,
			@RequestParam(value="orderBy",defaultValue="nome")String orderBy,
			@RequestParam(value="direction",defaultValue="ASC")String direction) {//DESC para decrescente
		Page<Categoria> list = categoriaService.findPage(page,linesPerPage,orderBy,direction);
		Page<CategoriaDTO> listDto = list.map(obj -> new CategoriaDTO(obj));
		return ResponseEntity.ok().body(listDto);
		
		//Integer linesPerPage é uma boa prática deixar 24 pois ele é multiplo de 1, 2 , 3 e 4
		//assim facilita a visualizaçao
	}
	
}
