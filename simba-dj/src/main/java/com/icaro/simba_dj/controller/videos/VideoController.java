package com.icaro.simba_dj.controller.videos;

import com.icaro.simba_dj.dto.request.videos.VideoRequest;
import com.icaro.simba_dj.dto.response.videos.VideoResponse;
import com.icaro.simba_dj.model.users.User;
import com.icaro.simba_dj.repository.videos.VideoRepository;
import com.icaro.simba_dj.service.videos.VideosService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/video")
public class VideoController {
    @Autowired
    private VideosService videosService;

    @PostMapping("/create")
    public ResponseEntity<?> createVideo(@RequestBody VideoRequest videoRequest,
                                         @AuthenticationPrincipal(expression = "user") User user){
        VideoResponse videoResponse = videosService.createVideo(videoRequest, user);
        return new ResponseEntity<>(videoResponse, HttpStatus.OK);
    }

    @PatchMapping("/update/{videoId}")
    public ResponseEntity<?> updateVideo(@RequestBody VideoRequest videoRequest,
                                         @AuthenticationPrincipal(expression = "user") User user,
                                         @PathVariable UUID videoId){
        VideoResponse videoResponse = videosService.updateVideo(videoRequest, user, videoId);
        return new ResponseEntity<>(videoResponse, HttpStatus.OK);
    }

    @GetMapping("/list")
    public ResponseEntity<?> listVideos(@AuthenticationPrincipal(expression = "user") User user){
        List<VideoResponse> videoResponse = videosService.listVideos(user);
        return new ResponseEntity<>(videoResponse, HttpStatus.OK);
    }

    @DeleteMapping("/delete/{videoId}")
    public ResponseEntity<?> deleteVideo(@PathVariable UUID videoId, @AuthenticationPrincipal(expression = "user") User user){
        videosService.deleteVideo(videoId, user);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
