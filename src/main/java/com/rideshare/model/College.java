package com.rideshare.model;

import java.sql.Timestamp;

public class College {

    private int collegeId;
    private String collegeName;
    private String collegeCode;
    private String address;
    private String emergencyContact;
    private Timestamp createdAt;

    public College() {}

    public College(int collegeId, String collegeName, String collegeCode,
                   String address, String emergencyContact) {
        this.collegeId = collegeId;
        this.collegeName = collegeName;
        this.collegeCode = collegeCode;
        this.address = address;
        this.emergencyContact = emergencyContact;
    }

    public int getCollegeId() { return collegeId; }
    public void setCollegeId(int collegeId) { this.collegeId = collegeId; }

    public String getCollegeName() { return collegeName; }
    public void setCollegeName(String collegeName) { this.collegeName = collegeName; }

    public String getCollegeCode() { return collegeCode; }
    public void setCollegeCode(String collegeCode) { this.collegeCode = collegeCode; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public String getEmergencyContact() { return emergencyContact; }
    public void setEmergencyContact(String emergencyContact) { this.emergencyContact = emergencyContact; }

    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }

    @Override
    public String toString() {
        return collegeName + " (" + collegeCode + ")";
    }
}
