package com.qardio.temperatureaggregator.model;

import lombok.*;

import java.time.Instant;

@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Temperature {

    private String temperature;
    private Instant resultTime;
}
