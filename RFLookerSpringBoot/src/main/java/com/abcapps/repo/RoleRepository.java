package com.abcapps.repo;

import org.springframework.data.jpa.repository.JpaRepository;

import com.abcapps.Entities.Role;

public interface RoleRepository extends JpaRepository<Role, Long> {
	Role findByName(String name);
}