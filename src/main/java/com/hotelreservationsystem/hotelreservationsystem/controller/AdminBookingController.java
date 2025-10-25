package com.hotelreservationsystem.hotelreservationsystem.controller;

import com.hotelreservationsystem.hotelreservationsystem.model.Booking;
import com.hotelreservationsystem.hotelreservationsystem.model.BookingStatus;
import com.hotelreservationsystem.hotelreservationsystem.model.Room;
import com.hotelreservationsystem.hotelreservationsystem.repository.BookingRepository;
import com.hotelreservationsystem.hotelreservationsystem.repository.RoomRepository;
import com.hotelreservationsystem.hotelreservationsystem.service.NotificationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDateTime;
import java.util.EnumSet;
import java.util.Optional;

@Controller
@RequestMapping("/admin/bookings")
public class AdminBookingController {

    private static final Logger logger = LoggerFactory.getLogger(AdminBookingController.class);

    private final BookingRepository bookingRepository;
    private final RoomRepository roomRepository;
    private final NotificationService notificationService;

    @Autowired
    public AdminBookingController(BookingRepository bookingRepository, RoomRepository roomRepository, NotificationService notificationService) {
        this.bookingRepository = bookingRepository;
        this.roomRepository = roomRepository;
        this.notificationService = notificationService;
    }

    @GetMapping
    public String listAllBookings(Model model) {
        model.addAttribute("bookings", bookingRepository.findAll());
        return "admin/bookings-list";
    }

    @PostMapping("/approve/{id}")
    public String approveBooking(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        Optional<Booking> optionalBooking = bookingRepository.findById(id);
        if (optionalBooking.isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Booking not found.");
            return "redirect:/admin/bookings";
        }

        Booking booking = optionalBooking.get();
        if (booking.getBookingStatus() != BookingStatus.PENDING && booking.getBookingStatus() != BookingStatus.PENDING_PAYMENT) {
            redirectAttributes.addFlashAttribute("errorMessage", "Only pending bookings can be approved.");
            return "redirect:/admin/bookings";
        }

        int updatedRows = bookingRepository.updateBookingStatus(id, BookingStatus.APPROVED);
        if (updatedRows == 0) {
            logger.warn("Failed to update booking status to APPROVED for booking {}", id);
            redirectAttributes.addFlashAttribute("errorMessage", "Failed to approve booking. Please try again.");
            return "redirect:/admin/bookings";
        }

        Room room = booking.getRoom();
        if (room != null) {
            room.setIsAvailable(false);
            roomRepository.save(room);
        }

        // CREATE NOTIFICATION FOR CUSTOMER
        String message = String.format(
                "Your booking for Room %s from %s to %s has been approved!",
                room != null ? room.getRoomNumber() : "N/A",
                booking.getCheckInDate(),
                booking.getCheckOutDate()
        );
        notificationService.createNotification(booking.getCustomer().getUser(), message);

        redirectAttributes.addFlashAttribute("successMessage", "Booking approved and customer notified!");
        return "redirect:/admin/bookings";
    }

    @PostMapping("/cancel/{id}")
    public String cancelBooking(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        Optional<Booking> optionalBooking = bookingRepository.findById(id);
        if (optionalBooking.isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Booking not found.");
            return "redirect:/admin/bookings";
        }

        Booking booking = optionalBooking.get();
        Room room = booking.getRoom();
        if (booking.getBookingStatus() == BookingStatus.CANCELLED) {
            redirectAttributes.addFlashAttribute("errorMessage", "This booking has already been cancelled.");
            return "redirect:/admin/bookings";
        }

        BookingStatus previousStatus = booking.getBookingStatus();

        int updatedRows = bookingRepository.updateBookingStatusAndCancelledAt(
                id,
                BookingStatus.CANCELLED,
                LocalDateTime.now()
        );

        if (updatedRows == 0) {
            logger.warn("Failed to cancel booking {}", id);
            redirectAttributes.addFlashAttribute("errorMessage", "Failed to cancel booking. Please try again.");
            return "redirect:/admin/bookings";
        }

        if (EnumSet.of(BookingStatus.APPROVED, BookingStatus.CONFIRMED, BookingStatus.CHECKED_IN).contains(previousStatus)) {
            if (room != null) {
                room.setIsAvailable(true);
                roomRepository.save(room);
            }
        }

        String message = String.format(
                "Your booking for Room %s from %s to %s has been cancelled.",
                room != null ? room.getRoomNumber() : "N/A",
                booking.getCheckInDate(),
                booking.getCheckOutDate()
        );
        notificationService.createNotification(booking.getCustomer().getUser(), message);

        redirectAttributes.addFlashAttribute("successMessage", "Booking cancelled successfully!");
        return "redirect:/admin/bookings";
    }
}