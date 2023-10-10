package com.polarbookshop.orderservice.book;

import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.*;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.io.IOException;

@TestMethodOrder(MethodOrderer.Random.class)
public class BookClientTest {

    private MockWebServer mockWebServer;
    private BookClient bookClient;

    @BeforeEach
    void setup() throws IOException {
        //Inicia o servidor simulado antes de executar um caso de teste
        this.mockWebServer = new MockWebServer();
        this.mockWebServer.start();

        //Usa a URL do servidor simulado como URL base para WebClient
        var webClient = WebClient.builder()
                .baseUrl(mockWebServer.url("/").toString())
                .build();
        this.bookClient = new BookClient(webClient);
    }

    @AfterEach
    void clean() throws IOException {
        //Desliga o servidor simulado após concluir um caso de teste
        this.mockWebServer.shutdown();
    }

    //na classe BookClientTests, você pode definir alguns casos de teste para validar a funcionalidade do cliente no
    //Order Service.

    @Test
    void whenBookExistTheReturnBook() {
        var bookIsbn = "1234567890";
        //1 - Define a resposta a ser retornada pelo servidor simulado
        var mockResponse = new MockResponse()
                .addHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .setBody("""
                        {
                          "isbn": %s,
                          "title": "Title",
                          "author": "author",
                          "price": 9.90,
                          "publicsher": "Polarshophia"    
                        }
                        """.formatted(bookIsbn));

        //2 - Adiciona uma resposta simulada à fila processada pelo servidor simulado
        mockWebServer.enqueue(mockResponse);
        Mono<Book> book = bookClient.getBookByIsbn(bookIsbn);

        //Inicializa um objeto StepVerifier com o objeto retornado por BookClient
        StepVerifier.create(book)
                //Afirma que o livro retornado tem o ISBN solicitado
                .expectNextMatches(b -> b.isbn().equals(bookIsbn))
                .verifyComplete();//Verifica se o reativo transmissão concluída com sucesso
    }

}
