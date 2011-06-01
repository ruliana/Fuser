package fuser.config;

import tekai.standard.SpecificTransformation;
import tekai.Expression;
import tekai.Transformation;
import static tekai.Expression.e;

public class SpecificConfig {

    private Transformation spMSSQL;
    private Transformation spORACLE;
    private Transformation spFIREBIRD;

    public SpecificConfig(){

         //SQL SERVER
        spMSSQL = new SpecificTransformation() {

            @Override
            public Expression then(Expression expression, Expression specific) {
                Expression top = e(" TOP", "TOP", e(specific.getChild(0).printValue(), "NUMBER"));

                if(expression.getChild(0).isType("UNION")){
                    Expression from = e(" FROM", "FROM", e(" AS", "ALIAS", e("(", "PARENTHESIS", expression), e(" SUBQUERY", "IDENTIFIER")));
                    Expression select = e("SELECT", "SELECT", top, e(" *", "IDENTIFIER"));
                    return e("SQL", "SQL", select, from);
                }else{
                    if(expression.getChild(0).getChild(0).isType("DISTINCT")){
                        expression.getChild(0).addChildAt(top, 1);
                }else
                    expression.getChild(0).addFirstChild(top);
                return expression;
                }
            }
            @Override
            public boolean when(Expression expression) {
                return expression.isType("LIMIT");
            }
        };

        //Oracle
        spORACLE = new SpecificTransformation() {

            @Override
            public Expression then(Expression expression, Expression specific) {
                Expression rownum = e(" <=", "OPERATOR", e(" ROWNUM", "IDENTIFIER"), e(specific.getChild(0).printValue(), "NUMBER"));
                Expression where = e(" WHERE", "WHERE", rownum);

                if(expression.getChild(0).isType("UNION")){
                    Expression from = e(" FROM", "FROM", e("(", "PARENTHESIS", expression));
                    Expression select = e("SELECT", "SELECT", e(" *", "IDENTIFIER"));
                    return e("SQL", "SQL", select, from, where);
                }else{
                    if(expression.getChildren().size() >= 3){
                        if(expression.getChild(2).isType("WHERE")){
                            where = expression.getChildren().remove(2);
                            Expression and = e(" AND", "BOOLEAN", where.getChildren().remove(0), rownum);
                            where.addChildren(and);
                        }
                    }
                expression.addChildAt(where, 2);
                return expression;
                }
            }
            @Override
            public boolean when(Expression expression) {
                return expression.isType("LIMIT");
            }
        };

        //FIREBIRD
        spFIREBIRD = new SpecificTransformation() {

            @Override
            public Expression then(Expression expression, Expression specific) {
                Expression top = e(" FIRST", "TOP", e(specific.getChild(0).printValue(), "NUMBER"));

                if(expression.getChild(0).isType("UNION")){
                    Expression from = e(" FROM", "FROM", e("(", "PARENTHESIS", expression));
                    Expression select = e("SELECT", "SELECT", top, e(" *", "IDENTIFIER"));
                    return e("SQL", "SQL", select, from);
                }else{
                    expression.getChild(0).addFirstChild(top);

                return expression;
                }
            }
            @Override
            public boolean when(Expression expression) {
                return expression.isType("LIMIT");
            }
        };

    }

    public Transformation getspFIREBIRD() {
        return spFIREBIRD;
    }

    public Transformation getspMSSQL() {
        return spMSSQL;
    }

    public Transformation getspORACLE() {
        return spORACLE;
    }


}
