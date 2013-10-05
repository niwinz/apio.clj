package apio_examples;

import java.util.ArrayList;
import java.lang.Thread;
import clojure.lang.PersistentVector;

public class Test {
    public void run(PersistentVector params) throws Exception {
        ArrayList<Integer> data = new ArrayList<Integer>(params);
        System.out.println("Java Thread: " + Thread.currentThread().getId() + " Args: " + data.toString());
    }
}
