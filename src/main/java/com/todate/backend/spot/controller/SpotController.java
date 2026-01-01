package com.todate.backend.spot.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.todate.backend.spot.dto.KaKaoSearchResponseDto.Document;
import com.todate.backend.spot.dto.SpotRequestDto;
import com.todate.backend.spot.service.SpotService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/spot/query")
public class SpotController {

    private final SpotService spotService;

    @PostMapping("/keyword")
    public ResponseEntity<List<Document>> searchByKeyword(@RequestBody SpotRequestDto requestDto) {
        return ResponseEntity.ok(spotService.searchByKeyword(requestDto));
    }

    @PostMapping("/place")
    public ResponseEntity<List<Document>> searchByPlace(@RequestBody SpotRequestDto requestDto) {
        return ResponseEntity.ok(spotService.searchByPlace(requestDto));
    }
}
