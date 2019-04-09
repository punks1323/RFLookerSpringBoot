package com.abcapps.repo;

import com.abcapps.entity.Mobile;
import com.abcapps.entity.SDCard;
import com.abcapps.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SDCardRepository extends JpaRepository<SDCard, String> {

	SDCard findByUser(User user);
}
