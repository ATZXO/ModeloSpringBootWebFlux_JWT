package com.atz.webflux.controller;

import com.atz.webflux.dto.ClientDTO;
import com.atz.webflux.model.Client;
import com.atz.webflux.service.ClientService;
import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.cloudinary.json.JSONObject;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import java.io.File;
import java.net.URI;
import java.nio.file.Files;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/clients")
public class ClientController {

    private final ClientService clientService;
    @Qualifier("clientMapper")
    private final ModelMapper modelMapper;
    private final Cloudinary cloudinary;

    @GetMapping
    private Mono<ResponseEntity<Flux<ClientDTO>>> findAll() {
        Flux<ClientDTO> clients = clientService.findAll().map(this::convertToDto);
        return Mono.just(
                ResponseEntity.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(clients)
        ).defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @GetMapping("/{id}")
    public Mono<ResponseEntity<ClientDTO>> findById(@PathVariable String id) {
        return clientService.findById(id)
                .map(this::convertToDto)
                .map(client -> ResponseEntity.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(client)
                ).defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @PostMapping
    public Mono<ResponseEntity<ClientDTO>> save(@Valid @RequestBody ClientDTO clientDTO, final ServerHttpRequest request) {
        return clientService.save(convertToDocument(clientDTO)).
                map(this::convertToDto).
                map(savedClient -> ResponseEntity.created(URI.create(request.getURI() + "/" + savedClient.getId()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(savedClient));
    }

    @PutMapping("/{id}")
    public Mono<ResponseEntity<ClientDTO>> update(@Valid @PathVariable String id, @RequestBody ClientDTO clientDTO) {
        return Mono.just(clientDTO)
                .flatMap(c -> {
                    c.setId(id);
                    return clientService.update(id, convertToDocument(c));
                })
                .map(this::convertToDto)
                .map(updatedClient -> ResponseEntity.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(updatedClient)
                ).defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<Boolean>> deleteById(@PathVariable String id) {
        return clientService.deleteById(id)
                .flatMap(result -> {
                    if (result) {
                        return Mono.just(ResponseEntity.noContent().build());
                    } else {
                        return Mono.just(ResponseEntity.notFound().build());
                    }
                });
    }

    //Subir foto a Cloudinary
    @PostMapping("/upload/{id}")
    public Mono<ResponseEntity<ClientDTO>> uploadPhoto(@PathVariable String id, @RequestPart("file") FilePart filePart ) {
        //Crear archivo temporal
        Mono<File> tempFileMono = Mono.fromCallable(() -> Files.createTempFile("tmp", filePart.filename()).toFile()); //Para procesos bloqueantes .subscribeOn(Schedulers.boundedElastic());

        //Buscar cliente
        Mono<Client> clientMono = clientService.findById(id);

        return tempFileMono
                .flatMap(tmpFile -> filePart.transferTo(tmpFile).thenReturn(tmpFile))
                .flatMap(tmpFile -> uploadToCloudinary(tmpFile)
                        .zipWith(clientMono, (url, client) -> {
                            client.setUrlPhoto(url);
                            return client;
                        }))
                .flatMap(client -> clientService.update(id, client))
                .map(this::convertToDto)
                .map(ResponseEntity::ok);
    }

    private Mono<String> uploadToCloudinary(File tempFile) {
        return Mono.fromCallable(() -> {
            Map<String, Object> response = cloudinary.uploader().upload(tempFile, ObjectUtils.asMap("resource_type", "auto"));
            return new JSONObject(response).getString("url");
        });
    }

    private ClientDTO convertToDto(Client client) {
        return modelMapper.map(client, ClientDTO.class);
    }

    private Client convertToDocument(ClientDTO clientDTO) {
        return modelMapper.map(clientDTO, Client.class);
    }
}



