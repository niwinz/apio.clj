package apio;

import java.util.Map;
import java.util.HashMap;
import java.util.ArrayList;

import clojure.lang.Symbol;
import clojure.lang.Keyword;


public abstract class TaskUnit {
    public Object exec(ArrayList params) {
        Map<Keyword, Object> result = new HashMap<Keyword, Object>();

        try {
            Object res = this.run(params);
            result.put(Keyword.intern("result"), res);
        } catch (Exception e) {
            e.printStackTrace();
            result.put(Keyword.intern("error"), e.getMessage());
        }

        return result;
    }

    public abstract Object run(ArrayList params) throws Exception;
}
