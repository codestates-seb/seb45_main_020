package com.pathfinder.server.tourinfo.service;

import com.pathfinder.server.tourinfo.entity.TourInfo;
import com.pathfinder.server.tourinfo.repository.TourInfoRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class TourInfoService {
    private final String API_KEY;
    private final TourInfoRepository tourInfoRepository;
    private final RestTemplate restTemplate;

    public TourInfoService(@Value("${open-api.tour-api.credentials.service-key}") String apiKey, TourInfoRepository tourInfoRepository, RestTemplate restTemplate) {
        API_KEY = apiKey;
        this.tourInfoRepository = tourInfoRepository;
        this.restTemplate = restTemplate;
    }

    @Transactional(readOnly = true)
    public List<TourInfo> findTourInfosByAddress(String address) {
        List<TourInfo> results = tourInfoRepository.findByAddr1Containing(address);

        Collections.shuffle(results);

        return results.stream().limit(6).collect(Collectors.toList());
    }

}
