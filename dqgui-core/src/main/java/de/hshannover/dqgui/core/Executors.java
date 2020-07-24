package de.hshannover.dqgui.core;

import java.util.concurrent.ExecutorService;
import com.google.common.util.concurrent.ThreadFactoryBuilder;

public class Executors {
    public static final ExecutorService SERVICE = java.util.concurrent.Executors.newFixedThreadPool(8, new ThreadFactoryBuilder()
            .setDaemon(true)
            .build());
}
