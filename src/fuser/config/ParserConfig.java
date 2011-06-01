package fuser.config;

import static tekai.Helpers.word;
import tekai.Expression;
import tekai.Parselet;
import tekai.Parser;
import tekai.standard.AtomParselet;
import tekai.standard.BeforeMiddleAfterParselet;
import tekai.standard.InfixParselet;
import tekai.standard.PostfixParselet;
import tekai.standard.PrefixParselet;

/**
 *
 * @author SFPISA
 */
public class ParserConfig {

     public static void configureParser(Parser parser) {
        // PRECEDENCE (What to parse first. Higher numbers means more precedence)
        int x = 1;
        final int ATOM = x++;
        final int OR = x++;
        final int AND = x++;
        final int NOT = x++;
        final int IS = x++;
        final int LIKE = x++;
        final int POS = x++;
        final int LOGICOPER = x++;
        final int OPERATOR = x++;
        final int MULTIPLY = x++;
        final int SUM = x++;
        final int GROUPING = x++;
        final int GROUP = x++;
        final int IN = x++;
        final int FUNCTION = x++;
        final int CASE = x++;
        final int SELECT = x++;

        // SQL
        parser.register(new Parselet(SELECT) {
            @Override
            public boolean isPrefixParselet() {
                return true;
            }

            @Override
            public String startingRegularExpression() {
                return word("SELECT");
            }

            @Override
            public Expression parse() {
                Expression result = new Expression("SQL", "SQL");

                Expression fields = new Expression("SELECT", lastMatch());

                if(canConsume(word("DISTINCT"))){
                    fields.addChildren(new Expression("DISTINCT", lastMatch()));
                }

                do {
                    Expression field = nextExpression();
                    fields.addChildren(field);
                } while (canConsume(","));

                consumeIf(word("FROM"));

                Expression from = new Expression("FROM", lastMatch());
                do {
                    from.addChildren(nextExpression());
                } while(canConsume(","));

                while (canConsume(word("(?:INNER|RIGHT|LEFT)?\\s+JOIN|JOIN"))) {
                    Expression join = new Expression("JOIN", lastMatch());
                    join.addChildren(nextExpression());
                    consumeIf(word("ON"));
                    Expression on = new Expression("ON", lastMatch());
                    on.addChildren(nextExpression());
                    join.addChildren(on);
                    from.addChildren(join);
                }
                result.addChildren(fields, from);

                if(canConsume(word("WHERE"))){
                    Expression where = new Expression("WHERE", lastMatch());
                    where.addChildren(nextExpression());
                   result.addChildren(where);
                }

                if(canConsume(word("GROUP BY"))){
                    Expression group = new Expression("GROUP", lastMatch());
                    do{
                        group.addChildren(nextExpression());
                    }while(canConsume(","));
                    result.addChildren(group);
                }

                if(canConsume(word("UNION\\s+ALL|UNION"))){
                    Expression union = new Expression("UNION", lastMatch());
                    union.addChildren(result);
                    union.addChildren(nextExpression());
                    result = new Expression("SQL", "SQL");
                    result.addChildren(union);
                }

                if(canConsume(word("ORDER BY"))){
                    Expression order = new Expression("ORDER", lastMatch());
                    do {
                        Expression descOrder = nextExpression();
                        order.addChildren(descOrder);
                    } while(canConsume(","));

                    result.addChildren(order);
                }

                if(canConsume(word("LIMIT"))){
                    Expression limit = new Expression("LIMIT", lastMatch());
                    limit.addChildren(nextExpression());
                    if(canConsume(word("OFFSET"))){
                        Expression offset = new Expression("OFFSET", lastMatch());
                        offset.addChildren(nextExpression());
                        limit.addChildren(offset);
                    }
                    result.addChildren(limit);
                }

                return result;
            }
        });

         //CASE
         parser.register(new Parselet(CASE) {

            @Override
            public boolean isPrefixParselet() {
                return true;
            }

            @Override
            public String startingRegularExpression() {
                return word("CASE");
            }

            @Override
            protected Expression parse() {
                Expression ecase = new Expression("CASE", lastMatch());

                do{
                    if(canConsume(word("WHEN"))){
                        Expression when = new Expression("WHEN", lastMatch());
                        when.addChildren(nextExpression());

                        consumeIf(word("THEN"));
                        Expression then = new Expression("THEN", lastMatch());
                        then.addChildren(nextExpression());
                        when.addChildren(then);
                        ecase.addChildren(when);
                    }else if(canConsume(word("ELSE"))){
                        Expression eelse = new Expression("ELSE", lastMatch());
                        eelse.addChildren(nextExpression());
                        ecase.addChildren(eelse);
                    }else
                        ecase.addChildren(nextExpression());
                }while(cannotConsume(word("END")));
                ecase.addChildren(new Expression("END", lastMatch()));

                return ecase;
            }
        });

        //IN
        parser.register(new Parselet(IN) {

            @Override
            public boolean isLeftAssociativity() {
              return true;
            }

            @Override
            public boolean isPrefixParselet() {
                return false;
            }

            @Override
            public String startingRegularExpression() {
                return word("NOT\\s+IN|IN")+"\\s*\\(";
            }

            @Override
            protected Expression parse() {
                Expression in = new Expression("IN", lastMatch());
                in.addChildren(left());
                Expression sub = new Expression("IN-EXP", "IN-EXP");
                do{
                    sub.addChildren(nextExpression());
                }while(canConsume("\\,"));
                consumeIf("\\)");
                in.addChildren(sub);
                return in;
            }
        });

        // BOOLEAN
        parser.register(new InfixParselet(OR, word("OR"), "BOOLEAN"));
        parser.register(new InfixParselet(AND, word("AND"), "BOOLEAN"));
        parser.register(new PrefixParselet(NOT, word("NOT"), "NOT"));

        //LIKE
        parser.register(new InfixParselet(LIKE, word("NOT\\s+LIKE|LIKE"), "LIKE"));

        // ARITHMETIC
        parser.register(new InfixParselet(MULTIPLY, "(\\*|/|%)", "ARITHMETIC"));
        parser.register(new InfixParselet(SUM, "(\\+|-)", "ARITHMETIC"));

        //ALIAS
        parser.register(new InfixParselet(ATOM, word("AS"), "ALIAS"));

        //OPERATORS
        parser.register(new InfixParselet(LOGICOPER, "\\>\\=", "OPERATOR"));
        parser.register(new InfixParselet(LOGICOPER, "\\<\\=", "OPERATOR"));
        parser.register(new InfixParselet(LOGICOPER, "\\<\\>", "OPERATOR"));
        parser.register(new InfixParselet(OPERATOR, "\\=", "OPERATOR"));
        parser.register(new InfixParselet(OPERATOR, "\\>", "OPERATOR"));
        parser.register(new InfixParselet(OPERATOR, "\\<", "OPERATOR"));

         //IS
        parser.register(new InfixParselet(IS, word("IS"), "IS"));

        //CONCAT
        parser.register(new BeforeMiddleAfterParselet(ATOM, null, "\\|\\|", null, "CONCAT"));

        //GROUP BY
        //parser.register(new BeforeMiddleAfterParselet(GROUP, word("GROUP BY"), "\\,", null, "GROUPBY"));

        // GROUPING (parenthesis)
        parser.register(new BeforeMiddleAfterParselet(GROUPING, "\\(", null, "\\)", "PARENTHESIS"));

        // FUNCTION
        parser.register(new BeforeMiddleAfterParselet(FUNCTION, "("+ word("POSITION") + ")\\s*\\(", word("IN"), "\\)", "FUNCTION-POSITION"));
        parser.register(new BeforeMiddleAfterParselet(FUNCTION, "(\\w+)\\s*\\(", "\\,", "\\)", "FUNCTION"));

        //POSTFIX ASC DESC
        parser.register(new PostfixParselet(POS, word("ASC|DESC"), "ORDERING"));

        //NUMBER
        parser.register(new AtomParselet(ATOM, "\\d+(?:\\.\\d+)?", "NUMBER"));

        //STRING
        parser.register(new AtomParselet(ATOM, "\\'[^\\']*?\\'", "STRING"));

        //IDENTIFIER
        parser.register(new AtomParselet(ATOM, "(\\w+\\.\\w+|\\w+|\\*)", "IDENTIFIER"));

        //PARAMETER
        parser.register(new AtomParselet(ATOM, "(\\:\\w+)", "PARAMETER"));
    }

}
