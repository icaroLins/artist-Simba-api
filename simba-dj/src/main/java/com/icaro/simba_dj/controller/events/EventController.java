package com.icaro.simba_dj.controller.events;

import com.icaro.simba_dj.dto.request.events.EventRequest;
import com.icaro.simba_dj.dto.response.events.EventResponse;
import com.icaro.simba_dj.model.users.User;
import com.icaro.simba_dj.service.events.EventService;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/event")
public class EventController {
    @Autowired
    private EventService eventService;

    @PostMapping("/create")
    public ResponseEntity<?> createEvent(@Valid @RequestBody EventRequest eventRequest,
                                         @AuthenticationPrincipal(expression = "user") User user){
        EventResponse eventResponse = eventService.createEvent(eventRequest, user);
        return new ResponseEntity<>(eventResponse, HttpStatus.OK);
    }

    @GetMapping("/get")
    public ResponseEntity<?> getEvents( @AuthenticationPrincipal(expression = "user") User user){
        List<EventResponse> eventResponse = eventService.getEvent(user);
        return new ResponseEntity<>(eventResponse, HttpStatus.OK);
    }

    @PatchMapping("/edit/{eventId}")
    public ResponseEntity<?> updateEvent(@RequestBody EventRequest eventRequest, @PathVariable UUID eventId, @AuthenticationPrincipal(expression = "user") User user){
        EventResponse eventResponse = eventService.patchEvent(eventRequest,eventId,user);
        return new ResponseEntity<>(eventResponse, HttpStatus.OK);
    }

    @DeleteMapping("/delete/{eventId}")
    public ResponseEntity<?> deleteEvent(@PathVariable UUID eventId, @AuthenticationPrincipal(expression = "user") User user){
        eventService.deleteEvent(eventId, user);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
