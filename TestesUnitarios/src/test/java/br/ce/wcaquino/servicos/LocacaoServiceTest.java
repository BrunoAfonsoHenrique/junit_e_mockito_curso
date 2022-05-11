package br.ce.wcaquino.servicos;

import java.util.Date;

import org.hamcrest.CoreMatchers;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ErrorCollector;

import br.ce.wcaquino.entidades.Filme;
import br.ce.wcaquino.entidades.Locacao;
import br.ce.wcaquino.entidades.Usuario;
import br.ce.wcaquino.utils.DataUtils;

public class LocacaoServiceTest {
		
	@Rule
	public ErrorCollector errorCollector= new ErrorCollector();

	@Test
	public void testeLocacao() throws Exception {

		// Cenario
		LocacaoService locacaoService = new LocacaoService();
		Usuario usuario = new Usuario();
		Filme filme = new Filme("Harry Potter", 2, 5.0);

		// Ação
		Locacao locacao;
		
		locacao = locacaoService.alugarFilme(usuario, filme);
			
		// Verificação
		errorCollector.checkThat(locacao.getValor(), CoreMatchers.is(CoreMatchers.equalTo(5.0))); // Verifique que o valor da locação é igual a 5.0
		errorCollector.checkThat(DataUtils.isMesmaData(locacao.getDataLocacao(), new Date()), CoreMatchers.is(true));
		errorCollector.checkThat(DataUtils.isMesmaData(locacao.getDataRetorno(), DataUtils.obterDataComDiferencaDias(1)), CoreMatchers.is(true));
	
		
	}
	
	//Estratégia elegante
	@Test(expected = Exception.class)
	public void testeLocacao_filmeSemEstoque() throws Exception {
		
		// Cenario
		LocacaoService locacaoService = new LocacaoService();
		Usuario usuario = new Usuario();
		Filme filme = new Filme("Harry Potter", 0, 5.0);

		// Ação
		Locacao locacao;
				
		locacao = locacaoService.alugarFilme(usuario, filme);
	}
	

}
