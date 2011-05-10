package fuser;

import java.util.List;
import tekai.Expression;
import static tekai.Helpers.word;

public class ValidateInstruction {

    private List<String> allowed;
    private List<String> notAllowed;

    public boolean validate(Expression e){

        if(!checkExpression(e))
            throw new FuserException("This expression is not allowed: " + e.getValue());

        for (Expression child : e.getChildren())
            validate(child);

        return true;
    }

    private boolean checkExpression(Expression e) {

        if(e.isType("FUNCTION")){
            for (String func : allowed) {
                if(e.getValue().matches(word(func)))
                    return true;
            }
            return false;
        }
        return true;
    }

    public void setAllowed(List<String> allowed) {
        this.allowed = allowed;
    }

}
