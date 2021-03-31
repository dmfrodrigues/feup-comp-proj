import pt.up.fe.comp.jmm.analysis.table.Symbol;
import pt.up.fe.comp.jmm.analysis.table.Type;

import java.util.HashSet;

public class OurSymbol extends Symbol {
    private final HashSet<String> attributes;
    private final OurScope scope;

    public OurSymbol(Type type, String name, HashSet<String> attributes, OurScope scope) {
        super(type, name);
        this.attributes = attributes;
        this.scope = scope;
    }

    public boolean isImport() { return attributes.contains("import"); }
    public boolean isClass() { return attributes.contains("class"); }
    public boolean isSuper() { return attributes.contains("extends"); }
    public boolean isField() { return attributes.contains("field"); }
    public boolean isMethod() { return attributes.contains("method"); }
    public boolean isReturn() { return attributes.contains("return"); }
    public boolean isParameter() { return attributes.contains("parameter"); }
    public boolean isVariable() { return attributes.contains("variable"); }

    public String getAttributes(){
        StringBuilder result = new StringBuilder(getType().getName() + ((getType().isArray()) ? "[]" : ""));
        for(String attribute : attributes){
            result.append(", ").append(attribute);
        }
        return result.toString();
    }

    public String getScope(){
        return scope.toString();
    }
}