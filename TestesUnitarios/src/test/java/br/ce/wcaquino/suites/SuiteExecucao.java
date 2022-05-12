package br.ce.wcaquino.suites;

import org.junit.runners.Suite.SuiteClasses;

import br.ce.wcaquino.servicos.CalculadoraTest;
import br.ce.wcaquino.servicos.CalculoValorLocacaoTeste;
import br.ce.wcaquino.servicos.LocacaoServiceTest;

//@RunWith(Suite.class)
@SuiteClasses({
	CalculadoraTest.class,
	CalculoValorLocacaoTeste.class,
	LocacaoServiceTest.class
}) // @SuiteClasses terá todos os testes que eu quero que essa suite execute
public class SuiteExecucao {
	
	//Remova se puder
}
