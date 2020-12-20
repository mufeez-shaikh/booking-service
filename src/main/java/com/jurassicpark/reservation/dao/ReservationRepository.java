package com.jurassicpark.reservation.dao;

import com.jurassicpark.reservation.entities.Reservation;
import com.jurassicpark.reservation.util.BookingConstants;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import javax.persistence.LockModeType;
import java.util.Date;
import java.util.List;
import java.util.Optional;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {

    @Query("SELECT r FROM Reservation r WHERE r.status = 'CONFIRMED'" +
            " and :startDate <= r.endDate and :endDate >= r.startDate order by r.campSiteId, startDate ")
    public List<Reservation> findAllReservationsBetweenDates(@Param("startDate") Date startDate,
                                                             @Param("endDate") Date endDate);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT r FROM Reservation r WHERE r.status = 'CONFIRMED'" +
            " and :startDate <= r.endDate and :endDate >= r.startDate " +
            "and r.campSiteId = :campSiteId order by r.campSiteId, startDate ")
    public List<Reservation> findReservationsForCampSite(@Param("startDate") Date startDate,
                                                         @Param("endDate") Date endDate, @Param("campSiteId") Long campSiteId);
    public Optional<Reservation> findByIdAndStatus(Long reservationId, BookingConstants.ReseervationStatus status);
}
