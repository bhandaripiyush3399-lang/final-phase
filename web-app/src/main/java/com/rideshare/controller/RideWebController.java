package com.rideshare.controller;

import com.rideshare.model.Ride.VehicleType;
import com.rideshare.model.User;
import com.rideshare.repository.UserRepository;
import com.rideshare.service.NotificationService;
import com.rideshare.service.RideService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDateTime;

@Controller
public class RideWebController extends BaseController {

    private final RideService rideService;

    public RideWebController(UserRepository userRepository, NotificationService notificationService,
                             RideService rideService) {
        super(userRepository, notificationService);
        this.rideService = rideService;
    }

    @GetMapping("/rides/available")
    public String availableRides(Authentication auth, Model model) {
        User user = getCurrentUser(auth);
        model.addAttribute("rides", rideService.getAvailableRides(user));
        model.addAttribute("user", user);
        return "book-ride";
    }

    @PostMapping("/rides/book/{rideId}")
    public String bookRide(@PathVariable Long rideId, Authentication auth,
                           RedirectAttributes redirectAttributes) {
        try {
            User user = getCurrentUser(auth);
            rideService.bookRide(rideId, user);
            redirectAttributes.addFlashAttribute("success", "Booking request sent! The provider will be notified.");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/rides/available";
    }

    @GetMapping("/rides/offer")
    public String offerRidePage(Model model) {
        model.addAttribute("vehicleTypes", VehicleType.values());
        return "offer-ride";
    }

    @PostMapping("/rides/offer")
    public String offerRide(@RequestParam String vehicleType,
                            @RequestParam String routeFrom,
                            @RequestParam String routeTo,
                            @RequestParam String departureTime,
                            @RequestParam int seats,
                            @RequestParam double fare,
                            Authentication auth,
                            RedirectAttributes redirectAttributes) {
        try {
            User user = getCurrentUser(auth);
            VehicleType vt = VehicleType.valueOf(vehicleType);
            LocalDateTime departure = LocalDateTime.parse(departureTime);
            rideService.createRide(user, vt, routeFrom, routeTo, departure, seats, fare);
            redirectAttributes.addFlashAttribute("success", "Ride offered successfully! Students can now book your ride.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/rides/offer";
    }

    @GetMapping("/rides/my")
    public String myRides(Authentication auth, Model model) {
        User user = getCurrentUser(auth);
        var rides = rideService.getMyOfferedRides(user);
        model.addAttribute("rides", rides);
        model.addAttribute("user", user);

        java.util.Map<Long, java.util.List<com.rideshare.model.Booking>> bookingsMap = new java.util.HashMap<>();
        for (var ride : rides) {
            bookingsMap.put(ride.getRideId(), rideService.getBookingsForRide(ride.getRideId()));
        }
        model.addAttribute("bookingsMap", bookingsMap);
        return "my-rides";
    }

    @PostMapping("/rides/cancel/{rideId}")
    public String cancelRide(@PathVariable Long rideId, Authentication auth,
                             RedirectAttributes redirectAttributes) {
        try {
            User user = getCurrentUser(auth);
            rideService.cancelRide(rideId, user);
            redirectAttributes.addFlashAttribute("success", "Ride cancelled. All passengers have been notified.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/rides/my";
    }

    @PostMapping("/bookings/accept/{bookingId}")
    public String acceptBooking(@PathVariable Long bookingId, Authentication auth,
                                RedirectAttributes redirectAttributes) {
        try {
            User user = getCurrentUser(auth);
            rideService.acceptBooking(bookingId, user);
            redirectAttributes.addFlashAttribute("success", "Booking accepted!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/rides/my";
    }

    @PostMapping("/bookings/reject/{bookingId}")
    public String rejectBooking(@PathVariable Long bookingId, Authentication auth,
                                RedirectAttributes redirectAttributes) {
        try {
            User user = getCurrentUser(auth);
            rideService.rejectBooking(bookingId, user);
            redirectAttributes.addFlashAttribute("success", "Booking rejected.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/rides/my";
    }

    @GetMapping("/bookings/my")
    public String myBookings(Authentication auth, Model model) {
        User user = getCurrentUser(auth);
        model.addAttribute("bookings", rideService.getMyBookings(user));
        model.addAttribute("user", user);
        return "my-bookings";
    }

    @PostMapping("/bookings/cancel/{bookingId}")
    public String cancelBooking(@PathVariable Long bookingId, Authentication auth,
                                RedirectAttributes redirectAttributes) {
        try {
            User user = getCurrentUser(auth);
            rideService.cancelBooking(bookingId, user);
            redirectAttributes.addFlashAttribute("success", "Booking cancelled.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/bookings/my";
    }
}
