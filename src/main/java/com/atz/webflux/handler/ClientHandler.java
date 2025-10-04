package com.atz.webflux.handler;

import com.atz.webflux.dto.ClientDTO;
import com.atz.webflux.model.Client;
import com.atz.webflux.service.ClientService;
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
public class ClientHandler {

    private final ClientService ClientService;
    @Qualifier("clientMapper")
    private final ModelMapper modelMapper;

    public Mono<ServerResponse> findAll(ServerRequest request) {
        return ServerResponse.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(ClientService.findAll().map(this::convertToDto), ClientDTO.class);
    }

    public Mono<ServerResponse> findById(ServerRequest request) {
        String id = request.pathVariable("id");
        return ClientService.findById(id)
                .map(this::convertToDto)
                .flatMap(Client -> ServerResponse.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(fromValue(Client)))
                .switchIfEmpty(ServerResponse.notFound().build());
    }

    public Mono<ServerResponse> save(ServerRequest request) {
        Mono<ClientDTO> ClientDTOMono = request.bodyToMono(ClientDTO.class);
        return ClientDTOMono.flatMap(ClientDTO -> ClientService.save(convertToDocument(ClientDTO)))
                .map(this::convertToDto)
                .flatMap(savedClient -> ServerResponse
                        .created(URI.create(request.uri().toString().concat("/").concat(savedClient.getId())))
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(fromValue(savedClient)));
    }

    public Mono<ServerResponse> update(ServerRequest request) {
        String id = request.pathVariable("id");
        Mono<ClientDTO> ClientDTOMono = request.bodyToMono(ClientDTO.class);

        return ClientDTOMono
                .flatMap(ClientDTO -> {
                    ClientDTO.setId(id);
                    return ClientService.update(id, convertToDocument(ClientDTO));
                })
                .map(this::convertToDto)
                .flatMap(Client -> ServerResponse.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(fromValue(Client)))
                .switchIfEmpty(ServerResponse.notFound().build());
    }

    public Mono<ServerResponse> delete(ServerRequest request) {
        String id = request.pathVariable("id");
        return ClientService.deleteById(id)
                .flatMap(result -> {
                    if (result) {
                        return ServerResponse.noContent().build();
                    }else {
                        return ServerResponse.notFound().build();
                    }
                });
    }

    private ClientDTO convertToDto(Client Client) {
        return modelMapper.map(Client, ClientDTO.class);
    }

    private Client convertToDocument(ClientDTO ClientDTO) {
        return modelMapper.map(ClientDTO, Client.class);
    }
}
















