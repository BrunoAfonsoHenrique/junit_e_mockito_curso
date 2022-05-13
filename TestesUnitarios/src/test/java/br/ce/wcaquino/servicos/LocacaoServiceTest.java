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
import org.mockito.Mockito;

import br.ce.wcaquino.builders.FilmeBuilder;
import br.ce.wcaquino.builders.UsuarioBuilder;
import br.ce.wcaquino.daos.LocacaoDAO;
import br.ce.wcaquino.daos.LocacaoDAOFake;
import br.ce.wcaquino.entidades.Filme;
import br.ce.wcaquino.entidades.Locacao;
import br.ce.wcaquino.entidades.Usuario;
import br.ce.wcaquino.exceptions.FilmeSemEstoqueException;
import br.ce.wcaquino.exceptions.LocadoraException;
import br.ce.wcaquino.matchers.MatchersProprios;
import br.ce.wcaquino.utils.DataUtils;

public class LocacaoServiceTest {
	
	private LocacaoService locacaoService;
	
	private LocacaoDAO dao;
	
	private SPCService spc;
		
	@Rule
	public ErrorCollector errorCollector= new ErrorCollector();
	
	@Rule
    public ExpectedException exception = ExpectedException.none();
	
	@Before
	public void init() {
		
		locacaoService = new LocacaoService();
		dao = Mockito.mock(LocacaoDAO.class);
		locacaoService.setLocacacaoDAO(dao);
		spc = Mockito.mock(SPCService.class);
		locacaoService.setSPCService(spc);
		
	}

	@Test
	public void deveAlugarFilme() throws Exception {
		
		Assume.assumeTrue(DataUtils.verificarDiaSemana(new Date(), Calendar.SATURDAY));

		// Cenario
		Usuario usuario = UsuarioBuilder.umUsuario().agora();
		List<Filme> filmes = Arrays.asList(FilmeBuilder.umFilme().comValor(5.0).agora());

		// Ação
		Locacao locacao = locacaoService.alugarFilme(usuario, filmes);
			
		// Verificação
		errorCollector.checkThat(locacao.getValor(), CoreMatchers.is(CoreMatchers.equalTo(5.0))); // Verifique que o valor da locação é igual a 5.0
		errorCollector.checkThat(locacao.getDataLocacao(), MatchersProprios.ehHoje());
		errorCollector.checkThat(locacao.getDataRetorno(), MatchersProprios.ehHojeComDiferencaDias(1));
		
	}
		
	
	//Estratégia elegante - Você pode mudar de Exception para FilmeSemEstoqueException, pois FilmeSemEstoqueException extends Exception
	// Tem que ter a garantia que a excessão esta vindo apenas por um motivo
	@Test(expected = FilmeSemEstoqueException.class)
	public void deveLancaExcessaoAoAlugarFilmeSemEstoque() throws Exception {
		
		// Cenario
		Usuario usuario = UsuarioBuilder.umUsuario().agora();
		List<Filme> filmes = Arrays.asList(FilmeBuilder.umFilmeSemEstoque().agora());
		
		// Ação		
		locacaoService.alugarFilme(usuario, filmes);
	}
	
	
	@Test
	public void naoDeveAlugarFilmeSemUsuario() throws FilmeSemEstoqueException {
		
		
		List<Filme> filmes = Arrays.asList(FilmeBuilder.umFilme().agora());
		
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
		
		
		Usuario usuario = UsuarioBuilder.umUsuario().agora();
		
		exception.expect(LocadoraException.class);
		exception.expectMessage("Filme vazio");
		
		locacaoService.alugarFilme(usuario, null);
	}
	
	
	@Test
	public void deveDevolverFilmeNaSegundAoAlugarNoSabado() throws FilmeSemEstoqueException, LocadoraException {
		
		Assume.assumeTrue(DataUtils.verificarDiaSemana(new Date(), Calendar.SATURDAY)); // Assumptions
		
		Usuario usuario = UsuarioBuilder.umUsuario().agora();
		List<Filme> filmes = Arrays.asList(FilmeBuilder.umFilme().agora());
		
		Locacao retorno = locacaoService.alugarFilme(usuario, filmes);
		
		boolean ehSegunda = DataUtils.verificarDiaSemana(retorno.getDataRetorno(), Calendar.MONDAY);
		
		Assert.assertTrue(ehSegunda);
		
		//Criação de matchers próprios para usar no assertThat
//		Assert.assertThat(retorno.getDataRetorno(), new DiaSemanaMatcher(Calendar.MONDAY));
		Assert.assertThat(retorno.getDataRetorno(), MatchersProprios.caiEm(Calendar.SUNDAY));
		Assert.assertThat(retorno.getDataRetorno(), MatchersProprios.caiNumaSegunda());
	}
	
	@Test
	public void naoDeveAlugarFilmeParaNegativadoSPC() throws FilmeSemEstoqueException, LocadoraException {
		
		Usuario usuario = UsuarioBuilder.umUsuario().agora();
		Usuario usuario2 = UsuarioBuilder.umUsuario().comNome("Usuario 2").agora();
		List<Filme> filmes = Arrays.asList(FilmeBuilder.umFilme().agora());
		
		Mockito.when(spc.possuiNegativacao(usuario)).thenReturn(true);
		
		exception.expect(LocadoraException.class);
		exception.expectMessage("Usuario negativado");
		
		
		locacaoService.alugarFilme(usuario, filmes);
		
	}
	
}
