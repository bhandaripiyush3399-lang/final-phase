package com.rideshare.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "colleges")
public class College {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long collegeId;

    @Column(nullable = false)
    private String collegeName;

    @Column(nullable = false, unique = true)
    private String collegeCode;

    private String address;
    private String emergencyContact;

    @Column(updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    public College() {}

    public College(String collegeName, String collegeCode, String address, String emergencyContact) {
        this.collegeName = collegeName;
        this.collegeCode = collegeCode;
        this.address = address;
        this.emergencyContact = emergencyContact;
    }

    public Long getCollegeId() { return collegeId; }
    public void setCollegeId(Long collegeId) { this.collegeId = collegeId; }
    public String getCollegeName() { return collegeName; }
    public void setCollegeName(String collegeName) { this.collegeName = collegeName; }
    public String getCollegeCode() { return collegeCode; }
    public void setCollegeCode(String collegeCode) { this.collegeCode = collegeCode; }
    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }
    public String getEmergencyContact() { return emergencyContact; }
    public void setEmergencyContact(String emergencyContact) { this.emergencyContact = emergencyContact; }
    public LocalDateTime getCreatedAt() { return createdAt; }

    @Override
    public String toString() { return collegeName + " (" + collegeCode + ")"; }
}
