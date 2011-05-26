package fuser;

import fuser.config.SpecificConfig;
import tekai.Expression;
import tekai.Parser;
import tekai.Printer;
import tekai.standard.MultiTransformation;
import fuser.config.ParserConfig;
import fuser.config.SqlPrinter;
import fuser.config.SqlValidate;
import fuser.config.TransformationConfig;
import tekai.Transformation;
import tekai.UnparseableException;

public class Fuser {

    private Parser parser;
    private TransformationConfig tconf;
    private Printer printer;
    private Expression parsed;
    private ValidateInstruction valid;
    private String sql;
    private SpecificConfig specific;

    public Fuser(String sqlString) {
        sql = sqlString;

        //Configurations
        configParser();
        tconf = new TransformationConfig();
        printer = new SqlPrinter();
        valid = new SqlValidate();
        specific = new SpecificConfig();
    }

    private String transformTo(MultiTransformation t, Transformation specific) {
        if(!validate(parse())) return "";
        if(specific == null)
            return print(t.applyOn(parse()));
        else
            return print(specific.applyOn(t.applyOn(parse())));
    }

    private String transformTo(MultiTransformation t) {
        return transformTo(t, null);
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
     * Change the parameters (:param) of a SQL String to NULL. To run on database.
     * @return String
     */
    public String changeParam(){
        return transformTo(tconf.gettParam());
    }

    /**
     * Method to convert a SQL statement for Oracle
     * @return String SQL Oracle
     */
    public String toOracle() {
        return transformTo(tconf.gettORACLE(), specific.getspORACLE());
    }

    /**
     * Method to convert a SQL statement for MySQL
     * @return String SQL MySQL
     */
    public String toMySQL() {
        return transformTo(tconf.gettMYSQL());
    }

    /**
     * Method to convert a SQL statement for SQL Server
     * @return String SQL Server
     */
    public String toSQLServer() {
        return transformTo(tconf.gettMSSQL(), specific.getspMSSQL());
    }

    /**
     * Method to convert a SQL statement for Firebird
     * @return String SQL Firebird
     */
    public String toFirebird() {
        return transformTo(tconf.gettFIREBIRD(), specific.getspFIREBIRD());
    }

    /**
     * Method to convert a SQL statement for PostgreSQL
     * @return String SQL PostgreSQL
     */
    public String toPostgreSQL() {
        return transformTo(tconf.gettPOSTGRES());
    }
}
