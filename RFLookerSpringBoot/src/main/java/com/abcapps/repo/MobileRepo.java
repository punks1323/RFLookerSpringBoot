package com.abcapps.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.abcapps.Entities.Phone;

@Repository
public interface MobileRepo extends JpaRepository<Phone, String> {

	Phone findByImei(String imei);
}
