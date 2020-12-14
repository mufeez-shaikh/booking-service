package com.jurassicpark.reservation.dao;

import com.jurassicpark.reservation.entities.CampSite;
import com.jurassicpark.reservation.util.BookingConstants;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CampSiteRepository extends JpaRepository<CampSite, Long> {

    public CampSite findBySiteName(String siteName);

    public List<CampSite> findByStatus(BookingConstants.CampSiteStatus status);
}
