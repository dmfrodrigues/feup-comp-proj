import pt.up.fe.comp.jmm.JmmNode;
import pt.up.fe.comp.jmm.analysis.table.Type;
import pt.up.fe.comp.jmm.ast.PostorderJmmVisitor;
import pt.up.fe.comp.jmm.report.Report;
import pt.up.fe.comp.jmm.report.ReportType;
import pt.up.fe.comp.jmm.report.Stage;

import java.util.List;

public class TypeVerificationVisitor extends PostorderJmmVisitor<List<Report>, Boolean> {

    private static final String terminalNodeName = "Terminal";
    private static final String binaryNodeName = "Binary";
    private static final String assignmentNodeName = "Assignment";
    private static final String arrayExprNodeName = "ArrayExpression";
    private static final String methodDeclNodeName = "MethodDeclaration";

    OurSymbolTable symbolTable;

    public TypeVerificationVisitor(OurSymbolTable symbolTable){
        this.symbolTable = symbolTable;

        addVisit(terminalNodeName, this::dealWithTerminal);
        addVisit(binaryNodeName, this::dealWithBinary);
        addVisit(assignmentNodeName, this::checkChildrenAreOfSameType);
        addVisit(arrayExprNodeName, this::dealWithArrayExpression);
        setDefaultVisit(TypeVerificationVisitor::defaultVisit);
    }

    private Boolean dealWithTerminal(JmmNode node, List<Report> reports){
        if (!node.get("type").equals("identifier")) return defaultVisit(node, reports);

        Type variableType = symbolTable.getLocalVariableType(
                node.getAncestor(methodDeclNodeName).map(ancestorNode -> ancestorNode.get("name")).orElse(null),
                node.get("type"),
                node.get("value"),
                Boolean.valueOf(node.get("isArray")));

        if (variableType == null){
            reports.add(new Report(
                    ReportType.WARNING,
                    Stage.SEMANTIC,
                    Integer.parseInt(node.get("line")),
                    Integer.parseInt(node.get("column")),
                    "Variable " + node.get("name") + " must be declared before being used"
            ));
            node.put("type", "error");
            node.put("isArray", "error");
        }
        else {
            node.put("type", variableType.getName());
            node.put("isArray", String.valueOf(variableType.isArray()));
        }

        return defaultVisit(node, reports);
    }

    private Boolean dealWithBinary(JmmNode node, List<Report> reports){
        return checkTheresNoArrayAccess(node, reports) &&
        checkChildrenAreOfSameType(node, reports);
    }

    private Boolean dealWithArrayExpression(JmmNode node, List<Report> reports){
        List<JmmNode> children = getChildren(node, reports);

        if (!children.get(0).get("isArray").equals("true")) {
            reports.add(new Report(
                    ReportType.WARNING,
                    Stage.SEMANTIC,
                    Integer.parseInt(node.get("line")),
                    Integer.parseInt(node.get("column")),
                    "Array type expected; found: '" + children.get(0).get("name") + "'"
            ));
        };

        return defaultVisit(node, reports);
    }

    private Boolean checkChildrenAreOfSameType(JmmNode node, List<Report> reports){
        List<JmmNode> children = getChildren(node, reports);

        if (children.get(0).get("type").equals("error") || children.get(1).get("type").equals("error")){
            node.put("type", "error");
            node.put("isArray", "error");
            return defaultVisit(node, reports);
        }

        Type leftVarType = getChildNodeType(0, node, children);
        Type rightVarType = getChildNodeType(1, node, children);

        if (!leftVarType.equals(rightVarType)) {
            reports.add(new Report(
                    ReportType.ERROR,
                    Stage.SEMANTIC,
                    Integer.parseInt(node.get("line")),
                    Integer.parseInt(node.get("column")),
                    "Type mismatch (" +
                            leftVarType.getName() + (leftVarType.isArray()?"[]":"") +
                            " != " +
                            rightVarType.getName() + (rightVarType.isArray()?"[]":"") + ")"
            ));
            node.put("type", "error");
            node.put("isArray", "error");
        }
        else {
            node.put("type", leftVarType.getName());
            node.put("isArray", String.valueOf(leftVarType.isArray()));
        }

        return defaultVisit(node, reports);
    }

    private Boolean checkTheresNoArrayAccess(JmmNode node, List<Report> reports){
        List<JmmNode> children = getChildren(node, reports);

        Type leftVarType = getChildNodeType(0, node, children);
        Type rightVarType = getChildNodeType(1, node, children);

        if (leftVarType.isArray() || rightVarType.isArray()) {
            reports.add(new Report(
                    ReportType.ERROR,
                    Stage.SEMANTIC,
                    Integer.parseInt(node.get("line")),
                    Integer.parseInt(node.get("column")),
                    "Operator '" + node.get("value") +
                            "' cannot be applied to '" +
                            leftVarType.getName() + (leftVarType.isArray() ? "[]" : "") + "', '" +
                            rightVarType.getName() + (rightVarType.isArray() ? "[]" : "") + "'"
            ));
            node.put("type", "error");
            node.put("isArray", "error");
        }

        return defaultVisit(node, reports);
    }

    private Type getChildNodeType(int index, JmmNode node, List<JmmNode> children) {
        return symbolTable.getLocalVariableType(
                node.getAncestor(methodDeclNodeName).map(ancestorNode -> ancestorNode.get("name")).orElse(null),
                children.get(index).get("type"),
                children.get(index).getOptional("value").orElse(""),
                Boolean.valueOf(children.get(index).get("isArray")));
    }

    private List<JmmNode> getChildren(JmmNode node, List<Report> reports) {
        List<JmmNode> result = node.getChildren();
        if (result.size() != 2) {
            reports.add(new Report(
                    ReportType.WARNING,
                    Stage.SEMANTIC,
                    Integer.parseInt(node.get("line")),
                    Integer.parseInt(node.get("column")),
                    "Binary node doesn't have 2 children. Something is wrong in syntactic phase"
            ));
        }
        return result;
    }

    private static Boolean defaultVisit(JmmNode node, List<Report> reports) {
        return true;
    }
}
