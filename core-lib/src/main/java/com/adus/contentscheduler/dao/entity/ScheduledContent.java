package com.adus.contentscheduler.dao.entity;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ScheduledContent {
    private Content content;
    private float duration;
}
