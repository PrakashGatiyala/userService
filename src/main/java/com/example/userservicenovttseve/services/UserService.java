package com.example.userservicenovttseve.services;
import com.example.userservicenovttseve.dtos.UserDto;
import com.example.userservicenovttseve.models.Role;
import com.example.userservicenovttseve.models.User;
import com.example.userservicenovttseve.repositories.RoleRepository;
import com.example.userservicenovttseve.repositories.UserRepository;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class UserService {
    private UserRepository userRepository;
    private RoleRepository roleRepository;

    public UserService(UserRepository userRepository, RoleRepository roleRepository) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
    }

    public UserDto getUserDetails(Long userId) {
        Optional<User> userOptional = userRepository.findById(userId);

        if (userOptional.isEmpty()) {
            return null;
        }

        return UserDto.from(userOptional.get());
    }

    public UserDto setUserRoles(Long userId, List<Long> roleIds) {
        Optional<User> userOptional = userRepository.findById(userId);
        List<Role> roles = roleRepository.findAllByIdIn(roleIds);

        if (userOptional.isEmpty()) {
            return null;
        }

        User user = userOptional.get();
//        user.setRoles(Set.copyOf(roles));
        user.setRoles(new HashSet<>(roles));

        User savedUser = userRepository.save(user);

        return UserDto.from(savedUser);
    }
}
