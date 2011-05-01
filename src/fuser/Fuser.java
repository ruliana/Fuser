package fuser;

import java.util.LinkedList;
import java.util.List;

import tekai.Expression;
import tekai.Parser;
import tekai.Printer;
import tekai.standard.MultiTransformation;
import fuser.config.ParserConfig;
import fuser.config.SqlPrinter;
import fuser.config.TransformationConfig;

public class Fuser {

    private Parser parser;
    private TransformationConfig tconf;
    private Printer printer;
    private Expression parsed;

    public Fuser(String sqlString) {
        this.parser = new Parser(sqlString);
        ParserConfig.configureParser(parser);

        // Config Transformation
        tconf = new TransformationConfig();
        printer = new SqlPrinter();
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
        return print(t.applyOn(parse()));
    }

    private Expression parse() {
        if (parsed == null) parsed = parser.parse();
        return parsed;
    }

    private String print(Expression e) {
        return printer.print(e);
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
