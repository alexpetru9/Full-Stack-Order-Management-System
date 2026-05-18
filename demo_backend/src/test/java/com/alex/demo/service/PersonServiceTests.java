package com.alex.demo.service;

import com.alex.demo.config.ValidationException;
import com.alex.demo.model.Person;
import com.alex.demo.model.PersonCreateDTO;
import com.alex.demo.repository.PersonRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import com.alex.demo.util.PasswordUtil;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class PersonServiceTests {

    @Mock
    private PersonRepository personRepository;

    @Mock
    private PasswordUtil passwordUtil;

    @InjectMocks
    private PersonService personService;

    private AutoCloseable closeable;

    @BeforeEach
    void setUp() {
        closeable = MockitoAnnotations.openMocks(this);
    }

    @AfterEach
    void tearDown() throws Exception {
        closeable.close();
    }

    @Test
    void testGetPeople() {
        List<Person> people = List.of(new Person(), new Person());

        when(personRepository.findAll()).thenReturn(people);
        List<Person> result = personService.getPeople();

        assertEquals(2, result.size());
        verify(personRepository, times(1)).findAll();
        assertEquals(people, result);
    }

    @Test
    void testAddPerson() throws ValidationException {
        PersonCreateDTO personDTO = new PersonCreateDTO();
        personDTO.setName("John");
        personDTO.setPassword("password");
        personDTO.setAge(30);
        personDTO.setEmail("john@example.com");

        when(passwordUtil.hashPassword("password")).thenReturn("hashed-password");
        when(personRepository.save(any(Person.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Person result = personService.addPerson(personDTO);

        assertEquals("John", result.getName());
        assertEquals(30, result.getAge());
        assertEquals("john@example.com", result.getEmail());
        assertEquals("hashed-password", result.getPassword());
        verify(passwordUtil, times(1)).hashPassword("password");
        verify(personRepository, times(1)).save(any(Person.class));
    }

    @Test
    void testUpdatePerson() throws ValidationException {
        UUID uuid = UUID.randomUUID();
        Person existingPerson = new Person();
        existingPerson.setId(uuid);
        existingPerson.setName("John");
        existingPerson.setAge(30);
        existingPerson.setEmail("john@example.com");
        existingPerson.setPassword("old-hash");

        Person updatePayload = new Person();
        updatePayload.setId(uuid);
        updatePayload.setName("Jane");
        updatePayload.setAge(25);
        updatePayload.setEmail("jane@example.com");

        when(personRepository.findById(uuid)).thenReturn(Optional.of(existingPerson));
        when(personRepository.save(any(Person.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Person result = personService.updatePerson(uuid, updatePayload);

        assertEquals("Jane", result.getName());
        assertEquals(25, result.getAge());
        assertEquals("jane@example.com", result.getEmail());
        assertEquals("old-hash", result.getPassword());
        verify(personRepository, times(1)).findById(uuid);
        verify(personRepository, times(1)).save(any(Person.class));
    }

    @Test
    void testUpdatePersonNotFound() {
        UUID uuid = UUID.randomUUID();
        Person person = new Person();

        when(personRepository.findById(uuid)).thenReturn(Optional.empty());

        assertThrows(ValidationException.class, () -> personService.updatePerson(uuid, person));
        verify(personRepository, times(1)).findById(uuid);
        verify(personRepository, never()).save(any(Person.class));
        verifyNoInteractions(passwordUtil);
    }

    @Test
    void testDeletePerson() {
        UUID uuid = UUID.randomUUID();

        doNothing().when(personRepository).deleteById(uuid);
        personService.deletePerson(uuid);

        verify(personRepository, times(1)).deleteById(uuid);
    }
}