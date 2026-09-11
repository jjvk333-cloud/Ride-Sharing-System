package com.velto.pattern;

import com.velto.pattern.singleton.AppConfigSingleton;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.junit.jupiter.api.Assertions.*;

class SingletonPatternTests {

    @AfterEach
    void tearDown() {
        AppConfigSingleton.getInstance().resetToDefaults();
    }

    @Test
    @DisplayName("AppConfigSingleton returns the exact same instance in sequential calls")
    void testSequentialSingletonInstance() {
        AppConfigSingleton instance1 = AppConfigSingleton.getInstance();
        AppConfigSingleton instance2 = AppConfigSingleton.getInstance();

        assertNotNull(instance1);
        assertNotNull(instance2);
        assertSame(instance1, instance2, "Both references must point to the exact same memory object");
        assertEquals(instance1.hashCode(), instance2.hashCode());
    }

    @Test
    @DisplayName("Multithreaded Concurrent Access returns the identical Singleton instance across 50 threads")
    void testMultithreadedConcurrentSingleton() throws InterruptedException {
        int threadCount = 50;
        ExecutorService executorService = Executors.newFixedThreadPool(threadCount);
        CountDownLatch latch = new CountDownLatch(1);
        List<AppConfigSingleton> instances = Collections.synchronizedList(new ArrayList<>());

        for (int i = 0; i < threadCount; i++) {
            executorService.submit(() -> {
                try {
                    latch.await(); // All threads wait to hit getInstance() simultaneously
                    instances.add(AppConfigSingleton.getInstance());
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            });
        }

        // Trigger simultaneous execution
        latch.countDown();
        executorService.shutdown();
        while (!executorService.isTerminated()) {
            Thread.sleep(10);
        }

        assertEquals(threadCount, instances.size());
        AppConfigSingleton expectedInstance = AppConfigSingleton.getInstance();

        for (AppConfigSingleton instance : instances) {
            assertSame(expectedInstance, instance, "Every concurrent thread must receive the exact same singleton instance");
        }
    }

    @Test
    @DisplayName("Reflection Attack is rejected by the private constructor")
    void testReflectionAttackPrevention() throws Exception {
        AppConfigSingleton existingInstance = AppConfigSingleton.getInstance();
        assertNotNull(existingInstance);

        Constructor<AppConfigSingleton> constructor = AppConfigSingleton.class.getDeclaredConstructor();
        constructor.setAccessible(true);

        InvocationTargetException thrown = assertThrows(InvocationTargetException.class, constructor::newInstance);
        assertTrue(thrown.getCause() instanceof IllegalStateException, "Expected IllegalStateException on reflection instantiation");
        assertTrue(thrown.getCause().getMessage().contains("already initialized"));
    }

    @Test
    @DisplayName("Serialization and Deserialization preserve Singleton identity via readResolve")
    void testSerializationPreservesSingleton() throws IOException, ClassNotFoundException {
        AppConfigSingleton original = AppConfigSingleton.getInstance();
        original.setSurgeMultiplier(2.2);

        // Serialize
        ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
        ObjectOutputStream out = new ObjectOutputStream(byteOut);
        out.writeObject(original);
        out.flush();

        // Deserialize
        ByteArrayInputStream byteIn = new ByteArrayInputStream(byteOut.toByteArray());
        ObjectInputStream in = new ObjectInputStream(byteIn);
        AppConfigSingleton deserialized = (AppConfigSingleton) in.readObject();

        assertSame(original, deserialized, "Deserialized instance must be identical to the original instance via readResolve()");
        assertEquals(2.2, deserialized.getSurgeMultiplier());
    }

    @Test
    @DisplayName("Runtime updates immediately propagate to all components")
    void testRuntimeConfigUpdatePropagation() {
        AppConfigSingleton config = AppConfigSingleton.getInstance();
        assertEquals(1.5, config.getSurgeMultiplier());

        config.updateConfig(75.0, 15.0, 1.8, 0.75, 7.5, "USD", true, 6);

        AppConfigSingleton checkInstance = AppConfigSingleton.getInstance();
        assertEquals(75.0, checkInstance.getBaseFare());
        assertEquals(15.0, checkInstance.getPerKmRate());
        assertEquals(1.8, checkInstance.getSurgeMultiplier());
        assertEquals(0.75, checkInstance.getSharedDiscountMultiplier());
        assertEquals(7.5, checkInstance.getPlatformFeePercentage());
        assertEquals("USD", checkInstance.getCurrency());
        assertTrue(checkInstance.isMaintenanceMode());
        assertEquals(6, checkInstance.getMaxSeatsPerBooking());
    }
}
