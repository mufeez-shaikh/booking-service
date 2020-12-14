package com.jurassicpark.reservation.rest;

import com.jurassicpark.reservation.entities.CampSite;
import com.jurassicpark.reservation.service.CampSiteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/camp-site")
public class CampSiteController {

    @Autowired
    CampSiteService campSiteService;

    @GetMapping("/health-check")
    public ResponseEntity<String> healthCheck(){
        return ResponseEntity.ok("SUCCESS");
    }

    @GetMapping("/{campId}")
    public ResponseEntity<CampSite> getSiteById(@PathVariable long campId){
        Optional<CampSite> campSiteOptional = campSiteService.getSiteById(campId);
        return campSiteOptional
                .map(c -> ResponseEntity.ok(c))
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("{siteName}")
    public ResponseEntity<CampSite> post(@PathVariable String siteName){
        return ResponseEntity.ok(campSiteService.createIfNotExist(siteName));
    }

    @GetMapping
    public ResponseEntity<List<CampSite>> get(){
        return  ResponseEntity.ok(campSiteService.getAllActiveSites());
    }
}
