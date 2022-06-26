package com.ermetic.dosclient.config;


import lombok.Data;

@Data
public class DosTaskConfig {
    public int maxClientCount;
    public int minWaitTimeMS;
    public int maxWaitTimeMS;
}
