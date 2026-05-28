package com.rideshare.dao;

import com.rideshare.model.DatabaseConnection;
import com.rideshare.model.Ride;
import com.rideshare.model.Ride.RideStatus;
import com.rideshare.model.Ride.VehicleType;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class RideDAO {

    public boolean createRide(Ride ride) {
        String sql = "INSERT INTO rides (provider_id, vehicle_type, route_from, route_to, " +
                     "departure_time, total_seats, available_seats, fare, status) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, 'ACTIVE')";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setInt(1, ride.getProviderId());
            pstmt.setString(2, ride.getVehicleType().name());
            pstmt.setString(3, ride.getRouteFrom());
            pstmt.setString(4, ride.getRouteTo());
            pstmt.setTimestamp(5, Timestamp.valueOf(ride.getDepartureTime()));
            pstmt.setInt(6, ride.getTotalSeats());
            pstmt.setInt(7, ride.getAvailableSeats());
            pstmt.setDouble(8, ride.getFare());

            int rows = pstmt.executeUpdate();
            if (rows > 0) {
                try (ResultSet keys = pstmt.getGeneratedKeys()) {
                    if (keys.next()) {
                        ride.setRideId(keys.getInt(1));
                    }
                }
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public List<Ride> getAvailableRides(int excludeUserId) {
        List<Ride> rides = new ArrayList<>();
        String sql = "SELECT r.*, u.full_name AS provider_name, c.college_name AS provider_college " +
                     "FROM rides r " +
                     "JOIN users u ON r.provider_id = u.user_id " +
                     "JOIN colleges c ON u.college_id = c.college_id " +
                     "WHERE r.status = 'ACTIVE' AND r.available_seats > 0 " +
                     "AND r.provider_id != ? AND r.departure_time > NOW() " +
                     "ORDER BY r.departure_time ASC";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, excludeUserId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    rides.add(extractRide(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return rides;
    }

    public List<Ride> getRidesByProvider(int providerId) {
        List<Ride> rides = new ArrayList<>();
        String sql = "SELECT r.*, u.full_name AS provider_name, c.college_name AS provider_college " +
                     "FROM rides r " +
                     "JOIN users u ON r.provider_id = u.user_id " +
                     "JOIN colleges c ON u.college_id = c.college_id " +
                     "WHERE r.provider_id = ? ORDER BY r.created_at DESC";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, providerId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    rides.add(extractRide(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return rides;
    }

    public Ride getRideById(int rideId) {
        String sql = "SELECT r.*, u.full_name AS provider_name, c.college_name AS provider_college " +
                     "FROM rides r " +
                     "JOIN users u ON r.provider_id = u.user_id " +
                     "JOIN colleges c ON u.college_id = c.college_id " +
                     "WHERE r.ride_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, rideId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return extractRide(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean updateRideStatus(int rideId, RideStatus status) {
        String sql = "UPDATE rides SET status = ? WHERE ride_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, status.name());
            pstmt.setInt(2, rideId);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean decrementAvailableSeats(int rideId) {
        String sql = "UPDATE rides SET available_seats = available_seats - 1, " +
                     "status = CASE WHEN available_seats - 1 = 0 THEN 'FULL' ELSE status END " +
                     "WHERE ride_id = ? AND available_seats > 0";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, rideId);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean incrementAvailableSeats(int rideId) {
        String sql = "UPDATE rides SET available_seats = available_seats + 1, " +
                     "status = CASE WHEN status = 'FULL' THEN 'ACTIVE' ELSE status END " +
                     "WHERE ride_id = ? AND available_seats < total_seats";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, rideId);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    private Ride extractRide(ResultSet rs) throws SQLException {
        Ride ride = new Ride();
        ride.setRideId(rs.getInt("ride_id"));
        ride.setProviderId(rs.getInt("provider_id"));
        ride.setProviderName(rs.getString("provider_name"));
        ride.setProviderCollege(rs.getString("provider_college"));
        ride.setVehicleType(VehicleType.valueOf(rs.getString("vehicle_type")));
        ride.setRouteFrom(rs.getString("route_from"));
        ride.setRouteTo(rs.getString("route_to"));
        ride.setDepartureTime(rs.getTimestamp("departure_time").toLocalDateTime());
        ride.setTotalSeats(rs.getInt("total_seats"));
        ride.setAvailableSeats(rs.getInt("available_seats"));
        ride.setFare(rs.getDouble("fare"));
        ride.setStatus(RideStatus.valueOf(rs.getString("status")));
        ride.setCreatedAt(rs.getTimestamp("created_at"));
        return ride;
    }
}
