package com.atz.webflux.service;

import com.atz.webflux.model.Invoice;
import reactor.core.publisher.Mono;

public interface InvoiceService extends CRUDService<Invoice, String> {
    Mono<byte[]> generateReport(String idInvoice);
}
