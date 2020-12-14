package com.jurassicpark.reservation.models;

import lombok.*;

@Getter
@Setter
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReservationModel {

    private long campSiteId;

    private String startDate;

    private String endDate;

    private String email;

    private String name;
}
