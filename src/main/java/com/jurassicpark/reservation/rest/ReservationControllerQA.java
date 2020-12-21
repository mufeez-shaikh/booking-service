package com.jurassicpark.reservation.rest;


import com.jurassicpark.reservation.dao.ReservationRepository;
import com.jurassicpark.reservation.service.ReservationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.xml.ws.Response;

@RestController
@RequestMapping("/reservationQA")
public class ReservationControllerQA {

    @Autowired
    ReservationRepository reservationRepository;

    @DeleteMapping
    public ResponseEntity deleteAll(){
        reservationRepository.deleteAll();
        return ResponseEntity.ok().build();
    }

}
