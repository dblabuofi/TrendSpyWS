package xin.ui.cpptutor.obj;

import xin.ui.cpptutor.obj.Pattern.PatternType;


public class Variable {
	public static String substitute(String str, Solution sol, String type, String pre, String post) {
		for (String uVar : sol.getVarMapping().keySet())
			str = str.replaceAll(((type != null) ? type + "\\." : "") + uVar, pre + sol.getVarMapping().get(uVar) + post);
		return str;
	}
}
