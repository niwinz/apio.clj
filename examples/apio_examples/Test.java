package apio_examples;

import java.util.ArrayList;
import java.lang.Thread;
import apio.TaskUnit;

public class Test extends TaskUnit {
    public Object run(ArrayList params) throws Exception {
        System.out.println("Java Thread: " + Thread.currentThread().getId() + " Args: " + params.toString());
        System.out.println(2/0);
        return new Object();
    }
}
