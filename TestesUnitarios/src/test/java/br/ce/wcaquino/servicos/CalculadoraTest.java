package br.ce.wcaquino.servicos;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import br.ce.wcaquino.exceptions.NaoPodeDividirPorZeroException;

public class CalculadoraTest {
	
	Calculadora calculadora;
	
	@Before
	public void init() {
		
		calculadora = new Calculadora();
	}
	
	@Test
	public void deve_somar_dois_valores() {
		
		int a = 5;
		int b = 3;
		
		int resultado = calculadora.somar(a, b);
		
		Assert.assertEquals(8, resultado);
	}
	
	@Test
	public void deve_subtrair_dois_valores() {
		
		int a = 5;
		int b = 3;
		
		int resultado = calculadora.subtrair(a, b);
		
		Assert.assertEquals(2, resultado);
	}
	
	@Test
	public void deve_dividir_dois_valores() throws NaoPodeDividirPorZeroException {
		
		int a = 6;
		int b = 3;
		
		int resultado = calculadora.dividir(a, b);
		
		Assert.assertEquals(2, resultado);
	}
	
	@Test(expected = NaoPodeDividirPorZeroException.class)
	public void deve_lanca_excecao_aoDividirPorZero() throws NaoPodeDividirPorZeroException {
		 int a = 10;
		 int b = 0;
		 
		 calculadora.dividir(a, b);
		
	}
	
}








