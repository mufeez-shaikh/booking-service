package com.jurassicpark.reservation.validation;

import org.joda.time.DateTime;
import org.joda.time.Days;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class ValidationService {

    public List<String> validateReservationDates(Optional<Date> startDateOpt, Optional<Date> endDateOpt){
        List<String> errors = new ArrayList<>();
        if(! startDateOpt.isPresent() || ! endDateOpt.isPresent()){
            errors.add("one of the input is null");
            return errors;
        }
        DateTime startD = new DateTime(startDateOpt.get()).withTimeAtStartOfDay();
        DateTime endD = new DateTime(endDateOpt.get()).withTimeAtStartOfDay();

        if(startD.isAfter(endD)){
            errors.add("Invalid input. startDateOpt is greater than endDateOpt");
        }

        if(Days.daysBetween(startD,endD).getDays() >30){
            errors.add("Invalid input. reservation can be done for upto 30 days");
        }

        if(startD.isBefore(DateTime.now().withTimeAtStartOfDay())
                || endD.isBefore(DateTime.now().withTimeAtStartOfDay())){
            errors.add("Invalid input: past dates are provided");
        }

        return errors;
    }

}
