package com.example.userservicenovttseve.services;

import com.example.userservicenovttseve.models.Role;
import com.example.userservicenovttseve.repositories.RoleRepository;
import org.springframework.stereotype.Service;

@Service
public class RoleService {
    private RoleRepository roleRepository;

    public RoleService(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    public Role createRole(String name) {
        Role role = new Role();
        role.setName(name);

        roleRepository.save(role);
        return role;
    }
}
