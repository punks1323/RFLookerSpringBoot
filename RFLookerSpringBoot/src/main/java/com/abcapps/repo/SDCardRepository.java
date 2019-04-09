package com.abcapps.repo;

import com.abcapps.entity.Mobile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MobileRepo2 extends JpaRepository<Mobile, String> {

	Mobile findByImei(String imei);
}
