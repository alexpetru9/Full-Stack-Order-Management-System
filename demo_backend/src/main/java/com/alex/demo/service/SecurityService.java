package com.alex.demo.service;

import com.alex.demo.model.LoginResponse;
import com.alex.demo.model.PasswordResetToken;
import com.alex.demo.model.Person;
import com.alex.demo.model.RegisterRequest;
import com.alex.demo.repository.PasswordResetTokenRepository;
import com.alex.demo.repository.PersonRepository;
import com.alex.demo.util.JwtUtil;
import com.alex.demo.util.PasswordUtil;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@AllArgsConstructor
public class SecurityService {

    private final PersonRepository personRepository;
    private final PasswordUtil passwordUtil;
    private final JwtUtil jwtUtil;
    private final PasswordResetTokenRepository passwordResetTokenRepository;
    private final EmailService emailService;

    public LoginResponse login(String email, String password) {
        Optional<Person> maybePerson = personRepository.findByEmail(email);

        if (maybePerson.isPresent()) {
            Person person = maybePerson.get();

            if (passwordUtil.checkPassword(password, person.getPassword())) {
                String token = jwtUtil.createToken(person);
                return new LoginResponse(true, person.getRole(), null, token, person.getId());
            } else {
                return new LoginResponse(false, null, "Incorrect password", null, null);
            }
        }
        return new LoginResponse(false, null, "Person with email " + email + " not found", null, null);
    }

    public void processForgotPassword(String email) {
        personRepository.findByEmail(email).orElseThrow(
                () -> new RuntimeException("User not found with email: " + email));

        String code = String.valueOf((int) ((Math.random() * 900000) + 100000));

        passwordResetTokenRepository.deleteByEmail(email);

        PasswordResetToken resetToken = new PasswordResetToken();
        resetToken.setEmail(email);
        resetToken.setToken(code);
        resetToken.setExpiryDate(LocalDateTime.now().plusMinutes(15));

        passwordResetTokenRepository.save(resetToken);

        emailService.sendResetCode(email, code);
    }

    public void resetPassword(String email, String token, String newPassword) {
        PasswordResetToken resetToken = passwordResetTokenRepository.findByTokenAndEmail(token, email)
                .orElseThrow(() -> new RuntimeException("Invalid code or email"));

        if (resetToken.getExpiryDate().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("Reset code has expired");
        }

        Person person = personRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        person.setPassword(passwordUtil.hashPassword(newPassword));
        personRepository.save(person);

        passwordResetTokenRepository.delete(resetToken);

        emailService.sendPasswordChangeConfirmation(email);
    }

    public void registerCustomer(RegisterRequest request) {
        if (personRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new RuntimeException("Email is already in use!");
        }

        Person newCustomer = new Person();
        newCustomer.setName(request.getName());
        newCustomer.setEmail(request.getEmail());
        newCustomer.setAge(request.getAge());
        newCustomer.setPassword(passwordUtil.hashPassword(request.getPassword()));
        newCustomer.setRole("CUSTOMER");

        personRepository.save(newCustomer);
    }
}