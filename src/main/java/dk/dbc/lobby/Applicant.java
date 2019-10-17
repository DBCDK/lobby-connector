/*
 * Copyright Dansk Bibliotekscenter a/s. Licensed under GPLv3
 * See license text in LICENSE.txt or at https://opensource.dbc.dk/licenses/gpl-3.0/
 */

package dk.dbc.lobby;

import com.fasterxml.jackson.databind.JsonNode;

import java.util.Arrays;
import java.util.Date;

public class Applicant {

    private String id;
    private String category;
    private String mimetype;
    private ApplicantState state;
    private byte[] body;
    private Date timeOfCreation;
    private Date timeOfLastModification;
    private JsonNode additionalInfo;
    private String bodyLink;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getMimetype() {
        return mimetype;
    }

    public void setMimetype(String mimetype) {
        this.mimetype = mimetype;
    }

    public ApplicantState getState() {
        return state;
    }

    public void setState(ApplicantState state) {
        this.state = state;
    }

    public byte[] getBody() {
        return body;
    }

    public void setBody(byte[] body) {
        this.body = body;
    }

    public Date getTimeOfCreation() {
        return timeOfCreation;
    }

    public void setTimeOfCreation(Date timeOfCreation) {
        this.timeOfCreation = timeOfCreation;
    }

    public Date getTimeOfLastModification() {
        return timeOfLastModification;
    }

    public void setTimeOfLastModification(Date timeOfLastModification) {
        this.timeOfLastModification = timeOfLastModification;
    }

    public JsonNode getAdditionalInfo() {
        return additionalInfo;
    }

    public void setAdditionalInfo(JsonNode additionalInfo) {
        this.additionalInfo = additionalInfo;
    }

    public String getBodyLink() {
        return bodyLink;
    }

    public void setBodyLink(String bodyLink) {
        this.bodyLink = bodyLink;
    }

    @Override
    public String toString() {
        return "Applicant{" +
                "id='" + id + '\'' +
                ", category='" + category + '\'' +
                ", mimetype='" + mimetype + '\'' +
                ", state=" + state +
                ", body=" + Arrays.toString(body) +
                ", timeOfCreation='" + timeOfCreation + '\'' +
                ", timeOfLastModification='" + timeOfLastModification + '\'' +
                ", additionalInfo=" + additionalInfo +
                ", bodyLink='" + bodyLink + '\'' +
                '}';
    }
}
