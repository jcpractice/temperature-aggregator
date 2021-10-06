package com.qardio.temperatureaggregator.service;

import com.influxdb.client.*;
import com.influxdb.client.domain.WritePrecision;
import com.influxdb.client.write.Point;
import com.influxdb.client.write.events.WriteErrorEvent;
import com.influxdb.query.FluxRecord;
import com.influxdb.query.FluxTable;
import com.influxdb.query.dsl.Flux;
import com.influxdb.query.dsl.functions.restriction.Restrictions;
import com.qardio.temperatureaggregator.config.InfluxDBConfiguration;
import com.qardio.temperatureaggregator.constants.IntervalSpecifier;
import com.qardio.temperatureaggregator.model.Temperature;
import com.qardio.temperatureaggregator.request.TemperatureRequest;
import io.reactivex.BackpressureOverflowStrategy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * This the service class to handle the CRUD operations.
 */

@Service
@Slf4j
public class TemperatureService {

    @Autowired
    InfluxDBConfiguration influxDBConfiguration;
    InfluxDBClient client;

    /**
     * Method to save readings.By default Batch is enabled and set to 10 for sampling.
     * @param temperatures
     */
    public boolean saveData(TemperatureRequest temperatures){
        final String METHODNAME="saveData";
        log.trace("Entry : "+METHODNAME);

        AtomicBoolean statusFlag = new AtomicBoolean(false);
        if(null!=temperatures && !temperatures.getTemperatures().isEmpty()){
            try{
                this.client = getInfluxDBConnection();
                List<Point> points = new ArrayList<>();
                temperatures.getTemperatures().forEach(temperature ->{
                    Point point = Point
                            .measurement("temperature")
                            .addField("temperature", Double.valueOf(temperature))
                            .addField("client", temperatures.getClientName())
                            .time(Instant.now(), WritePrecision.NS);
                    points.add(point);
                });

                WriteApi writeApi = client.getWriteApi(WriteOptions.builder().
                        batchSize(10).backpressureStrategy(BackpressureOverflowStrategy.DROP_OLDEST).build());
                writeApi.writePoints(influxDBConfiguration.getBucketName(), influxDBConfiguration.getOrgName(), points);

                writeApi.listenEvents(WriteErrorEvent.class,writeErrorEvent -> {
                    statusFlag.set(false);
                });
                statusFlag.set(true);

            }catch(Exception ex){
                statusFlag.set(false);
                client.close();
                log.error("Exception occurred : "+ex.getMessage());
                throw ex;
            }
            client.close();
        }

        log.trace("Exit : "+METHODNAME);
        return statusFlag.get();
    }

    /**
     * Method to retrieve the aggregated result.
     * @param duration
     * @return
     */
    public List<Temperature> getAggregatedData(String duration) {

        final String METHODNAME="getAggregatedData";
        log.trace("Entry : "+METHODNAME);

        List<Temperature> temperatureResults = new ArrayList<>();
        try{
            this.client = getInfluxDBConnection();

            /**
             * For experimental purpose the range is set to weekly.
             */
            Instant startDate = LocalDate.now().minusDays(7).atStartOfDay(ZoneId.systemDefault()).toInstant();
            Flux fluxObj = Flux.from(influxDBConfiguration.getBucketName()).range(startDate, Instant.now()).
                    filter(Restrictions.and(Restrictions.measurement().equal("temperature"))).
                    filter(Restrictions.and(Restrictions.field().equal("temperature"))).
                    aggregateWindow(IntervalSpecifier.valueOfWithDefault(duration).duration, ChronoUnit.HOURS, "mean").
                    withCreateEmpty(false).yield("mean");

            log.trace("fluxObj-->"+fluxObj.toString());

            List<FluxTable> tables = this.client.getQueryApi().query(fluxObj.toString(), this.influxDBConfiguration.getOrgName());
            for (FluxTable fluxTable : tables) {
                List<FluxRecord> records = fluxTable.getRecords();
                for (FluxRecord fluxRecord : records) {
                    temperatureResults.add(new Temperature(String.valueOf(fluxRecord.getValueByKey("_value")), fluxRecord.getTime()));
                }
            }
            //TODO will check later
        /*client.getQueryApi().query(fluxObj.toString(), (cancellable, record) -> {
            temperatureResults.add(new Temperature(String.valueOf(record.getValueByKey("_value")), record.getTime()));
        }, error -> {
                   log.error("Exception Occurred : "+ error.getMessage());
                }, () -> {
                   log.trace("Execution completed...");
                });*/

            client.close();
        }catch(Exception ex){
            client.close();
            log.error("Exception occurred : "+ex.getMessage());
            throw ex;
        }

        log.trace("Exit : "+METHODNAME);
        return temperatureResults;
    }

    InfluxDBClient  getInfluxDBConnection(){

        InfluxDBClient client = InfluxDBClientFactory.create(influxDBConfiguration.getDatabaseUrl(), influxDBConfiguration.getUserName(),influxDBConfiguration.getPassword().toCharArray());

        return client;
    }
}
