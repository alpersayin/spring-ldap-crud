package com.alpersayin.ldapcrud.service;

import com.alpersayin.ldapcrud.model.LdapUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ldap.core.DirContextOperations;
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.ldap.core.support.AbstractContextMapper;
import org.springframework.ldap.core.support.BaseLdapNameAware;
import org.springframework.ldap.filter.EqualsFilter;
import org.springframework.ldap.support.LdapNameBuilder;
import org.springframework.ldap.support.LdapUtils;
import org.springframework.stereotype.Service;

import javax.naming.Name;
import javax.naming.directory.*;
import javax.naming.ldap.LdapName;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.List;

@Service
public class LdapCrudService implements BaseLdapNameAware {

    @Autowired
    private LdapTemplate ldapTemplate;
    
    private LdapName ldapName;

    @Override
    public void setBaseLdapPath(LdapName ldapName) {
        this.ldapName = ldapName;
    }

    public LdapUser findOne(String userId) {
        return ldapTemplate.lookup(buildDn(userId), new LdapUserContextMapper());
    }

    public void create(LdapUser ldapUser) {
        ldapTemplate.bind(buildDn(ldapUser.getUserid()), null, buildAttributes(ldapUser));
    }

    public void update(LdapUser ldapUser) {
        ldapTemplate.rebind(buildDn(ldapUser.getUserid()), null, buildAttributes(ldapUser));
    }

    public void remove(String userId) {
        ldapTemplate.unbind(buildDn(userId));
    }

    // This function can be used for updating any attributes.
    public void updatePassword(String userId, String password) {
        Attribute attr = new BasicAttribute("userPassword", encodePassword(password));
        ModificationItem item = new ModificationItem(DirContext.REPLACE_ATTRIBUTE, attr);
        ldapTemplate.modifyAttributes(buildDn(userId), new ModificationItem[]{item});
    }

    // This returns all entries from User named Organization Unit (ou).
    // Filter can be changed according to your needs.
    public List<LdapUser> findAll() {
        EqualsFilter filter = new EqualsFilter("ou", "User");
        return ldapTemplate.search(LdapUtils.newLdapName(LdapNameBuilder.newInstance(ldapName).build()), filter.encode(), new LdapUserContextMapper());
    }

    private Name buildDn(String userId) {
        return LdapNameBuilder.newInstance(ldapName)
                .add("ou", "User")
                .add("uid", userId)
                .build();
    }

    private Attributes buildAttributes(LdapUser ldapUser) {
        Attributes attrs = new BasicAttributes();
        BasicAttribute ocAttr = new BasicAttribute("objectClass");
        ocAttr.add("top");
        ocAttr.add("person");
        ocAttr.add("inetOrgPerson");
        attrs.put(ocAttr);
        attrs.put("ou", "User");
        attrs.put("uid", ldapUser.getUserid());
        attrs.put("givenName", ldapUser.getFirstname());
        attrs.put("sn", ldapUser.getLastname());
        attrs.put("cn", ldapUser.getFullname());
        attrs.put("mail", ldapUser.getEmail());
        attrs.put("userPassword", encodePassword(ldapUser.getPassword()));
        attrs.put("description", "You can add any attributes according to your LDAP directory");
        return attrs;
    }

    private String encodePassword(String password) {
        MessageDigest md = null;
        try {
            md = MessageDigest.getInstance("SHA1");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        byte[] passwordByte = password.getBytes();
        assert md != null;
        md.update(passwordByte, 0, passwordByte.length);
        byte[] encodedPassword = md.digest();
        return "{SHA}" + Base64.getEncoder().encodeToString(encodedPassword);
    }

    private static class LdapUserContextMapper extends AbstractContextMapper<LdapUser> {
        public LdapUser doMapFromContext(DirContextOperations ctx) {
            LdapUser ldapUser = new LdapUser();
            ldapUser.setUserid(ctx.getStringAttribute("uid"));
            ldapUser.setFirstname(ctx.getStringAttribute("givenName"));
            ldapUser.setLastname(ctx.getStringAttribute("sn"));
            ldapUser.setFullname(ctx.getStringAttribute("cn"));
            ldapUser.setEmail(ctx.getStringAttribute("mail"));
            return ldapUser;
        }
    }

}
