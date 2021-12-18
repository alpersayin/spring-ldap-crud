package com.alpersayin.ldapcrud.model;

import lombok.*;
import org.springframework.ldap.odm.annotations.Entry;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Entry(base = "ou=User", objectClasses = {"top", "person", "inetOrgPerson"}) // Configure here considering your LDAP directory
public class LdapUser {
    private String userid;
    private String fullname;
    private String firstname;
    private String lastname;
    private String email;
    private String password;
}
