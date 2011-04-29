package fuser.config;

import tekai.standard.MultiTransformation;
import static tekai.standard.CommonTransformation.from;

/**
 *
 * @author SFPISA
 */
public class TransformationConfig {

    /*
     *
     * This class, separate rules of TransformationRules in groups
     */
    private MultiTransformation tORACLE;
    private MultiTransformation tMSSQL;
    private MultiTransformation tMYSQL;
    private MultiTransformation tFIREBIRD;
    private MultiTransformation tPOSTGRES;

    public TransformationConfig(){
        tORACLE = new MultiTransformation();
        tMSSQL = new MultiTransformation();
        tMYSQL = new MultiTransformation();
        tFIREBIRD = new MultiTransformation();
        tPOSTGRES = new MultiTransformation();

        tORACLE.register(TransformationRules.t2)
                .register(TransformationRules.t7);
        tMSSQL.register(TransformationRules.t1)
                .register(TransformationRules.t2)
                .register(TransformationRules.t4)
                .register(TransformationRules.t6);
        tMYSQL.register(TransformationRules.t5)
                .register(TransformationRules.t8);
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
