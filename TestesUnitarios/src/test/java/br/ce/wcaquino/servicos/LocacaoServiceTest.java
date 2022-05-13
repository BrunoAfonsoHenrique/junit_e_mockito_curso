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
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import br.ce.wcaquino.builders.FilmeBuilder;
import br.ce.wcaquino.builders.LocacaoBuilder;
import br.ce.wcaquino.builders.UsuarioBuilder;
import br.ce.wcaquino.daos.LocacaoDAO;
import br.ce.wcaquino.entidades.Filme;
import br.ce.wcaquino.entidades.Locacao;
import br.ce.wcaquino.entidades.Usuario;
import br.ce.wcaquino.exceptions.FilmeSemEstoqueException;
import br.ce.wcaquino.exceptions.LocadoraException;
import br.ce.wcaquino.matchers.MatchersProprios;
import br.ce.wcaquino.utils.DataUtils;

public class LocacaoServiceTest {
	
	@InjectMocks
	private LocacaoService locacaoService;
	
	@Mock
	private LocacaoDAO dao;
	
	@Mock
	private SPCService spc;
	
	@Mock
	private EmailService emailService;
		
	@Rule
	public ErrorCollector errorCollector= new ErrorCollector();
	
	@Rule
    public ExpectedException exception = ExpectedException.none();
	
	@SuppressWarnings("deprecation")
	@Before
	public void init() {
		
		MockitoAnnotations.initMocks(this);
		
		
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
	public void naoDeveAlugarFilmeParaNegativadoSPC() throws Exception  {
		
		Usuario usuario = UsuarioBuilder.umUsuario().agora();
		//Usuario usuario2 = UsuarioBuilder.umUsuario().comNome("Usuario 2").agora();
		List<Filme> filmes = Arrays.asList(FilmeBuilder.umFilme().agora());
		
		Mockito.when(spc.possuiNegativacao(Mockito.any(Usuario.class))).thenReturn(true);
		
		
		try {
			locacaoService.alugarFilme(usuario, filmes);
			Assert.fail();
		} catch (LocadoraException e) {
			Assert.assertThat(e.getMessage(), CoreMatchers.is("Usuario negativado"));
			e.printStackTrace();
		}
		
		Mockito.verify(spc).possuiNegativacao(usuario);
		
	}
	
	@Test
	public void deveEnviarEmailParaLocacoesAtrasadas() {
		
		Usuario usuario = UsuarioBuilder.umUsuario().agora();
		Usuario usuario2 = UsuarioBuilder.umUsuario().comNome("Usuario em dia").agora();
		Usuario usuario3 = UsuarioBuilder.umUsuario().comNome("Outro atrasado").agora();
		
		List<Locacao> locacoes = Arrays.asList(
						LocacaoBuilder.umLocacao().atrasada().comUsuario(usuario).atrasada().agora(),
						LocacaoBuilder.umLocacao().comUsuario(usuario2).agora(),
						LocacaoBuilder.umLocacao().atrasada().comUsuario(usuario3).atrasada().agora(),
						LocacaoBuilder.umLocacao().atrasada().comUsuario(usuario3).atrasada().agora());
		
		Mockito.when(dao.obterLocacoesPendentes()).thenReturn(locacoes);
		
		locacaoService.notificarAtrasos();
		
		Mockito.verify(emailService, Mockito.times(3)).notificarAtraso(Mockito.any(Usuario.class)); // Qualquer instancia da classe usuário // any não serve apenas para objetos, serve para tipos primiticos tbm
		Mockito.verify(emailService).notificarAtraso(usuario);
		Mockito.verify(emailService, Mockito.atLeastOnce()).notificarAtraso(usuario3);
		Mockito.verify(emailService, Mockito.never()).notificarAtraso(usuario2);
		Mockito.verifyNoMoreInteractions(emailService);
	
	}
	
	@Test
	public void deveTratarErroNoSPC() throws Exception {
		
		//Cenario
		Usuario usuario = UsuarioBuilder.umUsuario().agora();
		List<Filme> filmes = Arrays.asList(FilmeBuilder.umFilme().agora());
		
		//Expectativa
		Mockito.when(spc.possuiNegativacao(usuario)).thenThrow(new Exception("Falha catastrofica"));
		
		//Verificação
		exception.expect(LocadoraException.class);
//		exception.expectMessage("Falha catastrofica");
		exception.expectMessage("Problemas com SPC, tente novamente");
		
		//Ação
		locacaoService.alugarFilme(usuario, filmes);
		
		
	}
	
}
