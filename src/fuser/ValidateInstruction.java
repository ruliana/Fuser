package fuser;

import java.util.ArrayList;
import java.util.List;
import tekai.Expression;
import static tekai.Helpers.word;

public class ValidateInstruction {

    private List<ValidExpression> valids;

    public ValidateInstruction() {
        this.valids = new ArrayList<ValidExpression>();
    }

    public boolean validate(Expression e){

        if(!checkExpression(e))
            throw new FuserException("This expression is not allowed: " + e.getValue());

        for (Expression child : e.getChildren())
            validate(child);

        return true;
    }

    private boolean checkExpression(Expression e) {
        boolean result = true;
        for (ValidExpression v : valids) {
            boolean boolValue = false;
            boolean boolChild = false;
            if(e.isType(v.type)){
                if(e.getValue().matches(word(v.value))){
                    boolValue = true;
                    if((v.numChild != null ?  v.numChild : e.getChildren().size()) == e.getChildren().size())
                        boolChild = true;

                    if(boolValue && boolChild) return true;
                    if(!boolChild)
                        throw new FuserException("The expression "+e.getValue()+" should have "+v.numChild+" parameters, but has " + e.getChildren().size());
                }
                result = false;
            }
        }
        return result;
    }

    public void validExpression(String type, String value, Integer numChild){
        valids.add(new ValidExpression(type, value, numChild));
    }

    private static class ValidExpression{
        private String type;
        private String value;
        private Integer numChild;

        private ValidExpression(String type, String value, Integer numChild) {
            this.type = type;
            this.value = value;
            this.numChild = numChild;
        }

    }
}
