package com.example.demo.services;

import com.example.demo.exceptions.NotFoundException;
import com.example.demo.models.Book;
import com.example.demo.models.User;
import com.example.demo.repositories.UserRepo;
import com.example.demo.security.JwtHelper;
import io.jsonwebtoken.ExpiredJwtException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {
    private final UserRepo userRepo;
    private final PasswordEncoder passwordEncoder;
    private final JwtHelper helper;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepo.findByEmail(username);
        if(user == null) {
            throw new UsernameNotFoundException("User not found with username: " + username);
        }
        return user;
    }

    public List<User> getAllUsers(){
        List<User> users = userRepo.findAll();
        if(users.isEmpty()){
            throw new NotFoundException("No users found in the repository");
        }
        return users;
    }

    public User getUserById(String id){
        User user = userRepo.findById(id).orElseThrow(() -> new RuntimeException("User not found"));
        return user;
    }

    public User getCurrentUserRole(Principal principal){
        return (User) this.loadUserByUsername(principal.getName());
    }

    public String checkAndRenewToken(User user){
        String token = user.getToken();
        try{
            boolean isExpired = this.helper.isTokenExpired(token);
        }catch(ExpiredJwtException e){
            String newToken = this.helper.generateToken(user);
            user.setToken(newToken);
            userRepo.save(user);
            return newToken;
        }
        return token;
    }

    public User setMembership(String userId, int monthsToExtend) {
        User user = userRepo.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        LocalDate now = LocalDate.now();

        LocalDate start = user.getMembershipEnd() != null && user.getMembershipEnd().isAfter(now)
                ? user.getMembershipEnd()
                : now;

        LocalDate end = start.plusMonths(monthsToExtend);

        user.setMembershipStart(now);
        user.setMembershipEnd(end);
        return userRepo.save(user);
    }

    public boolean isMembershipValid(User user) {
        return user.getMembershipEnd() != null && user.getMembershipEnd().isAfter(LocalDate.now());
    }

    public User addUser(User user){
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        String token = this.helper.generateToken(user);
        user.setToken(token);
        return userRepo.save(user);
    }

    public Map<String,Object> getUserBookActivity(String userId){
        User user = this.getUserById(userId);
        List<Book> allBooks = user.getBorrowed();
        if(allBooks.isEmpty()){
            return new HashMap<>();
        }
        Book currentBook = allBooks.get(0);
        List<Book> previousBooks = allBooks.stream().skip(1).collect(Collectors.toList());
        Map<String,Object> activity = new HashMap<>();
        activity.put("current",currentBook);
        activity.put("previous",previousBooks);
        return activity;
    }

    public void deleteUser(Principal principal){
        User principalUser = (User) loadUserByUsername(principal.getName());
        userRepo.delete(principalUser);
    }

}
