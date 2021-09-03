package ecommercemarcelodebittencourt.tests;

import ecommercemarcelodebittencourt.pageobjects.*;
import ecommercemarcelodebittencourt.setup.Driver;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import java.util.List;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class HomePageTests {

    private WebDriver driver;
    private ProdutoPage produtoPage;

    //Executado anterior a todos os processos de testes
    @Before
    public void CriarDriver() {
        Driver wdriver = new Driver();
        this.driver = wdriver.WebDriver();
    }

    //Executado ao final de todos os processos de testes
    @After
    public void EncerrarDriver() {
        driver.close();
        driver.quit();
    }

    //Campo de Testes
    HomePage homepage;
    @Test
    public void ContarProdutos() {
        int produtos = 8;
        homepage = new HomePage(driver);
        homepage.contarProdutos(produtos);
    }

    @Test
    public void CarrinhoVazio() {
        int carrinhoValor = 0;
        HomePage homepage = new HomePage(driver);
        homepage.carrinhoVazio(carrinhoValor);
    }

    String nomeProduto_ProdutoPage;
    @Test
    public void DetalhesProduto() {
        int indice = 0;
        homepage = new HomePage(driver);
        String nomeProduto_HomePage = homepage.obterNomeProduto(indice);
        String precoProduto_HomePage = homepage.obterPrecoProduto(indice);

        System.out.println(nomeProduto_HomePage);
        System.out.println(precoProduto_HomePage);

        ProdutoPage produtoPage = homepage.clicarProduto(indice);
        nomeProduto_ProdutoPage = produtoPage.obterNomeProduto();
        String precoProduto_ProdutoPage = produtoPage.obterPrecoProduto();

        System.out.println(nomeProduto_ProdutoPage);
        System.out.println(precoProduto_ProdutoPage);

        //Validando nomes e preços
        assertEquals(nomeProduto_HomePage.toUpperCase(),nomeProduto_ProdutoPage.toUpperCase());
        assertEquals(precoProduto_HomePage.toUpperCase(), precoProduto_ProdutoPage.toUpperCase());
    }

    LoginPage loginPage;
    @Test
    public void LoginValido() {
        homepage = new HomePage(driver);

        //Clicar no botão Sign In na Homepage
        loginPage = homepage.clicarBotaoSignIn();

        //Preencher campos
        loginPage.preencherEmail("acedospunhosbemquentinho@gmail.com");
        loginPage.preencherSenha("BarbaBranca<3");

        //Clicar no botão Sign In para logar
        loginPage.clicarBotaoSignIn();

        //Validar se o usuário está logado corretamente
        assertTrue(homepage.estaLogado("Portgas D. Ace"));

        //Voltar página inicial
        driver.findElement(By.cssSelector("[alt='Loja de Teste']")).click();
    }
    ModalProdutoPage modalProdutoPage;
    @Test
    public void IncluirProdutoAoCarrinho() {
        homepage = new HomePage(driver);
        produtoPage = new ProdutoPage(driver);
        modalProdutoPage  = new ModalProdutoPage(driver);

        String tamanhoProduto = "L";
        String corProduto = "Black";
        int quantidadeProduto = 2;

        //--Pré-condição
        //Usuário logado
        if(!homepage.estaLogado("Portgas D. Ace")) {
            LoginValido();
        }

        //--Teste
        //Selecionar Produto
        DetalhesProduto();

        //Selecionar Tamanho
        List<String> listaOpcoes = produtoPage.obterOpcoesSelecionadas();

        System.out.println(listaOpcoes.get(0));
        System.out.println("Tamanho da lista: " + listaOpcoes.size());

        produtoPage.selecionarOpcaoDropdown(tamanhoProduto);
        listaOpcoes = produtoPage.obterOpcoesSelecionadas();

        System.out.println(listaOpcoes.get(0));
        System.out.println("Tamanho da lista: " + listaOpcoes.size());

        //Selecionar Cor
        produtoPage.alterarCor();

        //Selecionar quantidade
        produtoPage.alterarQuantidade(Integer.toString(quantidadeProduto));

        //Adicionar ao Carrinho
        ModalProdutoPage modalProdutoPage = produtoPage.clicarAddToCart();

        //Validações
        assertTrue(modalProdutoPage.obterMensagemProdutoAdcionado().endsWith("Product successfully added to your shopping cart"));
        assertEquals(modalProdutoPage.obterTamanhoProduto(), tamanhoProduto);
        assertEquals(modalProdutoPage.obterCorProduto(), corProduto);
        assertEquals(modalProdutoPage.obterQuantidadeProduto(),Integer.toString(quantidadeProduto));

        assertEquals(modalProdutoPage.obterDescricaoProduto().toLowerCase(), nomeProduto_ProdutoPage.toLowerCase());

        String precoProdutoString = modalProdutoPage.obterPrecoProduto();
        precoProdutoString = precoProdutoString.replace("$","");
        Double precoProduto = Double.parseDouble(precoProdutoString);
        System.out.println("Preço produto = " + precoProduto);

        String subtotalString = modalProdutoPage.obterSubtotal();
        subtotalString = subtotalString.replace("$","");
        Double subtotal = Double.parseDouble(subtotalString);
        System.out.println("Preço subtotal = " + subtotal);

        Double subtotalCalculado = quantidadeProduto * precoProduto;
        System.out.println(subtotalCalculado);
        assertEquals(subtotal, subtotalCalculado);

    }

    CarrinhoPage carrinhoPage;
    @Test
    public void irParaCarrinho() {
        //--Pré-condições
        //Produto incluido na tela ModalProdutoPage
        homepage = new HomePage(driver);
        produtoPage = new ProdutoPage(driver);
        modalProdutoPage  = new ModalProdutoPage(driver);

        IncluirProdutoAoCarrinho();
        carrinhoPage = modalProdutoPage.clicarBotaoProceedToCheckout();

    }
}
