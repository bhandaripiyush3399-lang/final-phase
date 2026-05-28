package com.rideshare.repository;

import com.rideshare.model.Ride;
import com.rideshare.model.Ride.RideStatus;
import com.rideshare.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDateTime;
import java.util.List;

public interface RideRepository extends JpaRepository<Ride, Long> {

    List<Ride> findByStatusAndAvailableSeatsGreaterThanAndProviderNotAndDepartureTimeAfterOrderByDepartureTimeAsc(
        RideStatus status, int seats, User provider, LocalDateTime time);

    List<Ride> findByProviderOrderByCreatedAtDesc(User provider);
}
