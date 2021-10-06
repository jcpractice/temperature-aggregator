package com.qardio.temperatureaggregator.test;

import com.qardio.temperatureaggregator.model.Temperature;
import com.qardio.temperatureaggregator.request.TemperatureRequest;
import com.qardio.temperatureaggregator.service.TemperatureService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@AutoConfigureMockMvc
public class TemperatureAggregatorTest {

    @Autowired
    private MockMvc mvc;
    @MockBean
    TemperatureService temperatureService;

    @Test
    public void pushTemperatureData_whenCorrect() throws Exception {

        String requestStr = "{\n" +
                "    \"clientName\": \"test\",\n" +
                "    \"temperatures\":[\"2\",\"5\"]\n" +
                "}";
        String expectedResponse = "Data pushed";

        Mockito.when(temperatureService.saveData(Mockito.any(TemperatureRequest.class))).thenReturn(true);

        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .post("/temperature-data/save")
                .accept(MediaType.APPLICATION_JSON).content(requestStr)
                .contentType(MediaType.APPLICATION_JSON);

        MvcResult result = mvc.perform(requestBuilder).andReturn();

        MockHttpServletResponse response = result.getResponse();

        assertEquals(HttpStatus.CREATED.value(), response.getStatus());

        assertEquals(expectedResponse, response.getContentAsString());

    }

    @Test
    public void getAggregatedTemperatureData_byHour() throws Exception {
        List<Temperature> tempTestResponse = new ArrayList<>();
        Temperature temp1 = new Temperature("10", Instant.now());
        Temperature temp2 = new Temperature("20", Instant.now());
        tempTestResponse.add(temp1);
        tempTestResponse.add(temp2);
        Mockito.when(temperatureService.getAggregatedData(Mockito.anyString())).thenReturn(tempTestResponse);

        RequestBuilder requestBuilder = MockMvcRequestBuilders.get(
                "/temperature-data/aggregator/byhour").accept(
                MediaType.APPLICATION_JSON);

        mvc.perform(requestBuilder).andExpect(MockMvcResultMatchers.status().isOk()).andExpect(MockMvcResultMatchers.jsonPath("$.[0].temperature").value("10"));
    }

    @Test
    public void getAggregatedTemperatureData_byDay() throws Exception {
        List<Temperature> tempTestResponse = new ArrayList<>();
        Temperature temp1 = new Temperature("100", Instant.now());
        Temperature temp2 = new Temperature("200", Instant.now());
        tempTestResponse.add(temp1);
        tempTestResponse.add(temp2);
        Mockito.when(temperatureService.getAggregatedData(Mockito.anyString())).thenReturn(tempTestResponse);

        RequestBuilder requestBuilder = MockMvcRequestBuilders.get(
                "/temperature-data/aggregator/byhour").accept(
                MediaType.APPLICATION_JSON);

        mvc.perform(requestBuilder).andExpect(MockMvcResultMatchers.status().isOk()).andExpect(MockMvcResultMatchers.jsonPath("$.[1].temperature").value("200"));
    }

}
