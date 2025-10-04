package com.atz.webflux.config;

import com.atz.webflux.handler.ClientHandler;
import com.atz.webflux.handler.DishHandler;
import com.atz.webflux.handler.InvoiceHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RequestPredicates.GET;
import static org.springframework.web.reactive.function.server.RequestPredicates.POST;
import static org.springframework.web.reactive.function.server.RequestPredicates.PUT;
import static org.springframework.web.reactive.function.server.RequestPredicates.DELETE;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@Configuration
public class RouterConfig {

    @Bean
    public RouterFunction<ServerResponse> dishRoutes(DishHandler dishHandler) {
        return route(GET("/v2/dishes"), dishHandler::findAll) //req -> dishHandler.findAll(req)
                .andRoute(GET("/v2/dishes/{id}"), dishHandler::findById)
                .andRoute(POST("/v2/dishes"), dishHandler::save)
                .andRoute(PUT("/v2/dishes/{id}"), dishHandler::update)
                .andRoute(DELETE("/v2/dishes/{id}"), dishHandler::delete);
    }

    @Bean
    public RouterFunction<ServerResponse> clientRoutes(ClientHandler clientHandler){
        return route(GET("/v2/clients"), clientHandler::findAll)
                .andRoute(GET("/v2/clients/{id}"), clientHandler::findById)
                .andRoute(POST("/v2/clients"), clientHandler::save)
                .andRoute(PUT("/v2/clients/{id}"), clientHandler::update)
                .andRoute(DELETE("/v2/clients/{id}"), clientHandler::delete);
    }

    @Bean
    public RouterFunction<ServerResponse> invoiceRoutes(InvoiceHandler invoiceHandler){
        return route(GET("/v2/invoices"), invoiceHandler::findAll)
                .andRoute(GET("/v2/invoices/{id}"), invoiceHandler::findById)
                .andRoute(POST("/v2/invoices"), invoiceHandler::save)
                .andRoute(PUT("/v2/invoices/{id}"), invoiceHandler::update)
                .andRoute(DELETE("/v2/invoices/{id}"), invoiceHandler::delete);
    }
}











