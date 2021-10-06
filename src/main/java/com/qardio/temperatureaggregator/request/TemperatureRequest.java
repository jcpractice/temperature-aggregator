package com.qardio.temperatureaggregator.request;

import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TemperatureRequest {
    String clientName;
    ArrayList<String> temperatures;
}
