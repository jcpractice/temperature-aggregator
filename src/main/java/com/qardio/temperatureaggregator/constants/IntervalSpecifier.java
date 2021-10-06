package com.qardio.temperatureaggregator.constants;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public enum IntervalSpecifier {
    byhour(Long.valueOf("1")),
    byday(Long.valueOf("24"));
    public  long duration;
    private IntervalSpecifier(long duration) {
        this.duration = duration;
    }
    public static IntervalSpecifier valueOfWithDefault(String duration){
        try{
            return IntervalSpecifier.valueOf(duration);
        }catch (Exception ex){
            log.error("Error while fetching duration! "+ ex.getMessage());
            return IntervalSpecifier.valueOf("byday");
        }

    }
}
