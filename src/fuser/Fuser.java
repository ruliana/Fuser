package fuser;

import fuser.config.ParserConfig;
import fuser.config.TransformationConfig;
import java.util.LinkedList;

import java.util.List;
import tekai.Expression;
import tekai.Parser;
import tekai.Printer;
import tekai.Transformation;

public class Fuser {

	private Parser parser;
        private Transformation transformation;
        private static Printer printer;

	public Fuser(String sqlString) {
		this.parser = new Parser(sqlString);
                ParserConfig.configureParser(parser);

                //Config Transformation
	}

        /**
         * Translate a SQL String to some Databases
         * @return List(SQL Postgres,
         *              SQL SQL Server,
         *              SQL Oracle,
         *              SQL MySQL,
         *              SQL Firebird)
         */
        public List<String> fusion(){

            List<String> result = new LinkedList<String>();
            result.add(transformTo(TransformationConfig.tPOSTGRES));
            result.add(transformTo(TransformationConfig.tMSSQL));
            result.add(transformTo(TransformationConfig.tORACLE));
            result.add(transformTo(TransformationConfig.tMYSQL));
            result.add(transformTo(TransformationConfig.tFIREBIRD));

            return result;

        }

        private String transformTo(Transformation t){
            transformation = t;
            return print(transformation.applyOn(parse()));
        }

        private Expression parse(){
            return parser.parse();
        }

        private String print(Expression e){
            return printer.print(e);
        }
}
