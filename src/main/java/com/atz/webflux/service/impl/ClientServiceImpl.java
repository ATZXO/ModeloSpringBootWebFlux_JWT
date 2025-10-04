package com.atz.webflux.service.impl;

import com.atz.webflux.model.Client;
import com.atz.webflux.repository.ClientRepository;
import com.atz.webflux.repository.GenericRepository;
import com.atz.webflux.service.ClientService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ClientServiceImpl extends CRUDServiceImpl<Client, String> implements ClientService {

    private final ClientRepository clientRepository;

    @Override
    protected GenericRepository<Client, String> getRepo() {
        return clientRepository;
    }
}








