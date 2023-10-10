package com.polarbookshop.orderservice.book;

import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

import java.time.Duration;

@Component
public class BookClient {

    private static final String BOOKS_ROUTE_API = "/books/";
    private final WebClient webClient;

    public BookClient(WebClient webClient) {
        this.webClient = webClient;
    }

    public Mono<Book> getBookByIsbn(String isbn) {
        return webClient
                .get()
                .uri(BOOKS_ROUTE_API + isbn)
                .retrieve()
                .bodyToMono(Book.class)
                .timeout(Duration.ofSeconds(3), Mono.empty())//Define um tempo limite de 3 segundos para a solicitação GET
                .onErrorResume
                        (WebClientResponseException.NotFound.class, exception -> Mono.empty())//Captura a resposta do tipo 404 e retorna um Mono.empty
                .retryWhen(Retry.backoff(3, Duration.ofMillis(100)))//A espera exponencial é usada como estratégia de nova tentativa. São permitidas três tentativas com tempo inicial de 100 ms
                .onErrorResume(Exception.class, exception -> Mono.empty());//Se ocorrer algum erro após as 3 novas tentativas, capture a exceção e retorne um objeto vazio.
        //Mono.empty()) O fallback retorna um objeto Mono vazio.
        /**timeout() que você pode usar para definir um limite de tempo para concluir uma operação.
         Em vez de lançar uma exceção quando o tempo limite expirar, você terá a chance de fornecer
         um comportamento de fallback. Considerando que o Order Service não pode aceitar um pedido
         se a disponibilidade do livro não for verificada, considere retornar um resultado vazio para que o
         pedido seja rejeitado. Você pode definir um resultado reativo vazio usando Mono.empty().
         */
    }

    public Mono<Book> deleteBookByIsbn(String isbn) {
        return webClient
                .delete()
                .uri(BOOKS_ROUTE_API + isbn)
                .retrieve()
                .bodyToMono(Book.class)
                .timeout(Duration.ofSeconds(3), Mono.empty()) //Define um tempo limite de 3 segundos para a solicitação GET
                .onErrorResume
                        (WebClientResponseException.NotFound.class, exception -> Mono.empty()) //Captura a resposta do tipo 404 e retorna um Mono.empty
                .retryWhen(Retry.backoff(3, Duration.ofMillis(100))) //A espera exponencial é usada como estratégia de nova tentativa. São permitidas três tentativas com tempo inicial de 100 ms
                .onErrorResume(Exception.class, exception -> Mono.empty()); //Se ocorrer algum erro após as 3 novas tentativas, capture a exceção e retorne um objeto vazio.


    }

    /**
     * 1 - Um bean WebClient conforme configurado anteriormente.
     * 2 - A solicitação deve usar o método GET.
     * 3 - O URI de destino da solicitação é  books/{isbn}.
     * 4 - Envia a solicitação e recupera a resposta
     * 5 - Retorna o objeto recuperado como Mono<Book>
     *     WebClient é um cliente HTTP reativo. Você acabou de ver como ele pode retornar dados como editores reativos.
     */
}
