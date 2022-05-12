package br.ce.wcaquino.servicos;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Assume;
import org.junit.Before;
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
	
	private LocacaoService locacaoService;
		
	@Rule
	public ErrorCollector errorCollector= new ErrorCollector();
	
	@Rule
    public ExpectedException exception = ExpectedException.none();
	
	@Before
	public void init() {
		
		locacaoService = new LocacaoService();
		
	}

	@Test
	public void deveAlugarFilme() throws Exception {
		
		Assume.assumeTrue(DataUtils.verificarDiaSemana(new Date(), Calendar.SATURDAY));

		// Cenario
		Usuario usuario = new Usuario();
		List<Filme> filmes = Arrays.asList(new Filme("Filme 1", 2, 5.0));

		// Ação
		Locacao locacao = locacaoService.alugarFilme(usuario, filmes);
			
		// Verificação
		errorCollector.checkThat(locacao.getValor(), CoreMatchers.is(CoreMatchers.equalTo(5.0))); // Verifique que o valor da locação é igual a 5.0
		errorCollector.checkThat(DataUtils.isMesmaData(locacao.getDataLocacao(), new Date()), CoreMatchers.is(true));
		errorCollector.checkThat(DataUtils.isMesmaData(locacao.getDataRetorno(), DataUtils.obterDataComDiferencaDias(1)), CoreMatchers.is(true));
	
		
	}
		
	
	//Estratégia elegante - Você pode mudar de Exception para FilmeSemEstoqueException, pois FilmeSemEstoqueException extends Exception
	// Tem que ter a garantia que a excessão esta vindo apenas por um motivo
	@Test(expected = FilmeSemEstoqueException.class)
	public void deveLancaExcessaoAoAlugarFilmeSemEstoque() throws Exception {
		
		// Cenario
		Usuario usuario = new Usuario("Usuario 1");
		List<Filme> filmes = Arrays.asList(new Filme("Filme 1", 0, 5.0));
		
		// Ação		
		locacaoService.alugarFilme(usuario, filmes);
	}
	
	
	@Test
	public void naoDeveAlugarFilmeSemUsuario() throws FilmeSemEstoqueException {
		
		
		List<Filme> filmes = Arrays.asList(new Filme("Harry Potter", 2, 5.0));
		
		try {
			locacaoService.alugarFilme(null, filmes);
			Assert.fail();
		}  catch (LocadoraException e) {
			//Checar a mensagem 
			Assert.assertThat(e.getMessage(), CoreMatchers.is("Usuario vazio"));
		}
		
	}
	
	
	@Test
	public void naoDeveAlugarFilmeSemFilme() throws FilmeSemEstoqueException, LocadoraException {
		
		
		Usuario usuario = new Usuario("Jeferson");
		
		exception.expect(LocadoraException.class);
		exception.expectMessage("Filme vazio");
		
		locacaoService.alugarFilme(usuario, null);
	}
	
	
	@Test
	public void deve_pagar75Porcento_noFilmeTres() throws FilmeSemEstoqueException, LocadoraException {
		
		Usuario usuario = new Usuario("Jeferson");
		List<Filme> filmes = Arrays.asList(new Filme("Filme 1", 2, 4.0), new Filme("Filme 2", 2, 4.0), new Filme("Filme 3", 2, 4.0));
		
		Locacao resultado = locacaoService.alugarFilme(usuario, filmes);
		
		Assert.assertThat(resultado.getValor(), CoreMatchers.is(11.0));
		
	}
	
	@Test
	public void deve_pagar75Porcento_noFilmeQuatro() throws FilmeSemEstoqueException, LocadoraException {
		
		Usuario usuario = new Usuario("Jeferson");
		List<Filme> filmes = Arrays.asList(new Filme("Filme 1", 2, 4.0), new Filme("Filme 2", 2, 4.0), new Filme("Filme 3", 2, 4.0), new Filme("Filme 4", 2, 4.0));
		
		Locacao resultado = locacaoService.alugarFilme(usuario, filmes);
		
		Assert.assertThat(resultado.getValor(), CoreMatchers.is(13.0));
		
	}
	
	@Test
	public void deve_pagar75Porcento_noFilmeCinco() throws FilmeSemEstoqueException, LocadoraException {
		
		Usuario usuario = new Usuario("Jeferson");
		List<Filme> filmes = Arrays.asList(new Filme("Filme 1", 2, 4.0), 
										new Filme("Filme 2", 2, 4.0), 
										new Filme("Filme 3", 2, 4.0), 
										new Filme("Filme 4", 2, 4.0), 
										new Filme("Filme 5", 2, 4.0));
		
		Locacao resultado = locacaoService.alugarFilme(usuario, filmes);
		
		Assert.assertThat(resultado.getValor(), CoreMatchers.is(14.0));
		
	}
	
	@Test
	public void deve_pagar75Porcento_noFilmeSeis() throws FilmeSemEstoqueException, LocadoraException {
		
		Usuario usuario = new Usuario("Jeferson");
		List<Filme> filmes = Arrays.asList(new Filme("Filme 1", 2, 4.0), 
										new Filme("Filme 2", 2, 4.0), 
										new Filme("Filme 3", 2, 4.0), 
										new Filme("Filme 4", 2, 4.0), 
										new Filme("Filme 5", 2, 4.0),
										new Filme("Filme 6", 2, 4.0));
		
		Locacao resultado = locacaoService.alugarFilme(usuario, filmes);
		
		Assert.assertThat(resultado.getValor(), CoreMatchers.is(14.0));
		
	}
	
	
	@Test
	public void deveDevolverFilmeNaSegundAoAlugarNoSabado() throws FilmeSemEstoqueException, LocadoraException {
		
		Assume.assumeTrue(DataUtils.verificarDiaSemana(new Date(), Calendar.SATURDAY)); // Assumptions
		
		Usuario usuario = new Usuario("Usuario 1");
		List<Filme> filmes = Arrays.asList(new Filme("Filme 1", 1, 5.0));
		
		Locacao retorno = locacaoService.alugarFilme(usuario, filmes);
		
		boolean ehSegunda = DataUtils.verificarDiaSemana(retorno.getDataRetorno(), Calendar.MONDAY);
		
		Assert.assertTrue(ehSegunda);
	}
	
}
