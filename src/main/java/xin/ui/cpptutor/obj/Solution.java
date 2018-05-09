package xin.ui.cpptutor.obj;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Solution {

    private Map<PatternVertex, Vertex> nodeMapping = new HashMap<>();
    private Map<String, String> varMapping = new HashMap<>();
    private List<PatternVertex> approxNodes = new ArrayList<>();

    public Solution clone() {
        Solution sol = new Solution();
        sol.nodeMapping = new HashMap<>(nodeMapping);
        sol.varMapping = new HashMap<>(varMapping);
        sol.approxNodes = new ArrayList<>(approxNodes);
        return sol;
    }

    public Map<PatternVertex, Vertex> getNodeMapping() {
        return nodeMapping;
    }

    public Map<String, String> getVarMapping() {
        return varMapping;
    }

    public List<PatternVertex> getApproxNodes() {
        return approxNodes;
    }

    public String toString() {

        String res = "*******solution********\n";
        res += "^^^nodeMapping^^^\n";
        for (Map.Entry<PatternVertex, Vertex> entry : nodeMapping.entrySet()) {
            res += "key: " + entry.getKey() + " value: " + entry.getValue();
            res += "\n";
        }
        res += "^^^varMapping^^^\n";
        for (Map.Entry<String, String> entry : varMapping.entrySet()) {
            res += "key: " + entry.getKey() + " value: " + entry.getValue();
            res += "\n";
        }
        res += "^^^approxNodes^^^\n";
        for (PatternVertex p : approxNodes) {
            res += p + " ";
            res += "\n";
        }
        return res;

    }
}
