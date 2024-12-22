package com.example.cbox.controller;

import com.example.cbox.annotation.ValidatedController;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

@ValidatedController
@RequestMapping(value = "/logs")
@RequiredArgsConstructor
@Slf4j
public class LogController {

    @PostMapping
    public ResponseEntity<?> verify(
            @RequestBody String logMessage
    ) {
        log.info(logMessage);

        try (PrintWriter pw = new PrintWriter(new FileWriter("log.txt", true))) {
            pw.println(logMessage);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return ResponseEntity.ok().build();
    }
}
