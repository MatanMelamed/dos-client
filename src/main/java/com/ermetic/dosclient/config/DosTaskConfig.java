package com.ermetic.dosclient.config;


import lombok.Data;

@Data
public class DosTaskConfig {
    public int minWaitTimeMS;
    public int maxWaitTimeMS;
}
