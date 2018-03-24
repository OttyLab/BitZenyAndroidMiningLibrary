package com.example.ottylab.bitzenyminer;


import junit.framework.Assert;

import org.junit.Test;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class UtilsUnitTest {
    @Test
    public void testLogQueue() throws Exception {
        final int CAPACITY = 10;

        BlockingQueue<String> queue = new LinkedBlockingQueue<>(CAPACITY);
        for (int i = 0; i < CAPACITY * 10; i++)  {
            String output = Utils.rotateStringQueue(queue, String.valueOf(i));

            int j = 0;
            for (String element: queue) {
                int expected = ((i < CAPACITY) ? 0 : i - CAPACITY + 1) + (j++);
                Assert.assertEquals(String.valueOf(expected), element);
            }
        }
    }
}