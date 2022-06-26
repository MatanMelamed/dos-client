package com.ermetic.dosclient.input_listener;

import com.ermetic.dosclient.dos_client.IDosService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Component;

import java.util.Scanner;

@Component
public class InputListener implements CommandLineRunner {

    @Autowired
    IDosService dosClientService;

    @Autowired
    private ConfigurableApplicationContext context;


    @Override
    public void run(String... args) {
        Scanner scanner = new Scanner(System.in);
        int httpClientsCount = -1;
        while (httpClientsCount < 0) {
            System.out.println("Enter the number of Dos clients:");
            String userInputString = scanner.next();
            try {
                httpClientsCount = Integer.parseInt(userInputString);
            } catch (NumberFormatException e) {
                System.out.println("Invalid input - Dos clients count must be a number, received: " + userInputString);
            }
        }

        dosClientService.startDosClients(httpClientsCount);
        System.out.println("Press Enter to stop all Dos clients");
        scanner.nextLine(); // first will consume enter from above
        scanner.nextLine();

        dosClientService.stopAllDosClients();
        context.close();
    }
}