package br.ce.wcaquino.servicos;

import org.junit.Assert;
import org.junit.Test;

import br.ce.wcaquino.entidades.Usuario;



public class AssertivasTest {
	
	@Test
	public void test() {
		
		//verifica um booleano
		Assert.assertTrue(true);
		Assert.assertFalse(false);
		
		
		//Verificar se um valor é igual a outro (O que eu quero (Esperado) | o que eu recebo (atual) )
		Assert.assertEquals("Essa mensagem aparece caso de algum erro", 1, 1);
		Assert.assertEquals(0.51, 0.51, 0.01); // O terceiro parametro é uma margem de erro
		Assert.assertEquals(Math.PI, 3.14, 0.01);
		
		// Tipos primitivos e tipos objetos
		int i = 5;
		Integer i2 = 5;
		Assert.assertEquals(Integer.valueOf(i), i2);
		Assert.assertEquals(i, i2.intValue());
		
		//Strings
		//Assert.assertEquals("bola", "Bola");
		Assert.assertTrue("bola".equalsIgnoreCase("Bola"));
		Assert.assertNotEquals("bola", "casa");
		
		//Objeto
		//Deve implementar o equals e hashCode
		Usuario usuarioUm = new Usuario("Bruno");
		Usuario usuarioDois = new Usuario("Bruno");
		Usuario usuarioTres = usuarioDois;
		Usuario usuarioQuatro = null;
		Assert.assertEquals(usuarioUm, usuarioDois);
		
		//Comparação em nivel de instancia
		//Assert.assertSame(usuarioUm, usuarioDois);
		Assert.assertNotSame(usuarioUm, usuarioDois);
		Assert.assertSame(usuarioTres, usuarioDois);
		Assert.assertNotSame(usuarioUm, usuarioDois);
		
		//Verificar se o objeto está nulo
		Assert.assertTrue(usuarioQuatro == null);
		Assert.assertNull(usuarioQuatro);
		Assert.assertNotNull(usuarioDois);
		
		
		
	}
}
