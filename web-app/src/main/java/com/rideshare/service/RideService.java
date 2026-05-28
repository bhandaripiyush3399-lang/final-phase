package com.rideshare.service;

import com.rideshare.model.*;
import com.rideshare.model.Booking.BookingStatus;
import com.rideshare.model.Notification.NotificationType;
import com.rideshare.model.Ride.RideStatus;
import com.rideshare.model.Ride.VehicleType;
import com.rideshare.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

@Service
public class RideService {

    private final RideRepository rideRepository;
    private final BookingRepository bookingRepository;
    private final NotificationRepository notificationRepository;

    public RideService(RideRepository rideRepository, BookingRepository bookingRepository,
                       NotificationRepository notificationRepository) {
        this.rideRepository = rideRepository;
        this.bookingRepository = bookingRepository;
        this.notificationRepository = notificationRepository;
    }

    public Ride createRide(User provider, VehicleType vehicleType, String from, String to,
                           LocalDateTime departure, int seats, double fare) {
        if (vehicleType == VehicleType.BIKE && seats != 1) {
            throw new IllegalArgumentException("Bike can only carry 1 passenger.");
        }
        if (departure.isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("Departure time must be in the future.");
        }

        Ride ride = new Ride();
        ride.setProvider(provider);
        ride.setVehicleType(vehicleType);
        ride.setRouteFrom(from);
        ride.setRouteTo(to);
        ride.setDepartureTime(departure);
        ride.setTotalSeats(seats);
        ride.setAvailableSeats(seats);
        ride.setFare(fare);
        ride.setStatus(RideStatus.ACTIVE);
        return rideRepository.save(ride);
    }

    public List<Ride> getAvailableRides(User excludeUser) {
        return rideRepository.findByStatusAndAvailableSeatsGreaterThanAndProviderNotAndDepartureTimeAfterOrderByDepartureTimeAsc(
            RideStatus.ACTIVE, 0, excludeUser, LocalDateTime.now());
    }

    public List<Ride> getMyOfferedRides(User provider) {
        return rideRepository.findByProviderOrderByCreatedAtDesc(provider);
    }

    @Transactional
    public Booking bookRide(Long rideId, User seeker) {
        Ride ride = rideRepository.findById(rideId)
            .orElseThrow(() -> new IllegalArgumentException("Ride not found."));

        if (ride.getStatus() != RideStatus.ACTIVE) {
            throw new IllegalArgumentException("This ride is no longer available.");
        }
        if (ride.getAvailableSeats() <= 0) {
            throw new IllegalArgumentException("No seats available.");
        }
        if (ride.getProvider().getUserId().equals(seeker.getUserId())) {
            throw new IllegalArgumentException("You cannot book your own ride.");
        }

        List<BookingStatus> activeStatuses = Arrays.asList(BookingStatus.PENDING, BookingStatus.ACCEPTED);
        if (bookingRepository.existsByRideAndSeekerAndStatusIn(ride, seeker, activeStatuses)) {
            throw new IllegalArgumentException("You already have a booking for this ride.");
        }

        Booking booking = new Booking();
        booking.setRide(ride);
        booking.setSeeker(seeker);
        bookingRepository.save(booking);

        notificationRepository.save(new Notification(
            ride.getProvider(),
            seeker.getFullName() + " has requested a seat on your ride from "
                + ride.getRouteFrom() + " to " + ride.getRouteTo() + ".",
            NotificationType.BOOKING_REQUEST));

        return booking;
    }

    @Transactional
    public void acceptBooking(Long bookingId, User provider) {
        Booking booking = bookingRepository.findById(bookingId)
            .orElseThrow(() -> new IllegalArgumentException("Booking not found."));

        Ride ride = booking.getRide();
        if (!ride.getProvider().getUserId().equals(provider.getUserId())) {
            throw new IllegalArgumentException("Not authorized.");
        }
        if (ride.getAvailableSeats() <= 0) {
            throw new IllegalArgumentException("No seats available to accept this booking.");
        }

        ride.setAvailableSeats(ride.getAvailableSeats() - 1);
        if (ride.getAvailableSeats() == 0) {
            ride.setStatus(RideStatus.FULL);
        }
        rideRepository.save(ride);

        booking.setStatus(BookingStatus.ACCEPTED);
        bookingRepository.save(booking);

        notificationRepository.save(new Notification(
            booking.getSeeker(),
            "Your booking for the ride from " + ride.getRouteFrom()
                + " to " + ride.getRouteTo() + " has been ACCEPTED!",
            NotificationType.RIDE_ACCEPTED));
    }

    @Transactional
    public void rejectBooking(Long bookingId, User provider) {
        Booking booking = bookingRepository.findById(bookingId)
            .orElseThrow(() -> new IllegalArgumentException("Booking not found."));

        Ride ride = booking.getRide();
        if (!ride.getProvider().getUserId().equals(provider.getUserId())) {
            throw new IllegalArgumentException("Not authorized.");
        }

        booking.setStatus(BookingStatus.REJECTED);
        bookingRepository.save(booking);

        notificationRepository.save(new Notification(
            booking.getSeeker(),
            "Your booking for the ride from " + ride.getRouteFrom()
                + " to " + ride.getRouteTo() + " has been REJECTED.",
            NotificationType.RIDE_REJECTED));
    }

    @Transactional
    public void cancelBooking(Long bookingId, User seeker) {
        Booking booking = bookingRepository.findById(bookingId)
            .orElseThrow(() -> new IllegalArgumentException("Booking not found."));

        if (!booking.getSeeker().getUserId().equals(seeker.getUserId())) {
            throw new IllegalArgumentException("Not authorized.");
        }

        BookingStatus previousStatus = booking.getStatus();
        booking.setStatus(BookingStatus.CANCELLED);
        bookingRepository.save(booking);

        Ride ride = booking.getRide();
        if (previousStatus == BookingStatus.ACCEPTED && ride.getAvailableSeats() < ride.getTotalSeats()) {
            ride.setAvailableSeats(ride.getAvailableSeats() + 1);
            if (ride.getStatus() == RideStatus.FULL) {
                ride.setStatus(RideStatus.ACTIVE);
            }
            rideRepository.save(ride);
        }

        notificationRepository.save(new Notification(
            ride.getProvider(),
            "A passenger has cancelled their booking for your ride from "
                + ride.getRouteFrom() + " to " + ride.getRouteTo() + ".",
            NotificationType.BOOKING_CANCELLED));
    }

    @Transactional
    public void cancelRide(Long rideId, User provider) {
        Ride ride = rideRepository.findById(rideId)
            .orElseThrow(() -> new IllegalArgumentException("Ride not found."));

        if (!ride.getProvider().getUserId().equals(provider.getUserId())) {
            throw new IllegalArgumentException("You can only cancel your own rides.");
        }

        ride.setStatus(RideStatus.CANCELLED);
        rideRepository.save(ride);

        List<BookingStatus> activeStatuses = Arrays.asList(BookingStatus.PENDING, BookingStatus.ACCEPTED);
        List<Booking> activeBookings = bookingRepository.findByRideAndStatusIn(ride, activeStatuses);
        for (Booking booking : activeBookings) {
            booking.setStatus(BookingStatus.CANCELLED);
            bookingRepository.save(booking);

            notificationRepository.save(new Notification(
                booking.getSeeker(),
                "The ride from " + ride.getRouteFrom() + " to " + ride.getRouteTo()
                    + " has been CANCELLED by the provider.",
                NotificationType.RIDE_CANCELLED));
        }
    }

    public List<Booking> getBookingsForRide(Long rideId) {
        Ride ride = rideRepository.findById(rideId).orElse(null);
        if (ride == null) return List.of();
        return bookingRepository.findByRideOrderByBookingTimeDesc(ride);
    }

    public List<Booking> getMyBookings(User seeker) {
        return bookingRepository.findBySeekerOrderByBookingTimeDesc(seeker);
    }
}
