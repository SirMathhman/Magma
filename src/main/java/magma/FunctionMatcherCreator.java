package magma;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

class FunctionMatcherCreator {
    
    static Matcher createInnerFunctionMatcher(InnerFunctionProcessor.InnerFunctionData data) {
        String syntheticInput = "fn " + data.functionName + "(" + (data.params != null ? data.params : "") + ")" + 
            (data.returnType != null ? " : " + data.returnType : "") + " => {" + data.body + "}";
        
        Pattern functionPattern = Pattern.compile("^fn\\s+(\\w+)\\s*\\(([^)]*)\\)(?:\\s*:\\s*(\\w+))?\\s*=>\\s*\\{(.*)}$", Pattern.DOTALL);
        return functionPattern.matcher(syntheticInput);
    }
}