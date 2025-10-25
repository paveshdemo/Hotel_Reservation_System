package com.hotelreservationsystem.hotelreservationsystem.controller;

import com.hotelreservationsystem.hotelreservationsystem.model.User;
import com.hotelreservationsystem.hotelreservationsystem.model.UserRole;
import com.hotelreservationsystem.hotelreservationsystem.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/admin/staff")
public class AdminStaffController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public AdminStaffController(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping
    public String listStaff(Model model) {
        List<User> staffList = userRepository.findAll().stream()
                .filter(user -> user.getRole() != null)
                .filter(user -> user.getRole().isStaffRole() && user.getRole() != UserRole.ADMIN)
                .collect(Collectors.toList());
        model.addAttribute("staffList", staffList);
        return "admin/staff-list";
    }

    @GetMapping("/new")
    public String showAddStaffForm(Model model) {
        model.addAttribute("staff", new User());
        model.addAttribute("roles", new ArrayList<>(UserRole.getAssignableStaffRoles()));
        return "admin/staff-form";
    }

    @GetMapping("/edit/{id}")
    public String showEditStaffForm(@PathVariable Long id, Model model) {
        User staff = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid staff Id:" + id));
        model.addAttribute("staff", staff);
        model.addAttribute("roles", new ArrayList<>(UserRole.getAssignableStaffRoles()));
        return "admin/staff-form";
    }

    @PostMapping("/save")
    public String saveStaff(@ModelAttribute("staff") User staff, RedirectAttributes redirectAttributes) {
        if (staff.getUserId() != null) { // Updating an existing staff member
            User existingUser = userRepository.findById(staff.getUserId())
                    .orElseThrow(() -> new IllegalArgumentException("Invalid staff Id:" + staff.getUserId()));

            boolean emailTakenByAnotherUser = userRepository.findByEmail(staff.getEmail())
                    .filter(user -> !user.getUserId().equals(existingUser.getUserId()))
                    .isPresent();
            if (emailTakenByAnotherUser) {
                redirectAttributes.addFlashAttribute("errorMessage", "Email already exists!");
                return "redirect:/admin/staff/edit/" + staff.getUserId();
            }

            existingUser.setEmail(staff.getEmail());
            existingUser.setFirstName(staff.getFirstName());
            existingUser.setLastName(staff.getLastName());
            existingUser.setRole(staff.getRole());

            if (staff.getPasswordHash() != null && !staff.getPasswordHash().isBlank()) {
                existingUser.setPasswordHash(passwordEncoder.encode(staff.getPasswordHash()));
            }

            userRepository.save(existingUser);
        } else { // Creating a new staff member
            if (userRepository.existsByUsername(staff.getUsername())) {
                redirectAttributes.addFlashAttribute("errorMessage", "Username already exists!");
                return "redirect:/admin/staff/new";
            }

            if (userRepository.existsByEmail(staff.getEmail())) {
                redirectAttributes.addFlashAttribute("errorMessage", "Email already exists!");
                return "redirect:/admin/staff/new";
            }

            staff.setPasswordHash(passwordEncoder.encode(staff.getPasswordHash()));
            userRepository.save(staff);
        }

        redirectAttributes.addFlashAttribute("successMessage", "Staff member saved successfully!");
        return "redirect:/admin/staff";
    }

    @GetMapping("/delete/{id}")
    public String deleteStaff(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        userRepository.deleteById(id);
        redirectAttributes.addFlashAttribute("successMessage", "Staff member deleted successfully!");
        return "redirect:/admin/staff";
    }
}