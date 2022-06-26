package com.ermetic.dosclient.dos_client;

import com.ermetic.dosclient.config.DosTaskConfig;
import com.ermetic.dosclient.config.DosServiceConfig;
import com.ermetic.dosclient.config.DosClientConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.TimeUnit;

@Component
public class DosService implements IDosService {
    private static final Logger logger = LoggerFactory.getLogger(DosService.class);

    @Autowired
    private DosServiceConfig dosServiceConfig;
    @Autowired
    private DosClientConfig dosClientConfig;
    @Autowired
    private DosTaskConfig dosTaskConfig;



    private ExecutorService executorService;

    @Override
    public void startDosClients(int dosClientCount) {
        logger.info("Starting {} Dos Clients", dosClientCount);

        int numberOfCores = Runtime.getRuntime().availableProcessors();
        int numberOfThreads = (int) (numberOfCores * (1 + dosServiceConfig.getBlockingCoefficient()));
        executorService = Executors.newFixedThreadPool(Math.min(numberOfThreads, dosServiceConfig.getMaxThreads()));
        logger.info("Dos Client thread count {}", numberOfThreads);

        final DosClient dosClient = new DosClient(dosClientConfig);
        List<DosTask> dosTasks = new Random().ints(dosClientCount)
                .mapToObj(randomClientId -> new DosTask(randomClientId, dosTaskConfig, dosClient))
                .toList();

        // decorating tasks to run on repeat, on executor service pool
        List<RecurrentTask> recurrentDosTasks = dosTasks.stream()
                .map(dosTask -> new RecurrentTask(dosTask, executorService))
                .toList();

        recurrentDosTasks.forEach(RecurrentTask::startExecution);
    }


    @Override
    public void stopAllDosClients() {
        logger.info("Stopping all Dos Clients");
        executorService.shutdown();
        try {
            if (executorService.awaitTermination(dosServiceConfig.getMaxAwaitSec(), TimeUnit.SECONDS)) {
                executorService.shutdownNow();
            }
        } catch (InterruptedException e) {
            executorService.shutdownNow();
            Thread.currentThread().interrupt();
        }
        logger.info("Dos Clients finished");
    }

    /**
     * decoration for tasks to run again on execution service pool
     */
    private static class RecurrentTask implements Runnable {

        private final DosTask dosTask;
        private final ExecutorService executorService;

        private RecurrentTask(DosTask dosTask, ExecutorService executorService) {
            this.dosTask = dosTask;
            this.executorService = executorService;
        }

        @Override
        public void run() {
            dosTask.run();
            try {
                executorService.execute(this);
            } catch (RejectedExecutionException e) {
                logger.info("Recurrent task execution was rejected: {}", e.getMessage());
            }
        }

        public void startExecution() {
            executorService.execute(this);
        }
    }
}
