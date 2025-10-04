package com.atz.webflux.service.impl;

import com.atz.webflux.model.Invoice;
import com.atz.webflux.model.InvoiceDetail;
import com.atz.webflux.repository.ClientRepository;
import com.atz.webflux.repository.DishRepository;
import com.atz.webflux.repository.GenericRepository;
import com.atz.webflux.repository.InvoiceRepository;
import com.atz.webflux.service.InvoiceService;
import lombok.RequiredArgsConstructor;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class InvoiceServiceImpl extends CRUDServiceImpl<Invoice, String> implements InvoiceService {

    private final InvoiceRepository invoiceRepository;
    private final ClientRepository clientRepository;
    private final DishRepository dishRepository;

    @Override
    protected GenericRepository<Invoice, String> getRepo() {
        return invoiceRepository;
    }

    @Override
    public Mono<byte[]> generateReport(String idInvoice) {
        return invoiceRepository.findById(idInvoice)
                .flatMap(this::populateClient)
                .flatMap(this::populateItems)
                .map(this::generatePDF);
    }

    private Mono<Invoice> populateClient(Invoice invoice){
        return clientRepository.findById(invoice.getClient().getId())
                .map(client -> {
                    invoice.setClient(client);
                    return invoice;
                });
    }

    private Mono<Invoice> populateItems(Invoice invoice){
        List<Mono<InvoiceDetail>> list = invoice.getItems().stream()
                .map(item -> dishRepository.findById(item.getDish().getId())
                        .map(dish -> {
                            item.setDish(dish);
                            return item;
                        })
                ).toList();

        return Mono.when(list).thenReturn(invoice);
    }

    private byte[] generatePDF(Invoice invoice){
        try(InputStream stream = getClass().getResourceAsStream("/facturas.jrxml")){
            Map<String, Object> params = new HashMap<>();
            params.put("txt_client", invoice.getClient().getFirstName());

            JasperReport report = JasperCompileManager.compileReport(stream);
            JasperPrint print = JasperFillManager.fillReport(report, params, new JRBeanCollectionDataSource(invoice.getItems()));
            return JasperExportManager.exportReportToPdf(print);
        }catch (Exception e){
            return new byte[0];
        }
    }
}









