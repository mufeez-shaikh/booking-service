package com.jurassicpark.reservation.validation;

import com.jurassicpark.reservation.models.ReservationModel;
import org.joda.time.DateTime;
import org.joda.time.Days;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class ValidationService {

    public static final String MSG_RESERVE_MAX_THIRTY_DAYS = "Invalid input: reservation can be made max 30 days in advance";
    public static final String MSG_TODAY_RESERVATION = "Invalid input: booking can't be made for todays date.";
    public static final String MSG_RESERVATION_MAX_THREE_DAYS = "Invalid input: reservation can be made for max of 3 days";
    public static final String MSG_PAST_DATES_PROVIDED = "Invalid input: past dates are provided";
    private String MSG_START_AFTER_END = "Invalid input: startDate is after endDate";

    public List<String> validateGetAvailableDates(Optional<Date> startDateOpt, Optional<Date> endDateOpt) {
        List<String> errors = new ArrayList<>();
        if (!startDateOpt.isPresent() || !endDateOpt.isPresent()) {
            errors.add("one of the input is null");
            return errors;
        }
        DateTime startD = new DateTime(startDateOpt.get()).withTimeAtStartOfDay();
        DateTime endD = new DateTime(endDateOpt.get()).withTimeAtStartOfDay();

        if (startD.isAfter(endD)) {
            errors.add(MSG_START_AFTER_END);
        }

        if (Days.daysBetween(startD, endD).getDays() > 30) {
            errors.add(MSG_RESERVE_MAX_THIRTY_DAYS);
        }

        if (startD.isBefore(DateTime.now().withTimeAtStartOfDay())
                || endD.isBefore(DateTime.now().withTimeAtStartOfDay())) {
            errors.add(MSG_PAST_DATES_PROVIDED);
        }

        return errors;
    }

    public List<String> validateCreateReservationModel(ReservationModel reservationModel) {
        List<String> errors = new ArrayList<>();
        DateTime startD = DateTime.parse(reservationModel.getStartDate()).withTimeAtStartOfDay();
        DateTime endD = DateTime.parse(reservationModel.getEndDate()).withTimeAtStartOfDay();
        DateTime today = DateTime.now().withTimeAtStartOfDay();
        int daysBetweenTodayStart = Days.daysBetween(today, startD).getDays();
        int daysBetweenTodayEnd = Days.daysBetween(today, endD).getDays();
        int daysBetweenStartEnd = Days.daysBetween(startD, endD).getDays();

        if (startD.isBefore(today.withTimeAtStartOfDay())
                || endD.isBefore(today.withTimeAtStartOfDay())) {
            errors.add(MSG_PAST_DATES_PROVIDED);
        }

        if ((startD.isAfter(today) && daysBetweenTodayStart + 1 > 30) ||
                (endD.isAfter(today) && daysBetweenTodayEnd + 1 > 30)) {
            errors.add(MSG_RESERVE_MAX_THIRTY_DAYS);
        }
        if (startD.isAfter(endD)) {
            errors.add(MSG_START_AFTER_END);
        }

        if (daysBetweenTodayStart == 0 || daysBetweenTodayEnd== 0) {
            errors.add(MSG_TODAY_RESERVATION);
        }

        if (daysBetweenStartEnd + 1 > 3) {
            errors.add(MSG_RESERVATION_MAX_THREE_DAYS);
        }

        return errors;
    }
}
