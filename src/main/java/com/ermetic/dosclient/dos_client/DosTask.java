package com.ermetic.dosclient.dos_client;

import com.ermetic.dosclient.config.DosTaskConfig;
import lombok.SneakyThrows;

import java.util.Random;
import java.util.concurrent.TimeUnit;

public class DosTask implements Runnable {
    private final int clientId;
    private final DosTaskConfig clientConfig;
    private final DosClient dosClient;
    private final Random random;

    DosTask(int clientId, DosTaskConfig dosTaskConfig, DosClient dosClient) {
        this.clientId = clientId;
        this.dosClient = dosClient;
        this.clientConfig = dosTaskConfig;
        this.random = new Random();
    }

    @SneakyThrows
    @Override
    public void run() {
        dosClient.sendRequest(clientId);
        int waitMS = random.nextInt(clientConfig.getMinWaitTimeMS(), clientConfig.getMaxWaitTimeMS());
        TimeUnit.MILLISECONDS.sleep(waitMS);
    }
}
