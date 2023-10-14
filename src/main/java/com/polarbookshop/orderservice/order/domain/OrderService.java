package com.polarbookshop.orderservice.order.domain;

import com.polarbookshop.orderservice.book.Book;
import com.polarbookshop.orderservice.book.BookClient;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class OrderService {
    private final OrderRepository orderRepository;
    private final BookClient bookClient;

    public OrderService(OrderRepository orderRepository, BookClient bookClient) {
        this.orderRepository = orderRepository;
        this.bookClient = bookClient;
    }

    public Flux<Order> getAllOrder() {
        return orderRepository.findAll();
    }

    /**
     * 1 Liga para o Serviço de Catálogo para verificar a disponibilidade do livro.
     * 2 Se o livro estiver disponível, ele aceita o pedido.
     * 3 Se o livro não estiver disponível, rejeita o pedido.
     * 5 Salva o pedido (como aceito ou rejeitado)
     * 6 Quando um pedido é aceito, especificamos ISBN, nome do livro (título + autor), quantidade e status.
     * Spring Data se encarrega de adicionar o identificador, a versão e os metadados de auditoria.
     *
     * @param isbn
     * @param quantity
     * @return Mono
     */
    public Mono<Order> submitOrder(String isbn, int quantity) {
        return bookClient.getBookByIsbn(isbn)
                .map(book -> buildAcceptedOrder(book, quantity))
                .defaultIfEmpty(buildRejectedOrder(isbn, quantity))
                .flatMap(orderRepository::save);
    }

    public Mono<Void> deleteBook(Long id) {
        return orderRepository.deleteById(id);
    }


    /**
     * O metodo just cria um Mono do tipo especificado com os valores passados em seus argumentos.
     * <p>
     * //Mono.just() permite criar um objeto Mono, buildReject retorna um objeto Order, e logo depois o flatMap transforma esse objeto
     * // em um Mono e o retorna, depois de ser salvado no banco de dados um objeto Order.
     * <p>
     * Esse metodo foi alterado depois da conexão WebClient
     * public Mono<Order> submitOrder(String bookIsbn, int quantity) {
     * return Mono.just(buildRejectedOrder(bookIsbn, quantity))
     * .flatMap(orderRepository::save);
     * }
     */
    public static Order buildAcceptedOrder(Book book, int quantity) {

        return Order.of(book.isbn(), book.title().concat(" - " + book.author()),
                book.price(), quantity, OrderStatus.ACCEPTED);
    }

    public static Order buildRejectedOrder(String bookIsbn, int quantity) {
        return Order.of(bookIsbn, null, null,
                quantity, OrderStatus.REJECTED);
    }


    public static Order deleteBooker(String isbn) {
        return Order.of(isbn, null, null, null, null);
    }
    /**
     * 1 Anotação de estereótipo que marca uma classe como um serviço gerenciado pelo Spring
     * 2 Um Flux é usado para publicar vários pedidos (0..N)
     *
     */

}
