package com.icaro.simba_dj.controller.photos;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.icaro.simba_dj.dto.request.photos.PhotoRequest;
import com.icaro.simba_dj.dto.response.photos.PhotoResponse;
import com.icaro.simba_dj.model.users.User;
import com.icaro.simba_dj.service.photos.PhotoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/photo")
public class PhotoController {
    @Autowired
    private PhotoService photoService;

    @PostMapping(value = "/create", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> createPhoto(@RequestPart("photoImage") MultipartFile file,
                                         @AuthenticationPrincipal(expression = "user") User user,
                                         @RequestPart("photo") String photoJson) throws IOException {

        ObjectMapper mapper = new ObjectMapper();
        PhotoRequest photoRequest = mapper.readValue(photoJson, PhotoRequest.class);
        PhotoResponse photoResponse = photoService.createPhoto(photoRequest,user,file);
        return new ResponseEntity<>(photoResponse, HttpStatus.OK);
    }

    @PatchMapping(value = "/update/{photoId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> updatePhoto(@RequestPart("photo") String photoJson,
                                         @AuthenticationPrincipal(expression = "user") User user,
                                         @RequestPart(required = false) MultipartFile file,
                                         @PathVariable UUID photoId) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        PhotoRequest photoRequest = mapper.readValue(photoJson, PhotoRequest.class);
        PhotoResponse photoResponse = photoService.updatePhoto(photoRequest,user,photoId,file);
        return new ResponseEntity<>(photoResponse, HttpStatus.OK);
    }

    @DeleteMapping("/delete/{photoId}")
    public ResponseEntity<?> deletePhoto(@AuthenticationPrincipal(expression = "user") User user, @PathVariable UUID photoId){
        photoService.deletePhoto(photoId,user);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("/list")
    public ResponseEntity<?> listPhotos(@AuthenticationPrincipal(expression = "user") User user){
        List<PhotoResponse> photoResponses = photoService.getPhoto(user);
        return new ResponseEntity<>(photoResponses, HttpStatus.OK);
    }

}
