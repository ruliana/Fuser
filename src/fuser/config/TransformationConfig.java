package fuser.config;

import java.util.List;
import tekai.Expression;
import static tekai.standard.CommonTransformation.from;
import tekai.Transformation;
import tekai.standard.MultiTransformation;
import static tekai.Helpers.word;
import static tekai.Expression.e;

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

    private static Transformation t1 = from(word("SUBSTR"), "FUNCTION").toValue("SUBSTRING");
    private static Transformation t3 = from(word("POSITION"), "FUNCTION-POSITION").toValue("CHARINDEX").toType("FUNCTION");
    private static Transformation t4 = from(word("POSITION"), "FUNCTION-POSITION").toValue("INSTR").toType("FUNCTION").toParamOrder(2, 1);
    private static Transformation t5 = from("\\|\\|", "CONCAT").toValue("+");
    private static Transformation t6 = from("\\|\\|", "CONCAT").toValue("CONCAT").toType("FUNCTION");
    private static Transformation t7 = from(word("CHAR_LENGTH"), "FUNCTION").toValue("LEN");
    private static Transformation t8 = from(word("CHAR_LENGTH"), "FUNCTION").toValue("LENGTH");
    private static Transformation t9 = from(word("END"), "END").toValue("END CASE");
    private static Transformation t10 = new Transformation() {
        @Override
        public boolean when(Expression expression) {
            return expression.hasValue(word("TRIM")) && expression.isType("FUNCTION");}
        @Override
        public Expression then(String value, String type, List<Expression> children) {
            return e(" LTRIM", "FUNCTION",e("RTRIM", "FUNCTION", children));
        }};
    private static Transformation t11 = from(word("SUBSTR"), "FUNCTION").toValue("SUBSTRING").toType("SUBSTRING-FIREBIRD");

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
              .register(t7)
              .register(t10);

        tMYSQL.register(t6);
              //.register(t9);

        tFIREBIRD.register(t11);
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
