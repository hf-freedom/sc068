package com.lab.service;

import com.lab.entity.User;
import com.lab.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AuditLogService auditLogService;

    public User createUser(User user) {
        User saved = userRepository.save(user);
        auditLogService.log("USER_CREATE", saved.getId(), "system",
                "创建用户: " + saved.getName());
        return saved;
    }

    public User updateUser(User user) {
        User saved = userRepository.save(user);
        auditLogService.log("USER_UPDATE", saved.getId(), "system",
                "更新用户: " + saved.getName());
        return saved;
    }

    public void deleteUser(String id) {
        Optional<User> userOpt = userRepository.findById(id);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            userRepository.deleteById(id);
            auditLogService.log("USER_DELETE", id, "system",
                    "删除用户: " + user.getName());
        }
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public Optional<User> getUserById(String id) {
        return userRepository.findById(id);
    }
}
