package br.ce.wcaquino.servicos;

import java.util.Date;

import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ErrorCollector;
import org.junit.rules.ExpectedException;

import br.ce.wcaquino.entidades.Filme;
import br.ce.wcaquino.entidades.Locacao;
import br.ce.wcaquino.entidades.Usuario;
import br.ce.wcaquino.exceptions.FilmeSemEstoqueException;
import br.ce.wcaquino.exceptions.LocadoraException;
import br.ce.wcaquino.utils.DataUtils;

public class LocacaoServiceTest {
		
	@Rule
	public ErrorCollector errorCollector= new ErrorCollector();
	
	@Rule
    public ExpectedException exception = ExpectedException.none();
	
	
	@Test
	public void testeLocacao() throws Exception {

		// Cenario
		LocacaoService locacaoService = new LocacaoService();
		Usuario usuario = new Usuario();
		Filme filme = new Filme("Harry Potter", 2, 5.0);

		// A��o
		Locacao locacao;
		
		locacao = locacaoService.alugarFilme(usuario, filme);
			
		// Verifica��o
		errorCollector.checkThat(locacao.getValor(), CoreMatchers.is(CoreMatchers.equalTo(5.0))); // Verifique que o valor da loca��o � igual a 5.0
		errorCollector.checkThat(DataUtils.isMesmaData(locacao.getDataLocacao(), new Date()), CoreMatchers.is(true));
		errorCollector.checkThat(DataUtils.isMesmaData(locacao.getDataRetorno(), DataUtils.obterDataComDiferencaDias(1)), CoreMatchers.is(true));
	
		
	}
		
	
	//Estrat�gia elegante - Voc� pode mudar de Exception para FilmeSemEstoqueException, pois FilmeSemEstoqueException extends Exception
	// Tem que ter a garantia que a excess�o esta vindo apenas por um motivo
	@Test(expected = FilmeSemEstoqueException.class)
	public void testeLocacao_filmeSemEstoque() throws Exception {
		
		// Cenario
		LocacaoService locacaoService = new LocacaoService();
		Usuario usuario = new Usuario();
		Filme filme = new Filme("Harry Potter", 0, 5.0);

		// A��o		
		locacaoService.alugarFilme(usuario, filme);
	}
	
	
	@Test
	public void deve_testar_UsuarioVazio() throws FilmeSemEstoqueException {
		
		LocacaoService locacaoService = new LocacaoService();
		Filme filme = new Filme("Avatar", 2, 25.0);
		
		try {
			locacaoService.alugarFilme(null, filme);
			Assert.fail();
		}  catch (LocadoraException e) {
			//Checar a mensagem 
			Assert.assertThat(e.getMessage(), CoreMatchers.is("Usuario vazio"));
		}
		
	}
	
	
	@Test
	public void deve_testar_FilmeVazio() throws FilmeSemEstoqueException, LocadoraException {
		
		LocacaoService locacaoService = new LocacaoService();
		Usuario usuario = new Usuario("Jeferson");
		
		exception.expect(LocadoraException.class);
		exception.expectMessage("Filme vazio");
		
		locacaoService.alugarFilme(usuario, null);
	}
	
	
//	Forma elegante -> funciona bem quando apenas a exce��o � o que importa, seria nos casos que voc� consegue garantir o motivo pelo qual a exce��o foi lan�ada
//	
//	Forma robusta e forma nova -> Se voc� precisar da mensagem, voc� vai precisar de uma das duas
//	
//	Forma nova -> atende na grande maioria dos casos
//	
//	Recomenda��o -> Forma robusta
	

}
