package fuser;

import java.util.LinkedList;
import java.util.List;

import tekai.Expression;
import tekai.Parser;
import tekai.Printer;
import tekai.standard.MultiTransformation;
import fuser.config.ParserConfig;
import fuser.config.SqlPrinter;
import fuser.config.SqlValidate;
import fuser.config.TransformationConfig;
import tekai.UnparseableException;

public class Fuser {

    private Parser parser;
    private TransformationConfig tconf;
    private Printer printer;
    private Expression parsed;
    private ValidateInstruction valid;
    private String sql;

    public Fuser(String sqlString) {
        sql = sqlString;
        configParser();

        // Config Transformation
        tconf = new TransformationConfig();
        printer = new SqlPrinter();

        valid = new SqlValidate();
    }

    /**
     * Translate a SQL String to some Databases
     *
     * @return List(SQL Postgres,
     *         SQL SQL Server,
     *         SQL Oracle,
     *         SQL MySQL,
     *         SQL Firebird)
     */
    public List<String> fusion() {

        List<String> result = new LinkedList<String>();
        result.add(transformTo(tconf.gettPOSTGRES()));
        result.add(transformTo(tconf.gettMSSQL()));
        result.add(transformTo(tconf.gettORACLE()));
        result.add(transformTo(tconf.gettMYSQL()));
        result.add(transformTo(tconf.gettFIREBIRD()));

        return result;
    }

    private String transformTo(MultiTransformation t) {
        if(!validate(parse())) return "";
        return print(t.applyOn(parse()));
    }

    private Expression parse() {
        try {
            if (parsed == null) parsed = parser.parse();
        } catch (Exception e) {
            configParser();
            parsed = null;
            throw new UnparseableException(e.getMessage());
        }

        return parsed;
    }

    private String print(Expression e) {
        return printer.print(e);
    }

    private boolean validate(Expression e){
        return valid.validate(e);
    }

    private void configParser(){
        parser = new Parser(sql);
        ParserConfig.configureParser(parser);
    }

    /**
     * Change the parameters (:param) of a SQL String to NULL.
     * To run on database.
     * @return String
     */
    public String changeParam(){
        return transformTo(tconf.gettParam());
    }

    public String toORACLE() {
        return transformTo(tconf.gettORACLE());
    }

    public String toMYSQL() {
        return transformTo(tconf.gettMYSQL());
    }

    public String toMSSQL() {
        return transformTo(tconf.gettMSSQL());
    }

    public String toFIREBIRD() {
        return transformTo(tconf.gettFIREBIRD());
    }

    public String toPOSTGRES() {
        return transformTo(tconf.gettPOSTGRES());
    }
}
