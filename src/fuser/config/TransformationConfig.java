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
    public static MultiTransformation tORACLE;
    public static MultiTransformation tMSSQL;
    public static MultiTransformation tMYSQL;
    public static MultiTransformation tFIREBIRD;
    public static MultiTransformation tPOSTGRES;

    public TransformationConfig(){

    }
    
}
