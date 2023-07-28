package com.trixlog.gatewayapi.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.trixlog.gatewayapi.records.CoordinateDataRecord;
import com.trixlog.gatewayapi.services.CoordinateReceiverService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

@ExtendWith(SpringExtension.class)
@WebMvcTest(controllers = CoordinateReceiverController.class)
class CoordinateReceiverControllerTest {

    public static final Double SPEED = 50.0;
    public static final Double ACCELERATION = 0.22;
    public static final Long TRANSMISSION_REASON_ID = 101L;
    public static final Double BATTERY_VOLTAGE = 23.0;
    public static final Double TEMPERATURE = 90.0;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private CoordinateReceiverService coordinateReceiverService;

    private CoordinateDataRecord coordinateDataRecord;

    private ObjectWriter objectWriter;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        objectWriter = objectMapper.writer().withDefaultPrettyPrinter();
        startCoordinateDataRecord();
    }

    @Test
    void shouldReturnOkWhenSendCoordinate() throws Exception {
        String content = objectWriter.writeValueAsString(coordinateDataRecord);

        MockHttpServletRequestBuilder request = post("/coordinates")
                .contentType(MediaType.APPLICATION_JSON)
                .content(content);

        MvcResult result = mockMvc.perform(request).andReturn();

        assertNull(result.getResolvedException());
        assertEquals(HttpStatus.OK.value(), result.getResponse().getStatus());
    }

    @Test
    void shouldReturnBadRequestWhenDontSendCoordinateOnBody() throws Exception {
        MockHttpServletRequestBuilder request = post("/coordinates")
                .contentType(MediaType.APPLICATION_JSON);

        MvcResult result = mockMvc.perform(request).andReturn();

        assertNotNull(result.getResolvedException());
        assertEquals(HttpStatus.BAD_REQUEST.value(), result.getResponse().getStatus());
    }

    private void startCoordinateDataRecord() {
        coordinateDataRecord = new CoordinateDataRecord(
                LocalDateTime.now(),
                "HVL2794",
                -26.991158,
                -48.6459632,
                SPEED,
                ACCELERATION,
                true,
                41528921L,
                807L,
                TRANSMISSION_REASON_ID,
                191L,
                BATTERY_VOLTAGE,
                TEMPERATURE
        );
    }
}