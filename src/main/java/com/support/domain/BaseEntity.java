package com.support.domain;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.Date;

@Getter
@MappedSuperclass
public class BaseEntity {
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "CREATED_TIME", nullable = false, updatable = false)
    private Date createdTime;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "UPDATED_TIME", nullable = false)
    private Date updatedTime;

    @PrePersist
    protected void onPersist() {
        this.createdTime = this.updatedTime = new Date();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedTime = new Date();
    }
}
