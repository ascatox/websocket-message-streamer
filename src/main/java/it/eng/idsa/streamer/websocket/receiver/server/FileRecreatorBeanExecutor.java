package it.eng.idsa.streamer.websocket.receiver.server;


import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;

import java.util.ResourceBundle;

/**
 * @author Antonio Scatoloni
 */

public class FileRecreatorBeanExecutor {
    private static final Logger logger = LogManager.getLogger(FileRecreatorBeanExecutor.class);
    private static FileRecreatorBeanExecutor instance;
    private static ResourceBundle configuration = ResourceBundle.getBundle("config");
    private Scheduler scheduler = null;

    private Integer port;
    private String keystorePath;
    private String keystorePassword;
    private Integer recreationFrequency;
    private String path;

    private FileRecreatorBeanExecutor() {
        trigger();
    }

    public static FileRecreatorBeanExecutor getInstance() {
        return getFileRecreatorBeanExecutor();
    }

    private static FileRecreatorBeanExecutor getFileRecreatorBeanExecutor() {
        if (instance == null) {
            synchronized (FileRecreatorBeanExecutor.class) {
                if (instance == null) {
                    instance = new FileRecreatorBeanExecutor();
                }
            }
        }
        return instance;
    }

    private void trigger() {
        try {
            Integer recreationFrequency = getRecreationFrequency() != null ? getRecreationFrequency() :
                    Integer.parseInt(configuration.getString("application.recreation.frequency"));
            JobDetail job = JobBuilder.newJob(FileRecreatorJob.class).build();

            // Trigger the job to run on the next round minute
            Trigger trigger = TriggerBuilder
                    .newTrigger()
                    .withSchedule(
                            SimpleScheduleBuilder.simpleSchedule()
                                    .withIntervalInMilliseconds(recreationFrequency)
                                    .repeatForever()).build();
            // schedule it
            Scheduler scheduler = new StdSchedulerFactory().getScheduler();
            scheduler.start();
            scheduler.scheduleJob(job, trigger);
        } catch (SchedulerException e) {
            logger.error("Error during Quartz Scheduling of FileRecreatorJob with stack: " + e.getMessage());
        }
    }

    public String getFileRecreatorResult() {
        try {
            for (JobExecutionContext jobContext : scheduler.getCurrentlyExecutingJobs()) {
                return jobContext.getResult().toString();
            }
        } catch (SchedulerException e) {
            logger.error("Error in Retrieving data from FileRecreator with stack: " + e.getMessage());
        }

        return null;
    }

    public Integer getPort() {
        return port;
    }

    public void setPort(Integer port) {
        this.port = port;
    }

    public String getKeystorePath() {
        return keystorePath;
    }

    public void setKeystorePath(String keystorePath) {
        this.keystorePath = keystorePath;
    }

    public String getKeystorePassword() {
        return keystorePassword;
    }

    public void setKeystorePassword(String keystorePassword) {
        this.keystorePassword = keystorePassword;
    }

    public Integer getRecreationFrequency() {
        return recreationFrequency;
    }

    public void setRecreationFrequency(Integer recreationFrequency) {
        this.recreationFrequency = recreationFrequency;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }
}
