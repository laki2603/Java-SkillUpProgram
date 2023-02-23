package com.accenture;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class UserTests {

	@Autowired
    private TestRestTemplate restTemplate;
    
    @Autowired
    private UserRepository userRepository;
    
    private UserEntity testUser;
    @Before
    public void setUp() {
        testUser = new UserEntity(1L,"John Doe", "john.doe@example.com");
        userRepository.save(testUser);
    }
    
    @After
    public void tearDown() {
        userRepository.deleteAll();
    }
    
    @Test
    public void testCreateUser() {
        UserEntity newUser = new UserEntity(1L,"Jane Smith", "jane.smith@example.com");
        ResponseEntity<UserEntity> response = restTemplate.postForEntity("/users", newUser, UserEntity.class);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(newUser.getName(), response.getBody().getName());
        assertEquals(newUser.getEmail(), response.getBody().getEmail());
    }
    
    @Test
    public void testGetUserById() {
        ResponseEntity<UserEntity> response = restTemplate.getForEntity("/users/{id}", UserEntity.class, testUser.getId());
        System.out.println("gags");
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(testUser.getName(), response.getBody().getName());
        assertEquals(testUser.getEmail(), response.getBody().getEmail());
    }
    
    @Test
    public void testUpdateUser() {
        testUser.setName("New Name");
        testUser.setEmail("new.email@example.com");
        HttpEntity<UserEntity> request = new HttpEntity<>(testUser);
        ResponseEntity<UserEntity> response = restTemplate.exchange("/users/{id}", HttpMethod.PUT, request, UserEntity.class, testUser.getId());
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(testUser.getName(), response.getBody().getName());
        assertEquals(testUser.getEmail(), response.getBody().getEmail());
    }
    
    @Test
    public void testDeleteUser() {
        ResponseEntity<Void> response = restTemplate.exchange("/users/{id}", HttpMethod.DELETE, null, Void.class, testUser.getId());
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        assertFalse(userRepository.findById(testUser.getId()).isPresent());
    }
	

}
