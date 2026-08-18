package com.icaro.simba_dj.controller.publicproducer;

import com.icaro.simba_dj.dto.response.profiles.PublicProducerResponse;
import com.icaro.simba_dj.service.publicproducer.PublicProducerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/public/producer")
public class PublicProducerController {
    @Autowired
    private PublicProducerService publicProducerService;

    @GetMapping("/{shortlink}")
    public ResponseEntity<PublicProducerResponse> getPublicProducer(@PathVariable String shortlink) {
        return ResponseEntity.ok(publicProducerService.getPublicProducer(shortlink));
    }
}
