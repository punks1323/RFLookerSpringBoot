package com.abcapps.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.abcapps.Entities.Mobile;

@Repository
public interface MobileRepo extends JpaRepository<Mobile, String> {

	Mobile findByImei(String imei);
}
