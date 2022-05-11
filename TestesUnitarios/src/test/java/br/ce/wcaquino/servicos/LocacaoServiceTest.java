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
	
	//Estrat�gia elegante
	@Test(expected = Exception.class)
	public void testeLocacao_filmeSemEstoque() throws Exception {
		
		// Cenario
		LocacaoService locacaoService = new LocacaoService();
		Usuario usuario = new Usuario();
		Filme filme = new Filme("Harry Potter", 0, 5.0);

		// A��o		
		locacaoService.alugarFilme(usuario, filme);
	}
	
	//Estrat�gia Robusta - Vantagem: Pode verficar a mensagem lan�ada na exce��o
	@Test
	public void testeLocacao_filmeSemEstoque_2() {
			
		// Cenario
		LocacaoService locacaoService = new LocacaoService();
		Usuario usuario = new Usuario();
		Filme filme = new Filme("Harry Potter", 0, 5.0);
	
		// A��o				
		try {
			locacaoService.alugarFilme(usuario, filme);
			Assert.fail("Deveria ter lan�ado uma exce�ao");
			
		} catch (Exception e) {
			
			Assert.assertThat(e.getMessage(), CoreMatchers.is("Filme sem estoque"));
		}
	
	}
	
	// Estrat�gia da forma nova - Tratamento dessa exce��o ser� atrav�s de uma outra Rule: ExpectedException
	@Test
	public void testeLocacao_filmeSemEstoque_3() throws Exception {
		
		// Cenario
		LocacaoService locacaoService = new LocacaoService();
		Usuario usuario = new Usuario();
		Filme filme = new Filme("Harry Potter", 0, 5.0);
		
		exception.expect(Exception.class);
		exception.expectMessage("Filme sem estoque");
		
		//A��o
		locacaoService.alugarFilme(usuario, filme);
	
	}
	

}
