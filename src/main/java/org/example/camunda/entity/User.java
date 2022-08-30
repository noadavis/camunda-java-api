package org.example.camunda.entity;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.example.camunda.app.user.Role;

import javax.persistence.*;
import java.util.*;

@Entity
@Table(name = "user")
public class User implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String username;
    private String email;
    private String password;
    private boolean active;
    private String roles;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    @Override
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public String getRolesString() {
        if (roles != null) return this.roles.replaceAll(" ", "");
        return "";
    }

    public String[] getRolesArray() {
        if (roles != null) return roles.split(",");
        return new String[] {};
    }

    public List<Role> getRoles() {
        List<Role> userRoles = new ArrayList<>();
        for (String role: roles.split(",")) {
            userRoles.add(Role.valueOf(role));
        }
        return userRoles;
    }

    public void setRoles(List<Role> roles) {
        List<String> userRoles = new ArrayList<>();
        for (Role role: roles) {
            userRoles.add(role.name());
        }
        if (userRoles.size() > 0) this.roles = String.join(",", userRoles);
        else this.roles = "";
    }

    public void setRolesAsString(String roles) {
        this.roles = roles;
    }

    public boolean checkRole(String needed) {
        if (roles == null) return false;
        return roles.contains(needed);
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return !isActive();
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return isActive();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return getRoles();
    }

}
