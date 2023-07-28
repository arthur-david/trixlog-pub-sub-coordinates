package com.trixlog.gatewayapi.services;

import com.trixlog.gatewayapi.records.CoordinateDataRecord;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;

@SpringBootTest
class CoordinateReceiverServiceTest {

    public static final Double SPEED = 50.0;
    public static final Double ACCELERATION = 0.22;
    public static final Long TRANSMISSION_REASON_ID = 101L;
    public static final Double BATTERY_VOLTAGE = 23.0;
    public static final Double TEMPERATURE = 90.0;

    @InjectMocks
    private CoordinateReceiverService coordinateReceiverService;

    @Mock
    private RabbitTemplate rabbitTemplate;

    private CoordinateDataRecord coordinateDataRecord;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        startCoordinateDataRecord();
    }

    @Test
    void shouldSendCoordinateInQueue() {
        doNothing().when(rabbitTemplate).convertAndSend(anyString(), anyString(), any(coordinateDataRecord.getClass()));

        assertDoesNotThrow(() -> coordinateReceiverService.send(coordinateDataRecord));
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