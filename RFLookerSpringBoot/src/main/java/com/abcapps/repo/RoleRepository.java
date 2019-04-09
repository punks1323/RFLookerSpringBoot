package com.abcapps.repo;

import org.springframework.data.jpa.repository.JpaRepository;

import com.abcapps.entity.Role;

public interface RoleRepository extends JpaRepository<Role, Long> {
	Role findByName(String name);
}