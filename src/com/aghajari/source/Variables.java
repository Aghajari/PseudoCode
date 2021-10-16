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

import java.util.HashMap;

public class Variables {

    public HashMap<String, String> vars = new HashMap<>();

    public String parseFunctionInfo(String var) {

        var = var.trim();
        String newType = "int";

        if (var.contains(":")) {
            String[] a = var.split(":");
            newType = getNewType(a[0].trim());
            var = a[1].trim();
        }
        if (var.endsWith("[]")) {
            var = var.substring(0, var.length() - 2);
            newType += "[]";
        }

        vars.put(var.toLowerCase().trim(), newType.trim());
        return newType + " " + var;
    }

    public Pair<String, Pair<String, String>> newVarIfNotExists(String var, String equals, boolean parse) {
        return newVarIfNotExists(var, equals, parse, true, "int", false);
    }

    public Pair<String, Pair<String, String>> newVarIfNotExists(String var, String equals, boolean parse, boolean importable, String defType, boolean checkArrayAsCount) {
        var = var.trim();
        String newType = defType;
        String count = "0";

        if (!parse && equals.contains("\""))
            newType = "String";

        if (var.contains(":")) {
            String[] a = var.split(":");
            newType = getNewType(a[0].trim());
            var = a[1].trim();
        }

        newType = Reformater.safeReplace(newType, "string", "String");
        newType = Reformater.safeReplace(newType, "int", "int");
        newType = Reformater.safeReplace(newType, "Integer", "int");
        newType = Reformater.safeReplace(newType, "boolean", "boolean");
        newType = Reformater.safeReplace(newType, "long", "long");
        newType = Reformater.safeReplace(newType, "short", "short");
        newType = Reformater.safeReplace(newType, "byte", "byte");
        newType = Reformater.safeReplace(newType, "double", "double");
        newType = Reformater.safeReplace(newType, "float", "float");
        newType = Reformater.safeReplace(newType, "char", "char");
        newType = Reformater.safeReplace(newType, "BigDecimal", "BigDecimal");
        newType = Reformater.safeReplace(newType, "BigInteger", "BigInteger");
        if (newType.trim().equalsIgnoreCase("bool"))
            newType = "boolean";

        if (equals.trim().equals("[]")) {
            count = "1000";
            equals = "new " + newType + "[" + count + "]";
            newType += "[]";

        } else if (equals.trim().startsWith("[") && equals.trim().endsWith("]")) {
            try {
                String e2 = equals.trim().substring(1, equals.trim().length() - 1);
                if (checkArrayAsCount) {
                    count = e2;
                    equals = "new " + newType + "[" + count + "]";
                } else {
                    if (equals.contains(","))
                        count = String.valueOf(equals.split(",").length);
                    else
                        count = "1";
                    equals = "new " + newType + "[] {" + e2 + "}";
                }
            } catch (Exception e) {
                count = "1000";
                equals = "new " + newType + "[1000]";
            }

            newType += "[]";
        } else if (parse) {
            if (equals.contains("scanner.")) {
                equals = parseScanner(newType, equals);
            } else {
                equals = parse(newType, equals);
            }
        }

        if (!newType.contains("[]")) {
            equals = "(" + newType + ") (" + equals.trim() + ")";
        }

        String realName = getRealNameWithOutArray(var);
        if (!importable || vars.containsKey(realName.toLowerCase().trim())) {
            return new Pair<>(var + " = " + equals + ";", new Pair<>(newType, count));
        } else {
            vars.put(realName.toLowerCase().trim(), newType.trim());
            return new Pair<>(newType + " " + var + " = " + equals + ";", new Pair<>(newType, count));
        }
    }

    private String parseScanner(String newType, String equals) {
        newType = newType.trim();
        if (newType.contains("[")) return equals;

        if (newType.equalsIgnoreCase("string"))
            return equals;
        if (newType.equalsIgnoreCase("integer"))
            newType = "int";

        if (newType.equalsIgnoreCase("int")
                || newType.equalsIgnoreCase("long")
                || newType.equalsIgnoreCase("short")
                || newType.equalsIgnoreCase("byte")
                || newType.equalsIgnoreCase("double")
                || newType.equalsIgnoreCase("float")
                || newType.equalsIgnoreCase("bigDecimal")
                || newType.equalsIgnoreCase("bigInteger")
                || newType.equalsIgnoreCase("boolean")) {
            String t = newType.substring(0, 1).toUpperCase() + newType.substring(1).toLowerCase();
            return equals.replaceAll("next\\(\\)", "next" + t + "()").replaceAll("nextLine\\(\\)", "next" + t + "()");
        }

        if (newType.equalsIgnoreCase("char")) {
            return equals + ".charAt(0)";
        }
        return equals;
    }

    private String getNewType(String t) {
        switch (t.toLowerCase().trim()) {
            case "%s":
                return "String";
            case "%i":
            case "%d":
            case "%u":
                return "int";
            case "%ld":
            case "%lu":
            case "%l":
                return "long";
            case "%f":
                return "float";
            case "%lf":
                return "double";
            case "%h":
            case "%uh":
                return "short";
            case "%c":
                return "char";
            case "%b":
                return "boolean";
            default:
                return t;
        }
    }

    private String getRealNameWithOutArray(String var) {
        if (var.contains("["))
            return var.substring(0, var.indexOf("["));
        return var;
    }

    private String parse(String newType, String equals) {
        if (newType.equalsIgnoreCase("int")) {
            equals = "Integer.parseInt(" + equals + ")";
        } else if (newType.equalsIgnoreCase("double")) {
            equals = "Double.parseDouble(" + equals + ")";
        } else if (newType.equalsIgnoreCase("float")) {
            equals = "Float.parseFloat(" + equals + ")";
        } else if (newType.equalsIgnoreCase("long")) {
            equals = "Float.parseLong(" + equals + ")";
        } else if (newType.equalsIgnoreCase("short")) {
            equals = "Short.parseShort(" + equals + ")";
        } else if (newType.equalsIgnoreCase("boolean")) {
            if (equals.equalsIgnoreCase("1"))
                equals = "true";
            else if (equals.equalsIgnoreCase("0"))
                equals = "false";
            else
                equals = "Boolean.parseBoolean(" + equals + ")";
        } else if (newType.equalsIgnoreCase("char")) {
            return equals + ".charAt(0)";
        }
        return equals;
    }
}
