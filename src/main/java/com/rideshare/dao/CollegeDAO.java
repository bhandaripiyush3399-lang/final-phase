package com.rideshare.dao;

import com.rideshare.model.College;
import com.rideshare.model.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CollegeDAO {

    public List<College> getAllColleges() {
        List<College> colleges = new ArrayList<>();
        String sql = "SELECT * FROM colleges ORDER BY college_name";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                College college = new College();
                college.setCollegeId(rs.getInt("college_id"));
                college.setCollegeName(rs.getString("college_name"));
                college.setCollegeCode(rs.getString("college_code"));
                college.setAddress(rs.getString("address"));
                college.setEmergencyContact(rs.getString("emergency_contact"));
                college.setCreatedAt(rs.getTimestamp("created_at"));
                colleges.add(college);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return colleges;
    }

    public College getCollegeById(int collegeId) {
        String sql = "SELECT * FROM colleges WHERE college_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, collegeId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    College college = new College();
                    college.setCollegeId(rs.getInt("college_id"));
                    college.setCollegeName(rs.getString("college_name"));
                    college.setCollegeCode(rs.getString("college_code"));
                    college.setAddress(rs.getString("address"));
                    college.setEmergencyContact(rs.getString("emergency_contact"));
                    college.setCreatedAt(rs.getTimestamp("created_at"));
                    return college;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}
