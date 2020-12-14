package com.jurassicpark.reservation.entities;

import com.jurassicpark.reservation.util.BookingConstants.CampSiteStatus;
import lombok.*;

import javax.persistence.*;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity(name = "camp_site")
public class CampSite {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String siteName;

    @Enumerated(EnumType.STRING)
    private CampSiteStatus status;
}
