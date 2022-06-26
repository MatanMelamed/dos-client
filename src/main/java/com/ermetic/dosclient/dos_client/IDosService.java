package com.ermetic.dosclient.dos_client;

public interface IDosService {
    void startDosClients(int httpClientCount);

    void stopAllDosClients();
}
