package br.ce.wcaquino.servicos;

import static br.ce.wcaquino.utils.DataUtils.adicionarDias;

import java.util.Date;
import java.util.List;

import br.ce.wcaquino.entidades.Filme;
import br.ce.wcaquino.entidades.Locacao;
import br.ce.wcaquino.entidades.Usuario;
import br.ce.wcaquino.exceptions.FilmeSemEstoqueException;
import br.ce.wcaquino.exceptions.LocadoraException;

public class LocacaoService {

	public Locacao alugarFilme(Usuario usuario, List<Filme> filmes) throws FilmeSemEstoqueException, LocadoraException {

		// Regra de neg�cio: - Usuario e filme s�o obrigat�rios
		if (usuario == null) {

			throw new LocadoraException("Usuario vazio");
		}

		if (filmes == null || filmes.isEmpty()) {

			throw new LocadoraException("Filme vazio");
		}
		
		for(Filme filmesLista : filmes) {
			if (filmesLista.getEstoque() == 0) {

				throw new FilmeSemEstoqueException();
			}
		}
		

		Locacao locacao = new Locacao();
		locacao.setFilmes(filmes);
		locacao.setUsuario(usuario);
		locacao.setDataLocacao(new Date());
		Double valorTotalFilme = 0d;
		for(Filme filmesLista : filmes) {
			valorTotalFilme += filmesLista.getPrecoLocacao();
		}
		locacao.setValor(valorTotalFilme);

		// Entrega no dia seguinte
		Date dataEntrega = new Date();
		dataEntrega = adicionarDias(dataEntrega, 1);
		locacao.setDataRetorno(dataEntrega);

		return locacao;
	}

}