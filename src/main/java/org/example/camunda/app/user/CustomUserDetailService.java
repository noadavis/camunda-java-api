package org.example.camunda.app.user;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.example.camunda.entity.User;
import org.example.camunda.repository.UserRepository;

import java.util.*;

@Service
public class CustomUserDetailService implements UserDetailsService {

    private final UserRepository userRepository;
    private final CustomPasswordEncoder passwordEncoder;

    public CustomUserDetailService(UserRepository userRepository, CustomPasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public boolean checkUsername(String username) {
        List<User> check = userRepository.checkUsername(username);
        return check == null || check.size() == 0;
    }

    public void createUser(String username, String password) {
        User user = new User();
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(password));
        user.setActive(true);
        user.setEmail(String.format("%s@local.loc", username));
        user.setRolesAsString("USER");
        userRepository.save(user);
    }

    public String createUser(User usr) {
        if (userRepository.findByUsername(usr.getUsername()) != null) return String.format("%s exist", usr.getUsername());
        try {
            usr.setPassword(passwordEncoder.encode(usr.getPassword()));
            User newUser = userRepository.save(usr);
            if (newUser.getId() > 0) return String.format("%s created", usr.getUsername());
        } catch (Exception e) {
            return String.format("%s not created [%s]", usr.getUsername(), e.getMessage());
        }
        return String.format("%s created", usr.getUsername());
    }

    public int updateUser(User user) {
        String newPassword = user.getPassword();
        if (user.getId() > 0) {
            Optional<User> dbRecord = findById(user.getId());
            String dbPassword = "";
            if (dbRecord.isPresent()) {
                dbPassword = dbRecord.get().getPassword();
            }
            if (!"".equals(newPassword)) user.setPassword(passwordEncoder.encode(newPassword));
            else user.setPassword(dbPassword);
        } else {
            user.setPassword(passwordEncoder.encode(newPassword));
        }
        userRepository.save(user);
        return user.getId();
    }

    public boolean updateUserInfo(User user, String action, String value) {
        switch (action) {
            case "email":
                user.setEmail(value);
                break;
            case "password":
                user.setPassword(passwordEncoder.encode(value));
                break;
            default:
                return false;
        }
        userRepository.save(user);
        return true;
    }

    public void blockUser(User user, boolean block) {
        user.setActive(block);
        userRepository.save(user);
    }

    public void deleteUser(User user) {
        userRepository.delete(user);
    }

    public User findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public Optional<User> findById(int id) {
        return userRepository.findById(id);
    }

    public Page<User> findAll(Pageable pageable) {
        return userRepository.findAll(pageable);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username);
        if (user == null) throw new UsernameNotFoundException("invalid_username_or_password");
        return user;
    }
}
