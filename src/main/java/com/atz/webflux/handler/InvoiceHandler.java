package com.atz.webflux.handler;

import com.atz.webflux.dto.InvoiceDTO;
import com.atz.webflux.model.Invoice;
import com.atz.webflux.service.InvoiceService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.net.URI;

import static org.springframework.web.reactive.function.BodyInserters.fromValue;

@Component
@RequiredArgsConstructor
public class InvoiceHandler {

    private final InvoiceService InvoiceService;
    @Qualifier("invoiceMapper")
    private final ModelMapper modelMapper;

    public Mono<ServerResponse> findAll(ServerRequest request) {
        return ServerResponse.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(InvoiceService.findAll().map(this::convertToDto), InvoiceDTO.class);
    }

    public Mono<ServerResponse> findById(ServerRequest request) {
        String id = request.pathVariable("id");
        return InvoiceService.findById(id)
                .map(this::convertToDto)
                .flatMap(Invoice -> ServerResponse.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(fromValue(Invoice)))
                .switchIfEmpty(ServerResponse.notFound().build());
    }

    public Mono<ServerResponse> save(ServerRequest request) {
        Mono<InvoiceDTO> InvoiceDTOMono = request.bodyToMono(InvoiceDTO.class);
        return InvoiceDTOMono.flatMap(InvoiceDTO -> InvoiceService.save(convertToDocument(InvoiceDTO)))
                .map(this::convertToDto)
                .flatMap(savedInvoice -> ServerResponse
                        .created(URI.create(request.uri().toString().concat("/").concat(savedInvoice.getId())))
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(fromValue(savedInvoice)));
    }

    public Mono<ServerResponse> update(ServerRequest request) {
        String id = request.pathVariable("id");
        Mono<InvoiceDTO> InvoiceDTOMono = request.bodyToMono(InvoiceDTO.class);

        return InvoiceDTOMono
                .flatMap(InvoiceDTO -> {
                    InvoiceDTO.setId(id);
                    return InvoiceService.update(id, convertToDocument(InvoiceDTO));
                })
                .map(this::convertToDto)
                .flatMap(Invoice -> ServerResponse.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(fromValue(Invoice)))
                .switchIfEmpty(ServerResponse.notFound().build());
    }

    public Mono<ServerResponse> delete(ServerRequest request) {
        String id = request.pathVariable("id");
        return InvoiceService.deleteById(id)
                .flatMap(result -> {
                    if (result) {
                        return ServerResponse.noContent().build();
                    }else {
                        return ServerResponse.notFound().build();
                    }
                });
    }

    private InvoiceDTO convertToDto(Invoice Invoice) {
        return modelMapper.map(Invoice, InvoiceDTO.class);
    }

    private Invoice convertToDocument(InvoiceDTO InvoiceDTO) {
        return modelMapper.map(InvoiceDTO, Invoice.class);
    }
}
















