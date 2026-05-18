package com.alex.demo.controller;

import com.alex.demo.config.ValidationException;
import com.alex.demo.model.PersonCreateDTO;
import com.alex.demo.service.PersonService;
import com.alex.demo.model.Person;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;


@RestController
@AllArgsConstructor
@CrossOrigin
public class PersonController {
    private final PersonService personService;

    @GetMapping("/person")
    public List<Person> getPeople(HttpServletRequest request) {
        checkAdminAccess(request);
        return personService.getPeople();
    }

    @GetMapping("/person/{uuid}")
    public Person getPersonById(@PathVariable UUID uuid, HttpServletRequest request) {
        checkAdminAccess(request);
        return personService.getPersonById(uuid);
    }

    @GetMapping("/person/email/{email}")
    public Person getPersonByEmail(@PathVariable String email, HttpServletRequest request) {
        checkAdminAccess(request);
        return personService.getPersonByEmail(email);
    }

    @PostMapping("/person")
    public Person addPerson(
            @Valid @RequestBody PersonCreateDTO personDTO,
            HttpServletRequest request
    ) throws ValidationException {
        checkAdminAccess(request);
        return personService.addPerson(personDTO);
    }

    @PutMapping("/person/{uuid}")
    public Person updatePerson(@PathVariable UUID uuid,
                               @Valid @RequestBody Person person,
                               HttpServletRequest request)
            throws ValidationException {
        checkAdminAccess(request);
        return personService.updatePerson(uuid, person);
    }

    @DeleteMapping("/person/{uuid}")
    public void deletePerson(@PathVariable UUID uuid, HttpServletRequest request) {
        checkAdminAccess(request);
        personService.deletePerson(uuid);
    }

    @PatchMapping("/person/{uuid}")
    public Person patchPerson(@PathVariable UUID uuid,
                              @RequestBody Person personUpdates,
                              HttpServletRequest request) throws ValidationException {
        checkAdminAccess(request);
        return personService.patchPerson(uuid, personUpdates);
    }

    private void checkAdminAccess(HttpServletRequest request) {
        String role = (String) request.getAttribute("userRole");
        if (!"ADMIN".equals(role)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Access denied. Admins only.");
        }
    }
}