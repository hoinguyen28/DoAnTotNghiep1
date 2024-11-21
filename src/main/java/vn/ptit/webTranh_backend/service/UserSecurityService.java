package vn.ptit.webTranh_backend.service;

import vn.ptit.webTranh_backend.entity.User;
import org.springframework.security.core.userdetails.UserDetailsService;
public interface UserSecurityService extends UserDetailsService {
    public User findByUsername(String username);
}