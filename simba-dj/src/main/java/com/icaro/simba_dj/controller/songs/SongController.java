package com.icaro.simba_dj.controller.songs;

import com.icaro.simba_dj.dto.request.songs.SongRequest;
import com.icaro.simba_dj.dto.response.songs.SongResponse;
import com.icaro.simba_dj.model.users.User;
import com.icaro.simba_dj.service.songs.SongService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/song")
public class SongController {
    @Autowired
    private SongService songService;

    @PostMapping("/create")
    public ResponseEntity<?> createSong(@RequestBody SongRequest songRequest,
                                        @AuthenticationPrincipal(expression = "user") User user) {

        SongResponse songResponse = songService.createSong(songRequest, user.getUserId());
        return new  ResponseEntity<>(songResponse, HttpStatus.OK);
    }

    @PatchMapping("/update/{songId}")
    public ResponseEntity<?> updateSong(@RequestBody SongRequest songRequest,
                                        @PathVariable UUID songId,
                                        @AuthenticationPrincipal(expression = "user") User user){
        SongResponse songResponse = songService.updateSong(songRequest, user.getUserId(), songId);
        return new  ResponseEntity<>(songResponse, HttpStatus.OK);
    }

    @DeleteMapping("/delete/{songId}")
    public ResponseEntity<?> deleteSong(@PathVariable UUID songId, @AuthenticationPrincipal(expression = "user") User user){
        songService.deleteSong(user.getUserId(), songId);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("/list")
    public ResponseEntity<?> listSongs(@AuthenticationPrincipal(expression = "user") User user){
        List<SongResponse> songResponse = songService.getSong(user.getUserId());
        return new  ResponseEntity<>(songResponse, HttpStatus.OK);
    }
}
