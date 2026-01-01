package com.todate.backend.spot.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.todate.backend.spot.client.KakaoMapClient;
import com.todate.backend.spot.dto.KaKaoSearchResponseDto;
import com.todate.backend.spot.dto.KaKaoSearchResponseDto.Document;
import com.todate.backend.spot.dto.SpotRequestDto;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SpotService {

    private final KakaoMapClient kakaoMapClient;

    public List<Document> searchByKeyword(SpotRequestDto requestDto) {
        KaKaoSearchResponseDto response = kakaoMapClient.searchByKeyword(requestDto.getKeyword(), requestDto.getRect());
        return getSafeDocuments(response);
    }

    public List<Document> searchByPlace(SpotRequestDto requestDto) {
        return searchByKeyword(requestDto);
    }

    private List<Document> getSafeDocuments(KaKaoSearchResponseDto response) {
        if (response == null || response.getDocuments() == null) {
            return List.of();
        }
        return response.getDocuments();

    }
}
