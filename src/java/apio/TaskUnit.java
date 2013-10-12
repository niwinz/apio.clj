package apio;

import java.util.Map;
import java.util.HashMap;
import java.util.ArrayList;


public abstract class TaskUnit {
    public Object exec(ArrayList params) {
        Map<String, Object> result = new HashMap<String, Object>();

        try {
            Object res = this.run(params);
            result.put("result", res);
        } catch (Exception e) {
            e.printStackTrace();
            result.put("error", e.getMessage());
        }

        return result;
    }

    public abstract Object run(ArrayList params) throws Exception;
}
