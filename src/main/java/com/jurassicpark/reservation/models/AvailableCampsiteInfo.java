package com.jurassicpark.reservation.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AvailableCampsiteInfo {

    private Long campSiteId;

    @JsonIgnore
    private List<String> availableDates;

    private String availabilities;

}
