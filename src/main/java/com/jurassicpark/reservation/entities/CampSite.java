package com.jurassicpark.reservation.entities;

import com.jurassicpark.reservation.util.BookingConstants.CampSiteStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "camp_site")
public class CampSite {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String siteName;

    @Enumerated(EnumType.STRING)
    private CampSiteStatus status;
}
