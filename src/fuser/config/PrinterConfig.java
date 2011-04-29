package fuser.config;

import java.util.Iterator;
import java.util.List;
import tekai.Expression;
import tekai.Printer;
import static tekai.Helpers.word;

/**
 *
 * @author SFPISA
 */
public class PrinterConfig extends Printer{

    @Override
    public String print(Expression e) {
        if (e.isType("SQL")) {
            return printChildren(e.getChildren(), "");
        } else if (e.isType("SELECT")) {
            if (e.getChild(0).isType("DISTINCT")) {
                return e.printValue() + print(e.getChildren().remove(0)) + printChildren(e.getChildren());
            } else {
                return e.printValue() + printChildren(e.getChildren());
            }
        } else if(e.isType("FROM")){
            return e.printValue() + printFrom(e.getChildren());
        } else if (e.isType("GROUP") || e.isType("ORDER")) {
            return e.printValue() + printChildren(e.getChildren());
        } else if (e.isType("LIMIT")
                        || e.isType("OFFSET")
                        || e.isType("CASE")
                        || e.isType("WHEN")
                        || e.isType("WHERE")
                        || e.isType("THEN")
                        || e.isType("ELSE")
                        || e.isType("NOT")
                        || e.isType("JOIN")
                        || e.isType("ON")) {
            return e.printValue() + printChildren(e.getChildren(), "");
        } else if (e.isType("CONCAT")) {
            return printChildren(e.getChildren(), e.printValue());
        } else if (e.isType("PARENTHESIS")) {
            return e.printValue() + printChildren(e.getChildren()) + ")";
        } else if (e.isType("FUNCTION")) {
            StringBuilder result = new StringBuilder();
            String separator = (e.hasValue(word("POSITION")) ? " IN" : ",");
            result.append(e.printValue()).append("(");
            result.append(printChildren(e.getChildren(), separator));
            return result.append(")").toString();
        } else if (e.isType("ARITHMETIC")
                        || e.isType("BOOLEAN")
                        || e.isType("LIKE")
                        || e.isType("ALIAS")
                        || e.isType("OPERATOR")) {
            return print(e.getChild(0)) + e.printValue() + print(e.getChild(1));
        } else if (e.isType("ORDERING")) {
            return print(e.getChild(0)) + e.printValue();
        } else {
            return e.printValue();
        }
    }

    protected String printFrom(List<Expression> e) {
        StringBuilder result = new StringBuilder();

        Iterator<Expression> iterator = e.iterator();
        if (iterator.hasNext())
            result.append(print(iterator.next()));

        while (iterator.hasNext()) {
            Expression exp = iterator.next();
            result.append(exp.isType("JOIN") ? "" : ",");
            result.append(print(exp));
        }

        return result.toString();
    }


}
