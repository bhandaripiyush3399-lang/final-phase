package com.rideshare.dao;

import com.rideshare.model.Booking;
import com.rideshare.model.Booking.BookingStatus;
import com.rideshare.model.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class BookingDAO {

    public boolean createBooking(Booking booking) {
        String sql = "INSERT INTO bookings (ride_id, seeker_id, status) VALUES (?, ?, 'PENDING')";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setInt(1, booking.getRideId());
            pstmt.setInt(2, booking.getSeekerId());

            int rows = pstmt.executeUpdate();
            if (rows > 0) {
                try (ResultSet keys = pstmt.getGeneratedKeys()) {
                    if (keys.next()) {
                        booking.setBookingId(keys.getInt(1));
                    }
                }
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean updateBookingStatus(int bookingId, BookingStatus status) {
        String sql = "UPDATE bookings SET status = ? WHERE booking_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, status.name());
            pstmt.setInt(2, bookingId);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public List<Booking> getBookingsForRide(int rideId) {
        List<Booking> bookings = new ArrayList<>();
        String sql = "SELECT b.*, u.full_name AS seeker_name, u.phone AS seeker_phone, " +
                     "c.college_name AS seeker_college " +
                     "FROM bookings b " +
                     "JOIN users u ON b.seeker_id = u.user_id " +
                     "JOIN colleges c ON u.college_id = c.college_id " +
                     "WHERE b.ride_id = ? ORDER BY b.booking_time DESC";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, rideId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    bookings.add(extractBooking(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return bookings;
    }

    public List<Booking> getBookingsBySeeker(int seekerId) {
        List<Booking> bookings = new ArrayList<>();
        String sql = "SELECT b.*, u.full_name AS seeker_name, u.phone AS seeker_phone, " +
                     "c.college_name AS seeker_college, " +
                     "r.route_from, r.route_to, r.vehicle_type, r.fare, " +
                     "p.full_name AS provider_name " +
                     "FROM bookings b " +
                     "JOIN users u ON b.seeker_id = u.user_id " +
                     "JOIN colleges c ON u.college_id = c.college_id " +
                     "JOIN rides r ON b.ride_id = r.ride_id " +
                     "JOIN users p ON r.provider_id = p.user_id " +
                     "WHERE b.seeker_id = ? ORDER BY b.booking_time DESC";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, seekerId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Booking booking = extractBooking(rs);
                    booking.setRouteFrom(rs.getString("route_from"));
                    booking.setRouteTo(rs.getString("route_to"));
                    booking.setProviderName(rs.getString("provider_name"));
                    booking.setVehicleType(rs.getString("vehicle_type"));
                    booking.setFare(rs.getDouble("fare"));
                    bookings.add(booking);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return bookings;
    }

    public boolean hasExistingBooking(int rideId, int seekerId) {
        String sql = "SELECT COUNT(*) FROM bookings WHERE ride_id = ? AND seeker_id = ? " +
                     "AND status IN ('PENDING', 'ACCEPTED')";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, rideId);
            pstmt.setInt(2, seekerId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public List<Booking> getActiveBookingsForRide(int rideId) {
        List<Booking> bookings = new ArrayList<>();
        String sql = "SELECT b.*, u.full_name AS seeker_name, u.phone AS seeker_phone, " +
                     "c.college_name AS seeker_college " +
                     "FROM bookings b " +
                     "JOIN users u ON b.seeker_id = u.user_id " +
                     "JOIN colleges c ON u.college_id = c.college_id " +
                     "WHERE b.ride_id = ? AND b.status IN ('PENDING', 'ACCEPTED') " +
                     "ORDER BY b.booking_time DESC";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, rideId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    bookings.add(extractBooking(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return bookings;
    }

    private Booking extractBooking(ResultSet rs) throws SQLException {
        Booking booking = new Booking();
        booking.setBookingId(rs.getInt("booking_id"));
        booking.setRideId(rs.getInt("ride_id"));
        booking.setSeekerId(rs.getInt("seeker_id"));
        booking.setSeekerName(rs.getString("seeker_name"));
        booking.setSeekerPhone(rs.getString("seeker_phone"));
        booking.setSeekerCollege(rs.getString("seeker_college"));
        booking.setStatus(BookingStatus.valueOf(rs.getString("status")));
        booking.setBookingTime(rs.getTimestamp("booking_time"));
        booking.setUpdatedAt(rs.getTimestamp("updated_at"));
        return booking;
    }
}
