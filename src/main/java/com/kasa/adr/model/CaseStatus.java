package com.kasa.adr.model;

public enum CaseStatus{
    OPEN("Open"),
    IN_PROGRESS("In Progress"),
    CLOSED("Closed"),
    REJECTED("Rejected"),
    ESCALATED("Escalated"),
    RESOLVED("Resolved");

    private final String status;

    CaseStatus(String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }

    @Override
    public String toString() {
        return status;
    }
}
