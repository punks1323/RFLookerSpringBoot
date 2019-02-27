package com.abcapps.Entities;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Setter
@Getter
public class RFLookerDirectory {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @OneToOne(mappedBy = "rfLookerDirectory")
    private User user;
    String jsonLocation;
}
