package com.techyourchance.multithreading.demonstrations.visibility;

import java.util.concurrent.atomic.AtomicInteger;

//problems of threads: visibility, autonomy, happens before

//the problem here: the sCount is not visible between threads. consumer doesnt see the changes there.
//this is because sCount is cached locally and not reading it from memory
//this is a problem of multiple threads share the same state
//the fix here is to add the 'volatile' word to the sCount so it will be located in the main memory
//volatile - only concern with visibility (not autonomy)
public class VisibilityDemonstration {

    private static int sCount = 0;

    public static void main(String[] args) {
        new Consumer().start();
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            return;
        }
        new Producer().start();
    }

    static class Consumer extends Thread {
        @Override
        public void run() {
            int localValue = -1;
            while (true) {
                if (localValue != sCount) {
                    System.out.println("Consumer: detected count change " + sCount);
                    localValue = sCount;
                }
                if (sCount >= 5) {
                    break;
                }
            }
            System.out.println("Consumer: terminating");
        }
    }

    static class Producer extends Thread {
        @Override
        public void run() {
            while (sCount < 5) {
                int localValue = sCount;
                localValue++;
                System.out.println("Producer: incrementing count to " + localValue);
                sCount = localValue;
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    return;
                }
                //another issue is above: synchronization
                //if we will remove the sleep, we will see some strange behaviour.
                //we need to LOCK here the producer and the costumer in order to have a
                //robust behaviour.
                //note 1: LOCK should be inside the while loop and the condition should be
                //inside the while loop as well.
                //note 2: we don't need the volatile for the sCount any more. the LOCk take
                //care of that
                //problems: if one hold the LOCK for a lot of time - issue
                //problem: if one never release the LOCK - big issue. no one will execute
                //problem: two LOCKS - DEAD LOCK
            }
            System.out.println("Producer: terminating");
        }
    }

}