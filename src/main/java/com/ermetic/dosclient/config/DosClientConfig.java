package com.ermetic.dosclient.config;

import lombok.Data;


@Data
public class DosClientConfig {
    public String url;
    public int timeoutMS;
}
