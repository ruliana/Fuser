package fuser.config;

import tekai.Expression;
import tekai.Printer;

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
        } else if (e.isType("FROM") || e.isType("GROUP") || e.isType("ORDER")) {
            return e.printValue() + printChildren(e.getChildren());
        } else if (e.isType("LIMIT")
                        || e.isType("OFFSET")
                        || e.isType("CASE")
                        || e.isType("WHEN")
                        || e.isType("WHERE")
                        || e.isType("THEN")
                        || e.isType("ELSE")
                        || e.isType("NOT")) {
            return e.printValue() + printChildren(e.getChildren(), "");
        } else if (e.isType("CONCAT")) {
            return printChildren(e.getChildren(), e.printValue());
        } else if (e.isType("PARENTHESIS")) {
            return e.printValue() + printChildren(e.getChildren()) + ")";
        } else if (e.isType("FUNCTION")) {
            StringBuilder result = new StringBuilder();
            result.append(e.printValue()).append("(");
            result.append(printChildren(e.getChildren()));
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


}
