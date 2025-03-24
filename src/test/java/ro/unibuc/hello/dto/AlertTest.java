package ro.unibuc.hello.dto;

import java.time.LocalDateTime;

import org.junit.Test;
import org.junit.jupiter.api.Assertions;

public class AlertTest {

    private static String headline = "Weather Warning";
    private static String msgType = "Alert";
    private static String severity = "Severe";
    private static String urgency = "Immediate";
    private static String areas = "All counties";
    private static String category = "Weather";
    private static String certainty = "Likely";
    private static String event = "Storm";
    private static String note = "Stay indoors";
    private static LocalDateTime effective = LocalDateTime.of(2025, 3, 24, 12, 0);
    private static LocalDateTime expires = LocalDateTime.of(2025, 3, 24, 18, 0);
    private static String desc = "Heavy storm expected";
    private static String instruction = "Remain inside and avoid travel";

    private static Alert alert = new Alert(headline, msgType, severity, urgency, areas,
            category, certainty, event, note, effective, expires, desc, instruction);

    @Test
    public void test_content() {
        Assertions.assertEquals(headline, alert.getHeadline());
        Assertions.assertEquals(msgType, alert.getMsgType());
        Assertions.assertEquals(severity, alert.getSeverity());
        Assertions.assertEquals(urgency, alert.getUrgency());
        Assertions.assertEquals(areas, alert.getAreas());
        Assertions.assertEquals(category, alert.getCategory());
        Assertions.assertEquals(certainty, alert.getCertainty());
        Assertions.assertEquals(event, alert.getEvent());
        Assertions.assertEquals(note, alert.getNote());
        Assertions.assertEquals(effective, alert.getEffective());
        Assertions.assertEquals(expires, alert.getExpires());
        Assertions.assertEquals(desc, alert.getDesc());
        Assertions.assertEquals(instruction, alert.getInstruction());
    }

    @Test
    public void test_setters() {
        Alert a = new Alert();

        a.setHeadline("Test Headline");
        a.setMsgType("Info");
        a.setSeverity("Moderate");
        a.setUrgency("Expected");
        a.setAreas("Dorohoi");
        a.setCategory("General");
        a.setCertainty("Possible");
        a.setEvent("Rain");
        a.setNote("Some note");
        a.setEffective(LocalDateTime.of(2025, 1, 1, 10, 0));
        a.setExpires(LocalDateTime.of(2025, 1, 1, 16, 0));
        a.setDesc("Some description");
        a.setInstruction("Do something");

        Assertions.assertEquals("Test Headline", a.getHeadline());
        Assertions.assertEquals("Info", a.getMsgType());
        Assertions.assertEquals("Moderate", a.getSeverity());
        Assertions.assertEquals("Expected", a.getUrgency());
        Assertions.assertEquals("Dorohoi", a.getAreas());
        Assertions.assertEquals("General", a.getCategory());
        Assertions.assertEquals("Possible", a.getCertainty());
        Assertions.assertEquals("Rain", a.getEvent());
        Assertions.assertEquals("Some note", a.getNote());
        Assertions.assertEquals(LocalDateTime.of(2025, 1, 1, 10, 0), a.getEffective());
        Assertions.assertEquals(LocalDateTime.of(2025, 1, 1, 16, 0), a.getExpires());
        Assertions.assertEquals("Some description", a.getDesc());
        Assertions.assertEquals("Do something", a.getInstruction());
    }
}
