package fuser.config;

import fuser.ValidateInstruction;
import java.util.ArrayList;
import java.util.Arrays;

public class SqlValidate extends ValidateInstruction {

    public SqlValidate(){
        setAllowed(new ArrayList(Arrays.asList(
                "ABS", "AVG", "CHAR_LENGTH", "COUNT",
                "COALESCE", "EXISTS", "LOWER", "LTRIM",
                "MAX", "MIN", "NULLIF", "POSITION",
                "REPLACE", "ROUND", "RTRIM", "SUBSTR",
                "SUM", "TRIM", "UPPER")));
    }
}
