package com.ermetic.dosclient.config;

import lombok.Data;

@Data
public class DosServiceConfig {
    public float blockingCoefficient;
    public int maxAwaitSec;
    public int maxThreads;
}
