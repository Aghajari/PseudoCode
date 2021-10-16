/*
 * Copyright (C) 2021 - Amir Hossein Aghajari
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */
package com.aghajari.source;

import com.aghajari.functions.Do;
import com.aghajari.functions.ElseIf;
import com.aghajari.functions.Return;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Parser {

    static final String arrow = "\uF0DF";

    public static Output parse(List<String> lines) {
        StringBuilder source = new StringBuilder();
        Variables vars = new Variables();

        ArrayList<Pair<Integer, Base>> statementTab = new ArrayList<>();

        Output o = new Output("starterFunction");
        String functionType = "void";
        source.append(functionType).append(" ").append(o.firstFunctionName).append("() {\n");
        source.append("if (true) {\n");

        boolean isEmpty = true;

        Map<String, String> functions = new HashMap<>();
        for (String l : lines) {
            Pair<String, Integer> linePair = Reformater.reformat(l);
            if (linePair == null || linePair.a == null) continue;
            if (linePair.a.trim().startsWith("import ")) continue;

            Pair<String, String> pair = read(linePair.a);

            if (isFunction(pair.a.trim())) {
                String fn = pair.b.trim();
                if (fn.contains("(")) fn = fn.substring(0, fn.indexOf("(")).trim();
                functions.put(fn, getFunctionName(fn));
            }
        }

        for (int realLineIndex = 0; realLineIndex < lines.size(); realLineIndex++) {
            String l = lines.get(realLineIndex);

            boolean justClosedStatement = false;

            String l2 = l;
            for (Map.Entry<String, String> entry : functions.entrySet()) {
                l2 = Reformater.safeReplace(l2, entry.getKey(), entry.getValue());
            }
            Pair<String, Integer> linePair = Reformater.reformat(l2);
            if (linePair == null || linePair.a == null) continue;

            //System.out.println(linePair.a + " : " + linePair.b);

            String line = linePair.a.trim();
            if (line.startsWith("import ")) {
                if (!line.endsWith(";")) line += ";";
                o.imports.add(line);
                continue;
            }

            if (line.endsWith("{")) {
                line = line.substring(0, line.length() - 1);
            }

            if (statementTab.size() > 0) {
                Pair<Integer, Base> statement = statementTab.get(statementTab.size() - 1);

                if (statement.a == -1) {
                    statementTab.get(statementTab.size() - 1).a = linePair.b;
                } else if (linePair.b < statement.a) {
                    while (statement != null && linePair.b < statement.a && !(statement.b instanceof Do)) {
                        justClosedStatement = true;
                        source.append("}").append("\n");
                        statementTab.remove(statementTab.size() - 1);
                        statement = statementTab.size() == 0 ? null : statementTab.get(statementTab.size() - 1);
                    }
                }

                if (statement != null && statement.b instanceof Do) {
                    String line_p = line;
                    if (line_p.startsWith("}"))
                        line_p = line_p.substring(1).trim();

                    Pair<String, String> pair_p = read(line_p);
                    if (((Do) statement.b).canParseAsCondition(pair_p)) {
                        statementTab.remove(statementTab.size() - 1);
                        source.append("} ").append(((Do) statement.b).parseCondition(vars, pair_p.b)).append(";\n");
                        addLine(o, source, l, realLineIndex);
                        continue;
                    }
                }
            }

            String readerLine = null;

            Pair<String, String> pair = read(line);

            if (isFunction(pair.a.trim())) {
                String fn = pair.b.trim();
                if (fn.contains("(")) fn = fn.substring(0, fn.indexOf("(")).trim();

                if (isEmpty) {
                    if (!pair.b.contains("("))
                        source.append(fn).append("();\n");
                    isEmpty = false;
                }
                endFunction(source, statementTab, functionType);

                vars = new Variables();

                String fnInfo = "()";
                if (pair.b.contains("(")) {
                    String moreInfo = pair.b.substring(pair.b.indexOf("(") + 1).trim();

                    functionType = "int";
                    if (moreInfo.contains(":")) {
                        String[] a = moreInfo.split(":");
                        functionType = a[1].trim();
                        moreInfo = a[0].trim();
                    }
                    moreInfo = moreInfo.substring(0, moreInfo.length() - 1);

                    if (!moreInfo.isEmpty()) {
                        String[] fVars;
                        if (moreInfo.contains(",")) {
                            fVars = moreInfo.split(",");
                        } else {
                            fVars = new String[]{moreInfo};
                        }

                        fnInfo = "(";
                        for (String fVar : fVars) {
                            fnInfo += vars.parseFunctionInfo(fVar) + ", ";
                        }
                        fnInfo = fnInfo.trim();
                        fnInfo = fnInfo.substring(0, fnInfo.length() - 1);
                        fnInfo += ")";
                    }
                }

                if (functionType.equalsIgnoreCase("bool"))
                    functionType = "boolean";
                if (functionType.equalsIgnoreCase("integer"))
                    functionType = "Integer";

                source.append(functionType).append(" ").append(fn).append(fnInfo).append(" {\n");
                addLine(o, source, l, realLineIndex);
                source.append("if (true) {\n");
                continue;
            }

            Base base;

            if ((base = Functions.get(pair)) != null) {
                if (base instanceof Return)
                    ((Return) base).returnType = functionType;

                readerLine = base.parse(vars, pair.b);
            }

            if (readerLine == null && (base = getVariableBase(pair)) != null)
                readerLine = base.parse(vars, pair.b);

            if (base instanceof ElseIf && !justClosedStatement && statementTab.size() > 0) {
                if (statementTab.get(statementTab.size() - 1).a != -2)
                    source.append("}").append("\n");
                statementTab.remove(statementTab.size() - 1);
            }

            if (readerLine == null) {

                String rp = line.trim().replaceAll(" ", "");
                if (rp.startsWith("("))
                    rp = rp.substring(1);
                if (rp.endsWith(")"))
                    rp = rp.substring(0, rp.length() - 1);
                rp = rp.trim();

                if (rp.equalsIgnoreCase("endwhile")
                        || rp.equalsIgnoreCase("endfor")
                        || rp.equalsIgnoreCase("loop"))
                    continue;

                base = new DefBase(pair);
                if (base.isStatement() ||
                        line.trim().equalsIgnoreCase("}") || line.trim().equalsIgnoreCase("{")) {
                    readerLine = line;
                    if (line.trim().equalsIgnoreCase("}") && statementTab.size() > 0)
                        statementTab.remove(statementTab.size() - 1);
                } else {
                    readerLine = line + ";";
                }
            }

            if (!readerLine.isEmpty()) {
                source.append(readerLine).append((base.isStatement() || linePair.a.trim().endsWith("{")) ? " {" : "").append("\n");
                addLine(o, source, l, realLineIndex);

                isEmpty = false;
                if (base.isStatement() || linePair.a.trim().endsWith("{"))
                    statementTab.add(new Pair<>(linePair.a.trim().endsWith("{") ? -2 : -1, base));
            }
        }

        endFunction(source, statementTab, functionType);

        /*System.out.println("------");
        System.out.println(source);
        System.out.println("------");*/

        o.source = source.toString().trim();
        return o;
    }

    private static void addLine(Output out, StringBuilder source, String line, int realIndex) {
        try {
            int lineIndex = source.toString().split("\n").length;
            out.lines.put(lineIndex, new Pair<>(realIndex + 1, line));
        } catch (Exception ignore) {
        }
    }

    private static boolean isFunction(String start) {
        if (start.startsWith("+")
                || start.startsWith("-")
                || start.startsWith("*"))
            start = start.substring(1).trim();

        return start.equalsIgnoreCase("algorithm:")
                || start.equalsIgnoreCase("algorithm")
                || start.equalsIgnoreCase("sub")
                || start.equalsIgnoreCase("method")
                || start.equalsIgnoreCase("function");
    }

    private static void endFunction(StringBuilder source, ArrayList<Pair<Integer, Base>> statementTab, String functionType) {
        while (statementTab.size() > 0) {
            if (statementTab.get(statementTab.size() - 1).a != -2)
                source.append("}").append("\n");
            statementTab.remove(statementTab.size() - 1);
        }

        source.append("}\n");
        source.append(Return.returnValue(functionType)).append("\n");
        source.append("}\n");
    }

    private static String getFunctionName(String name) {
        return name.trim().replaceAll("\\W+", "");
    }

    private static Pair<String, String> read(String line) {
        int pos = 0;
        char ch = line.charAt(pos);
        while ((ch >= 'a' && ch <= 'z') ||
                (ch >= 'A' && ch <= 'Z') ||
                (ch == '_' || ch == ':' || ch == '[' || ch == ']' || ch == '%') ||
                (ch >= '0' && ch <= '9')) {
            pos++;
            ch = pos < line.length() ? line.charAt(pos) : '\0';
        }

        return new Pair<>(line.substring(0, pos), pos < line.length() ? line.substring(pos) : "");
    }

    private static VariableBase getVariableBase(Pair<String, String> pair) {
        String b = pair.b.trim();
        if (b.startsWith(arrow) || b.startsWith("=")) {
            return new VariableBase(pair.a, pair.b.trim().substring(b.startsWith(arrow) ? arrow.length() : 1));
        }
        return null;
    }

    private static class VariableBase implements Base {
        final String line;
        final String var;

        public VariableBase(String var, String line) {
            this.line = line;
            this.var = var;
        }

        @Override
        public boolean isStatement() {
            return false;
        }

        @Override
        public String parse(Variables variables, String oldLine) {
            return variables.newVarIfNotExists(var, line, false).a;
        }
    }

    private static class DefBase implements Base {

        private final boolean statement;

        private DefBase(Pair<String, ?> pair) {
            String fn = pair.a.trim();
            statement = fn.equalsIgnoreCase("for") || fn.equalsIgnoreCase("while");

        }

        @Override
        public boolean isStatement() {
            return statement;
        }

        @Override
        public String parse(Variables variables, String line) {
            return null;
        }
    }

    public static class Output {
        public final String firstFunctionName;
        public final List<String> imports = new ArrayList<>();
        public final HashMap<Integer, Pair<Integer, String>> lines = new HashMap<>();
        public String source;
        public int lineDelta = 0;

        public Output(String firstFunctionName) {
            this.firstFunctionName = firstFunctionName;
        }
    }
}
