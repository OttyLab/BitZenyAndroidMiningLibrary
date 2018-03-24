package com.example.ottylab.bitzenyminer;

import java.util.concurrent.BlockingQueue;

/**
 * Created by ochikage on 2018/02/18.
 */

public class Utils {
    public static String rotateStringQueue(BlockingQueue<String> queue, String next) {
        while (!queue.offer(next)) {
            try {
                queue.take();
            } catch (InterruptedException e){
                ;
            }
        }

        StringBuilder logs = new StringBuilder();
        for (String element: queue) {
            logs.append(element);
        }

        return logs.toString();
    }
}
