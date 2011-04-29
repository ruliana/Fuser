package fuser.config;

import tekai.Transformation;
import static tekai.standard.CommonTransformation.from;
import static tekai.standard.CommonTransformation.fromType;
/**
 *
 * @author SFPISA
 */
public class TransformationRules {

    /**
     * This class contains all possible rules for Transformation
     */

    public static Transformation t1 = from("SUBSTR", "FUNCTION").toValue("SUBSTRING");
    public static Transformation t2 = from("POSITION", "FUNCTION").toValue("CHARINDEX");
    public static Transformation t3 = from("POSITION", "FUNCTION").toValue("INSTR").toParamOrder(2, 1);
    public static Transformation t4 = from("\\|\\|", "CONCAT").toValue("+");
    public static Transformation t5 = from("\\|\\|", "CONCAT").toValue("CONCAT").toType("FUNCTION");
    public static Transformation t6 = from("CHAR_LENGTH", "FUNCTION").toValue("LEN");
    public static Transformation t7 = from("CHAR_LENGTH", "FUNCTION").toValue("LENGTH");
    public static Transformation t8 = from("END", "END").toValue("END CASE");

    //TRIM, (RTRIM, LTRIM)
    //CONVERT
    //LIMIT

}
