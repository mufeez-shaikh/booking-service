package com.jurassicpark.reservation.rest;

import com.jurassicpark.reservation.entities.Reservation;
import com.jurassicpark.reservation.exceptions.NotAvailableException;
import com.jurassicpark.reservation.exceptions.NotFoundException;
import com.jurassicpark.reservation.models.ReservationModel;
import com.jurassicpark.reservation.service.ReservationService;
import com.jurassicpark.reservation.validation.ValidationService;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/reservation")
public class ReservationController {

    private static final Logger logger = LoggerFactory.getLogger(ReservationController.class);
    @Autowired
    ReservationService reservationService;

    @Autowired
    ValidationService validationService;

    @GetMapping("/availability")
    public ResponseEntity getAvailableSites(
            @RequestParam @DateTimeFormat(pattern="yyyy-MM-dd") Optional<Date> startDate,
            @RequestParam @DateTimeFormat(pattern="yyyy-MM-dd") Optional<Date> endDate){
        if(! startDate.isPresent() && ! endDate.isPresent()){
            startDate = Optional.of(DateTime.now().withTimeAtStartOfDay().toDate());
            endDate = Optional.of(DateTime.now().withTimeAtStartOfDay().plusDays(30).toDate());
        }
        List<String> errors = validationService.validateGetAvailableDates(startDate, endDate);
        if( ! errors.isEmpty()){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors);
        }
        return ResponseEntity.ok(reservationService.getCampsiteAvailability(startDate.get(), endDate.get()));
    }

    @GetMapping
    public ResponseEntity<List<Reservation>> getReservations(){
        return ResponseEntity.ok(reservationService.getAllReservations());
    }

    @PostMapping
    public ResponseEntity createReservation(@RequestBody ReservationModel reservationModel){
        try {
            List<String> errors = validationService.validateCreateReservationModel(reservationModel);
            if( ! errors.isEmpty()){
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors);
            }
            Long reservationId = reservationService.createReservation(reservationModel);

            return ResponseEntity.ok(reservationId);
        } catch (NotAvailableException e) {
            logger.error("reservation not available, returning error response");

            return  ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @PutMapping("/cancel/{reservationId}")
    public ResponseEntity cancelReservation(@PathVariable Long reservationId){
        try {
            return ResponseEntity.ok(reservationService.cancelReservation(reservationId));
        } catch (NotFoundException e) {
            logger.error(e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }
}
