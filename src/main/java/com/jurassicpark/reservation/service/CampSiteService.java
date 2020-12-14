package com.jurassicpark.reservation.service;

import com.jurassicpark.reservation.dao.CampSiteRepository;
import com.jurassicpark.reservation.entities.CampSite;
import com.jurassicpark.reservation.util.BookingConstants;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.Random;

import static com.jurassicpark.reservation.util.BookingConstants.CampSiteStatus.ACTIVE;

@Service
public class CampSiteService {
    private static final Logger logger = LoggerFactory.getLogger(CampSiteService.class);

    @Autowired
    private CampSiteRepository campSiteRepository;


    public CampSite createIfNotExist(String siteName){
        CampSite bySiteName = campSiteRepository.findBySiteName(siteName);
        if(bySiteName != null){
            return bySiteName;
        }
        CampSite campSite = CampSite.builder().siteName(siteName).status(ACTIVE).build();
        logger.info("creating new campsite");
        return campSiteRepository.save(campSite);
    }

    public Optional<CampSite> getSiteById(long campId){
        return campSiteRepository.findById(campId);
    }

    public CampSite save(CampSite campSite) {
        CampSite savedEntity = campSiteRepository.save(campSite);
        logger.info("created entity={}", savedEntity);
        return savedEntity;
    }

    public List<CampSite> getAllActiveSites(){
        return campSiteRepository.findByStatus(ACTIVE);
    }
}
