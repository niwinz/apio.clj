package apio_examples;

import java.util.ArrayList;
import java.lang.Thread;

public class Test {
    public void run(ArrayList params) throws Exception {
        System.out.println("Java Thread: " + Thread.currentThread().getId() + " Args: " + params.toString());
    }
}
