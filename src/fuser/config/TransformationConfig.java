package fuser.config;

import static tekai.standard.CommonTransformation.from;
import tekai.Transformation;
import tekai.standard.MultiTransformation;

/**
 *
 * @author SFPISA
 */
public class TransformationConfig {

    /*
     *
     * This class separates rules of TransformationRules in groups
     */
    private MultiTransformation tORACLE;
    private MultiTransformation tMSSQL;
    private MultiTransformation tMYSQL;
    private MultiTransformation tFIREBIRD;
    private MultiTransformation tPOSTGRES;

    private static Transformation t1 = from("SUBSTR", "FUNCTION").toValue("SUBSTRING");
    private static Transformation t3 = from("POSITION", "FUNCTION-POSITION").toValue("CHARINDEX").toType("FUNCTION");
    private static Transformation t4 = from("POSITION", "FUNCTION-POSITION").toValue("INSTR").toType("FUNCTION").toParamOrder(2, 1);
    private static Transformation t5 = from("\\|\\|", "CONCAT").toValue("+");
    private static Transformation t6 = from("\\|\\|", "CONCAT").toValue("CONCAT").toType("FUNCTION");
    private static Transformation t7 = from("CHAR_LENGTH", "FUNCTION").toValue("LEN");
    private static Transformation t8 = from("CHAR_LENGTH", "FUNCTION").toValue("LENGTH");
    private static Transformation t9 = from("END", "END").toValue("END CASE");

    public TransformationConfig(){
        tORACLE = new MultiTransformation();
        tMSSQL = new MultiTransformation();
        tMYSQL = new MultiTransformation();
        tFIREBIRD = new MultiTransformation();
        tPOSTGRES = new MultiTransformation();

        tORACLE.register(t4)
               .register(t8);

        tMSSQL.register(t1)
              .register(t3)
              .register(t5)
              .register(t7);

        tMYSQL.register(t4)
              .register(t6)
              .register(t9);
    }

    public MultiTransformation gettFIREBIRD() {
        return tFIREBIRD;
    }

    public MultiTransformation gettMSSQL() {
        return tMSSQL;
    }

    public MultiTransformation gettMYSQL() {
        return tMYSQL;
    }

    public MultiTransformation gettORACLE() {
        return tORACLE;
    }

    public MultiTransformation gettPOSTGRES() {
        return tPOSTGRES;
    }

}
