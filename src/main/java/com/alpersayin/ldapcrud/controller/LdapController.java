package com.alpersayin.ldapcrud.controller;

import com.alpersayin.ldapcrud.model.LdapUser;
import com.alpersayin.ldapcrud.service.LdapCrudService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.springframework.http.HttpStatus.OK;

@RestController
@AllArgsConstructor
@RequestMapping("/api")
public class LdapController {

    private final LdapCrudService ldapCrudService;

    @PostMapping("/bind-user")
    public ResponseEntity<String> bindLdapUser(@RequestBody LdapUser ldapUser) {
        ldapCrudService.create(ldapUser);
        return ResponseEntity.status(OK).body("New User added with userid : [" + ldapUser.getUserid() + "]");
    }

    @PutMapping("/rebind-user")
    public ResponseEntity<String> rebindLdapUser(@RequestBody LdapUser ldapUser) {
        ldapCrudService.update(ldapUser);
        return ResponseEntity.status(OK).body("User updated with userid : [" + ldapUser.getUserid() + "]");
    }

    @PutMapping("/update-password/{userId}")
    public ResponseEntity<String> updatePassword(@PathVariable String userId, @RequestBody String password) {
        ldapCrudService.updatePassword(userId, password);
        return ResponseEntity.status(OK).body("Password updated.");
    }

    @DeleteMapping("/unbind-user/{userId}")
    public ResponseEntity<String> unbindLdapUser(@PathVariable String userId) {
        ldapCrudService.remove(userId);
        return ResponseEntity.status(OK).body("User deleted with userid : [" + userId + "]");
    }

    @GetMapping("/users")
    public ResponseEntity<List<LdapUser>> getLdapUsers() {
        return new ResponseEntity<>(ldapCrudService.findAll(), OK);
    }

    @GetMapping("/users/{userId}")
    public ResponseEntity<LdapUser> findLdapUser(@PathVariable String userId) {
        return new ResponseEntity<>(ldapCrudService.findOne(userId), OK);
    }
}
