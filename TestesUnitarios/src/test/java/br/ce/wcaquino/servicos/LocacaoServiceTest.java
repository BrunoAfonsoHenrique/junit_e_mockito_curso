package br.ce.wcaquino.servicos;

import java.util.Date;

import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;

import br.ce.wcaquino.entidades.Filme;
import br.ce.wcaquino.entidades.Locacao;
import br.ce.wcaquino.entidades.Usuario;
import br.ce.wcaquino.utils.DataUtils;

public class LocacaoServiceTest {

	@Test
	public void teste() {

		// Cenario
		LocacaoService locacaoService = new LocacaoService();
		Usuario usuario = new Usuario();
		Filme filme = new Filme("Harry Potter", 2, 5.0);

		// A��o
		Locacao locacao = locacaoService.alugarFilme(usuario, filme);

		// Verifica��o
		Assert.assertThat(locacao.getValor(), CoreMatchers.is(5.0)); // Verifique que o valor da loca�ao � 5.0
		Assert.assertThat(locacao.getValor(), CoreMatchers.is(CoreMatchers.equalTo(5.0))); // Verifique que o valor da loca��o � igual a 5.0
		Assert.assertThat(locacao.getValor(), CoreMatchers.is(CoreMatchers.not(6.0))); // Verifique que o valor da loca��o n�o � 6.0
		Assert.assertThat(DataUtils.isMesmaData(locacao.getDataLocacao(), new Date()), CoreMatchers.is(true));
		Assert.assertThat(DataUtils.isMesmaData(locacao.getDataRetorno(), DataUtils.obterDataComDiferencaDias(1)), CoreMatchers.is(true));
	}

}
