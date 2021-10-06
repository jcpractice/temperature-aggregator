package com.qardio.temperatureaggregator.controller;

import com.qardio.temperatureaggregator.model.Temperature;
import com.qardio.temperatureaggregator.request.TemperatureRequest;
import com.qardio.temperatureaggregator.service.TemperatureService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@Slf4j
@RequestMapping("/temperature-data/")
public class TemperatureController {

    //WebClient webclient = WebClient.
    @Autowired
    TemperatureService temperatureService;

    @PostMapping("/save")
    public ResponseEntity saveTemperatureDate(@RequestBody TemperatureRequest temperatures){
        boolean isSuccess = temperatureService.saveData(temperatures);
        if(isSuccess){
            return ResponseEntity.status(HttpStatus.CREATED).body("Data pushed");
        }else{
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Something went wrong.Please contact Support");
        }
    }

    @GetMapping("/aggregator/{byDuration}")
    public ResponseEntity<List<Temperature>> getAggregatedData(@PathVariable String byDuration){
        List<Temperature> result = temperatureService.getAggregatedData(byDuration);

        return ResponseEntity.ok(result);
    }
}
