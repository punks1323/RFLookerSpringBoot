package com.abcapps.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.abcapps.entity.Mobile;
import com.abcapps.entity.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    User findByEmailId(String emailId);

    User findByEmailIdUuid(String emailIdUuid);

    User findByMobile(Mobile mobile);
}
