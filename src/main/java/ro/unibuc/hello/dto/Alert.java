package ro.unibuc.hello.dto;

import java.time.LocalDateTime;

public class Alert {

    private String headline;
    private String msgType;
    private String severity;
    private String urgency;
    private String areas;
    private String category;
    private String certainty;
    private String event;
    private String note;
    private LocalDateTime effective;
    private LocalDateTime expires;
    private String desc;
    private String instruction;

    public Alert() {
    }

    public Alert(String headline, String msgType, String severity, String urgency, String areas,
                 String category, String certainty, String event, String note,
                 LocalDateTime effective, LocalDateTime expires, String desc, String instruction) {
        this.headline = headline;
        this.msgType = msgType;
        this.severity = severity;
        this.urgency = urgency;
        this.areas = areas;
        this.category = category;
        this.certainty = certainty;
        this.event = event;
        this.note = note;
        this.effective = effective;
        this.expires = expires;
        this.desc = desc;
        this.instruction = instruction;
    }

    // Getters and Setters
    public String getHeadline() {
        return headline;
    }

    public void setHeadline(String headline) {
        this.headline = headline;
    }

    public String getMsgType() {
        return msgType;
    }

    public void setMsgType(String msgType) {
        this.msgType = msgType;
    }

    public String getSeverity() {
        return severity;
    }

    public void setSeverity(String severity) {
        this.severity = severity;
    }

    public String getUrgency() {
        return urgency;
    }

    public void setUrgency(String urgency) {
        this.urgency = urgency;
    }

    public String getAreas() {
        return areas;
    }

    public void setAreas(String areas) {
        this.areas = areas;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getCertainty() {
        return certainty;
    }

    public void setCertainty(String certainty) {
        this.certainty = certainty;
    }

    public String getEvent() {
        return event;
    }

    public void setEvent(String event) {
        this.event = event;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public LocalDateTime getEffective() {
        return effective;
    }

    public void setEffective(LocalDateTime effective) {
        this.effective = effective;
    }

    public LocalDateTime getExpires() {
        return expires;
    }

    public void setExpires(LocalDateTime expires) {
        this.expires = expires;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getInstruction() {
        return instruction;
    }

    public void setInstruction(String instruction) {
        this.instruction = instruction;
    }

    @Override
    public String toString() {
        return "Alert{" +
                "headline='" + headline + '\'' +
                ", msgType='" + msgType + '\'' +
                ", severity='" + severity + '\'' +
                ", urgency='" + urgency + '\'' +
                ", areas='" + areas + '\'' +
                ", category='" + category + '\'' +
                ", certainty='" + certainty + '\'' +
                ", event='" + event + '\'' +
                ", note='" + note + '\'' +
                ", effective=" + effective +
                ", expires=" + expires +
                ", desc='" + desc + '\'' +
                ", instruction='" + instruction + '\'' +
                '}';
    }
}
