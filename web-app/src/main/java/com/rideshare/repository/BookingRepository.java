package com.rideshare.repository;

import com.rideshare.model.Booking;
import com.rideshare.model.Booking.BookingStatus;
import com.rideshare.model.Ride;
import com.rideshare.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    List<Booking> findByRideOrderByBookingTimeDesc(Ride ride);

    List<Booking> findBySeekerOrderByBookingTimeDesc(User seeker);

    boolean existsByRideAndSeekerAndStatusIn(Ride ride, User seeker, List<BookingStatus> statuses);

    List<Booking> findByRideAndStatusIn(Ride ride, List<BookingStatus> statuses);
}
