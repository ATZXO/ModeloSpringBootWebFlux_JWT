package com.atz.webflux.config;

import com.atz.webflux.dto.ClientDTO;
import com.atz.webflux.dto.InvoiceDTO;
import com.atz.webflux.model.Client;
import com.atz.webflux.model.Invoice;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDate;

@Configuration
public class MapperConfig {

    @Bean
    public ModelMapper defaultMapper() {
        return new ModelMapper();
    }

    @Bean("clientMapper")
    public ModelMapper clientMapper() {
        ModelMapper modelMapper = new ModelMapper();

        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);

        //READ
        modelMapper.createTypeMap(Client.class, ClientDTO.class)
                .addMapping(Client::getFirstName, (dest, v) -> dest.setName((String) v))
                .addMapping(Client::getBirthDate, (dest, v) -> dest.setBirthDateClient((LocalDate) v))
                .addMapping(Client::getUrlPhoto, (dest, v) -> dest.setPicture((String) v));

        //WRITE
        modelMapper.createTypeMap(ClientDTO.class, Client.class)
                .addMapping(ClientDTO::getName, (dest, v) -> dest.setFirstName((String) v))
                .addMapping(ClientDTO::getBirthDateClient, (dest, v) -> dest.setBirthDate((LocalDate) v))
                .addMapping(ClientDTO::getPicture, (dest, v) -> dest.setUrlPhoto((String) v));

        return modelMapper;
    }

    @Bean("invoiceMapper")
    public ModelMapper invoiceMapper() {
        ModelMapper modelMapper = new ModelMapper();

        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);

        //READ
        modelMapper.createTypeMap(Invoice.class, InvoiceDTO.class)
                .addMapping(e -> e.getClient().getFirstName(), (dest, v) -> dest.getClient().setName((String) v));

        //WRITE
        modelMapper.createTypeMap(InvoiceDTO.class, Invoice.class)
                .addMapping(e -> e.getClient().getName(), (dest, v) -> dest.getClient().setFirstName((String) v));

        return modelMapper;
    }
}














