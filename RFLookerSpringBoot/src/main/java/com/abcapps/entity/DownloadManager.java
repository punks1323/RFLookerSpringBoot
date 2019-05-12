package com.abcapps.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.Date;

@Setter
@Getter
@Entity
@Table(name = "downloads_manager")
public class DownloadManager {

    public static final String STATUS_UPLOAD_DEFAULT = "STATUS_UPLOAD_DEFAULT";
    public static final String STATUS_UPLOAD_COMPLETE = "STATUS_UPLOAD_COMPLETE";
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    Long id;

    @JsonIgnore
    Long userId;

    String filePath;

    Date requestTime;

    String uploadStatus = STATUS_UPLOAD_DEFAULT;

    Date uploadCompleteTime;

    boolean active;

}
