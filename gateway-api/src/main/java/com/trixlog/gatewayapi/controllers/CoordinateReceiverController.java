package com.trixlog.gatewayapi.controllers;

import com.trixlog.gatewayapi.records.CoordinateDataRecord;
import com.trixlog.gatewayapi.services.CoordinateReceiverService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("coordinates")
public class CoordinateReceiverController {

    private final CoordinateReceiverService coordinateReceiverService;

    @PostMapping
    public ResponseEntity<Void> send(@RequestBody CoordinateDataRecord coordinateData) {
        coordinateReceiverService.send(coordinateData);
        return ResponseEntity.ok().build();
    }
}
