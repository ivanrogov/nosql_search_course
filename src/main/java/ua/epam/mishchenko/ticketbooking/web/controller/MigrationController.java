package ua.epam.mishchenko.ticketbooking.web.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ua.epam.mishchenko.ticketbooking.service.MigrationService;

@RestController
@RequestMapping("/migration")
public class MigrationController {

    @Autowired
    private MigrationService migrationService;

    @PostMapping("/run")
    public String executeMigration(){
        migrationService.executeMigration();
        return "{\"status\":\"OK\"}";
    }
}
