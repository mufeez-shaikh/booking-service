package com.jurassicpark.reservation.service;

import com.jurassicpark.reservation.dao.CampSiteRepository;
import com.jurassicpark.reservation.dao.ReservationRepository;
import com.jurassicpark.reservation.entities.CampSite;
import com.jurassicpark.reservation.entities.Reservation;
import com.jurassicpark.reservation.exceptions.NotAvailableException;
import com.jurassicpark.reservation.exceptions.NotFoundException;
import com.jurassicpark.reservation.models.AvailableCampsiteInfo;
import com.jurassicpark.reservation.models.ReservationModel;
import com.jurassicpark.reservation.util.BookingConstants;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

import static com.jurassicpark.reservation.util.BookingConstants.CampSiteStatus.ACTIVE;

@Service
public class ReservationService {

    private static final Logger logger = LoggerFactory.getLogger(ReservationService.class);

    private static DateTimeFormatter dateFormatter = DateTimeFormat.forPattern("yyyy-MM-dd");

    @Autowired
    private ReservationRepository reservationRepository;

    @Autowired
    private CampSiteRepository campSiteRepository;

    public List<Reservation> getAllReservations() {
        return reservationRepository.findAll();
    }

    public Reservation cancelReservation(Long reservationId) throws NotFoundException {
        Optional<Reservation> reservation = reservationRepository.findByIdAndStatus(reservationId, BookingConstants.ReseervationStatus.CONFIRMED);
        if (reservation.isPresent()) {
            Reservation toUpdate = reservation.get();
            toUpdate.setStatus(BookingConstants.ReseervationStatus.CANCELLED);
            return reservationRepository.save(toUpdate);
        } else {
            throw new NotFoundException("invalid booking id");
        }
    }

    @Transactional
    public void updateReservation(ReservationModel reservationModel) throws NotFoundException, NotAvailableException {
        Optional<Reservation> reservation = reservationRepository.findByIdAndStatus(reservationModel.getReservationId(), BookingConstants.ReseervationStatus.CONFIRMED);
        if (! reservation.isPresent()) {
            throw new NotFoundException("invalid booking id");
        }
        DateTime startD = DateTime.parse(reservationModel.getStartDate()).withTimeAtStartOfDay();
        DateTime endD = DateTime.parse(reservationModel.getEndDate()).withTimeAtStartOfDay();

        List<Reservation> reservationsForCampSite = reservationRepository
                .findReservationsForCampSite(startD.toDate(), endD.toDate(), reservationModel.getCampSiteId());
        if (reservationsForCampSite.isEmpty() ||
                ( reservationsForCampSite.size() ==1 &&
                        reservationsForCampSite.get(0).getId().equals(reservationModel.getReservationId()))) {
            Reservation reservationToUpdate = toEntity(reservationModel);
            reservationToUpdate.setId(reservationModel.getReservationId());
            reservationRepository.save(reservationToUpdate);
            logger.info("reservation id: "+reservationModel.getReservationId()+" is updated");
            return;
        }

        String msg = "Reservation not available between " + getDateString(startD) + " and " + getDateString(endD);
        logger.error(msg);
        throw new NotAvailableException(msg);

    }

    @Transactional
    public Long createReservation(ReservationModel reservationModel) throws NotAvailableException {

        DateTime startD = DateTime.parse(reservationModel.getStartDate()).withTimeAtStartOfDay();
        DateTime endD = DateTime.parse(reservationModel.getEndDate()).withTimeAtStartOfDay();

        List<Reservation> reservationsForCampSite = reservationRepository
                .findReservationsForCampSite(startD.toDate(), endD.toDate(), reservationModel.getCampSiteId());

        if (reservationsForCampSite.isEmpty()) {
            Reservation savedReservation = reservationRepository.save(toEntity(reservationModel));
            logger.info("reservation created. Id=" + savedReservation.getId());
            return savedReservation.getId();
        }

        String msg = "Reservation not available between " + getDateString(startD) + " and " + getDateString(endD);
        logger.error(msg);
        throw new NotAvailableException(msg);
    }

    public Collection<AvailableCampsiteInfo> getCampsiteAvailability(Date startDate, Date endDate) {
        logger.info("searching availablity between "
                + getDateString(new DateTime(startDate)) + " and "
                + getDateString(new DateTime(endDate)));
        List<Reservation> allReservations = reservationRepository.findAllReservationsBetweenDates(startDate, endDate);
        Collection<AvailableCampsiteInfo> availableCampsiteInfos = calculateAvailableDates(allReservations, startDate, endDate);
        availableCampsiteInfos.forEach(this::setAvailabilityMessage);
        return availableCampsiteInfos;
    }

    private Collection<AvailableCampsiteInfo> calculateAvailableDates(List<Reservation> reservations, Date startDate, Date endDate) {

        List<CampSite> campSites = campSiteRepository.findByStatus(ACTIVE);

        Map<Long, List<Reservation>> reservationsByCampId = reservations
                .stream()
                .collect(Collectors.groupingBy(Reservation::getCampSiteId));

        Map<Long, AvailableCampsiteInfo> availableCampsiteMap = new HashMap<>();
        campSites.forEach(c -> availableCampsiteMap.put(c.getId(),
                AvailableCampsiteInfo.builder()
                        .campSiteId(c.getId())
                        .availableDates(null)
                        .build()
        ));

        for (Long campSiteId : reservationsByCampId.keySet()) {
            DateTime rangeStart = new DateTime(startDate).withTimeAtStartOfDay();
            DateTime rangeEnd = new DateTime(endDate).withTimeAtStartOfDay();
            List<Reservation> reservationsForCampSiteId = reservationsByCampId.get(campSiteId);

            AvailableCampsiteInfo availableCampsiteInfo = availableCampsiteMap.get(campSiteId);
            if (availableCampsiteInfo.getAvailableDates() == null) {
                availableCampsiteInfo.setAvailableDates(new ArrayList<>());
            }
            setAvailabilityForCampSite(reservationsForCampSiteId, rangeStart, rangeEnd, availableCampsiteInfo);

        }

        return availableCampsiteMap.values();
    }

    private void setAvailabilityForCampSite(List<Reservation> reservationsForCampSiteId,
                                            DateTime rangeStart, DateTime rangeEnd, AvailableCampsiteInfo availableCampsiteInfo) {
        for (Reservation reservation : reservationsForCampSiteId) {
            DateTime reservationStartDate = new DateTime(reservation.getStartDate()).withTimeAtStartOfDay();
            DateTime reservationEndDate = new DateTime(reservation.getEndDate()).withTimeAtStartOfDay();
            if (!rangeStart.isBefore(reservationStartDate) && !rangeStart.isAfter(reservationEndDate)) {
                rangeStart = reservationEndDate.plusDays(1);
                logger.info("rangeStart is in reservation date's range. setting to r.endDate + 1");
                continue;
            }

            if (rangeStart.isBefore(reservationStartDate)) {
                while (rangeStart.isBefore(reservationStartDate)) {
                    availableCampsiteInfo.getAvailableDates().add(getDateString(rangeStart));
                    rangeStart = rangeStart.plusDays(1);
                }
                rangeStart = reservationEndDate.plusDays(1);
            }
        }

        logger.info("added availability from reservations");
        if (!rangeStart.isAfter(rangeEnd)) {
            while (!rangeStart.isAfter(rangeEnd)) {
                availableCampsiteInfo.getAvailableDates().add(getDateString(rangeStart));
                rangeStart = rangeStart.plusDays(1);
            }
            logger.info("added remaining days");
        }
    }

    private void setAvailabilityMessage(AvailableCampsiteInfo availableCampsiteInfo) {
        List<String> availableDates = availableCampsiteInfo.getAvailableDates();
        String availabilityMsg = null;
        if (availableDates == null) {
            availabilityMsg = "available all dates";
        } else if (availableDates.isEmpty()) {
            availabilityMsg = "no availability";
        } else {
            availabilityMsg = String.join(",", availableDates);
        }
        logger.info("campSite id: " + availableCampsiteInfo.getCampSiteId() + " with availabilityMsg: " + availabilityMsg);
        availableCampsiteInfo.setAvailabilities(availabilityMsg);
    }

    private String getDateString(DateTime dateTime) {

        String dateStr = dateTime.toString(dateFormatter);

        return dateStr;
    }

    private Reservation toEntity(ReservationModel reservationModel) {
        return Reservation.builder()
                .campSiteId(reservationModel.getCampSiteId())
                .startDate(DateTime.parse(reservationModel.getStartDate()).withTimeAtStartOfDay().toDate())
                .endDate(DateTime.parse(reservationModel.getEndDate()).withTimeAtStartOfDay().toDate())
                .bookedBy(reservationModel.getName())
                .status(BookingConstants.ReseervationStatus.CONFIRMED)
                .email(reservationModel.getEmail())
                .build();
    }

}
