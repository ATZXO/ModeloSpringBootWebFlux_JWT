package com.atz.webflux.controller;

import com.atz.webflux.dto.InvoiceDTO;
import com.atz.webflux.model.Invoice;
import com.atz.webflux.service.InvoiceService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.net.URI;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/invoices")
public class InvoiceController {

    private final InvoiceService InvoiceService;
    @Qualifier("invoiceMapper")
    private final ModelMapper modelMapper;

    @GetMapping
    private Mono<ResponseEntity<Flux<InvoiceDTO>>> findAllInvoice() {
        Flux<InvoiceDTO> invoices = InvoiceService.findAll().map(this::convertToDto);
        return Mono.just(
                ResponseEntity.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(invoices)
        ).defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @GetMapping("/{id}")
    public Mono<ResponseEntity<InvoiceDTO>> findById(@PathVariable String id) {
        return InvoiceService.findById(id)
                .map(this::convertToDto)
                .map(e -> ResponseEntity.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(e)
                ).defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @PostMapping
    public Mono<ResponseEntity<InvoiceDTO>> saveInvoice(@Valid @RequestBody InvoiceDTO invoiceDTO, final ServerHttpRequest request) {
        return InvoiceService.save(convertToDocument(invoiceDTO)).
                map(this::convertToDto).
                map(savedInvoice -> ResponseEntity.created(URI.create(request.getURI() + "/" + savedInvoice.getId()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(savedInvoice));
    }

    @PutMapping("/{id}")
    public Mono<ResponseEntity<InvoiceDTO>> updateInvoice(@Valid @PathVariable String id, @RequestBody InvoiceDTO invoiceDTO) {
        return Mono.just(invoiceDTO)
                .flatMap(e -> {
                    e.setId(id);
                    return InvoiceService.update(id, convertToDocument(e));
                })
                .map(this::convertToDto)
                .map(updatedInvoice -> ResponseEntity.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(updatedInvoice)
                ).defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<Boolean>> deleteById(@PathVariable String id) {
        return InvoiceService.deleteById(id)
                .flatMap(result -> {
                    if (result) {
                        return Mono.just(ResponseEntity.noContent().build());
                    } else {
                        return Mono.just(ResponseEntity.notFound().build());
                    }
                });
    }

    @GetMapping("/report/{id}")
    public Mono<ResponseEntity<byte[]>> generateReport(@PathVariable String id) {
        return InvoiceService.generateReport(id)
                .map(bytes -> ResponseEntity
                        .ok()
                        .contentType(MediaType.APPLICATION_PDF)
                        .body(bytes)
                ).defaultIfEmpty(ResponseEntity.notFound().build());
    }

    private InvoiceDTO convertToDto(Invoice Invoice) {
        return modelMapper.map(Invoice, InvoiceDTO.class);
    }

    private Invoice convertToDocument(InvoiceDTO InvoiceDTO) {
        return modelMapper.map(InvoiceDTO, Invoice.class);
    }
}








