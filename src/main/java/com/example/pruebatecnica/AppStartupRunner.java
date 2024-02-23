package com.example.pruebatecnica;

import com.example.pruebatecnica.services.DataLoaderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class AppStartupRunner implements CommandLineRunner {
    @Autowired
    private DataLoaderService dataLoaderService;

    @Override
    public void run(String... args) throws Exception {
        dataLoaderService.loadInitialData();
    }
}
