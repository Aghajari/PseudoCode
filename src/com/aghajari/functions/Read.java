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
package com.aghajari.functions;

import com.aghajari.source.Base;
import com.aghajari.source.Functions;
import com.aghajari.source.Pair;
import com.aghajari.source.Variables;


/**
 * Gets an input from user, known as read, scan
 * The general form is:
 * <br>
 * <pre>{@code
 *      read id         // id is an integer!
 *      read %s:name    // name is a String!
 *      print "ID: " id " NAME: " name
 * }</pre>
 * <p>
 * Get array by using:
 * <pre>{@code
 *      read x[5]         // x is an array of integers!
 *      print x
 * }</pre>
 * <p>
 * Get inputs by whitespace delimiter
 * <pre>{@code
 *      read id, %s:name
 *      print "ID: " id " NAME: " name
 * }</pre>
 */
public class Read implements Base {

    @Override
    public boolean isStatement() {
        return false;
    }

    @Override
    public String parse(Variables variables, String line) {
        String[] vars;

        if (line.contains(","))
            vars = line.split(",");
        else
            vars = new String[]{line};

        StringBuilder source = new StringBuilder();
        for (String var : vars) {
            if (var == null || var.trim().isEmpty())
                continue;

            if (vars.length == 1 && var.contains("[")) {
                String rv = var.substring(0, var.indexOf("["));
                Pair<String, Pair<String, String>> pair = variables.newVarIfNotExists(rv,
                        var.substring(var.indexOf("[")), false, true, "int", true);

                source.append(pair.a).append("\n");

                if (pair.b.b != null && !pair.b.b.isEmpty() && !pair.b.b.equalsIgnoreCase("0")) {
                    String type = pair.b.a.substring(0, pair.b.a.length() - 2);
                    source.append("for(int _my_local_i = 0; _my_local_i < ").append(pair.b.b).append("; _my_local_i++) {\n\t");
                    source.append(variables.newVarIfNotExists(rv + "[_my_local_i]",
                            Functions.SCANNER_NAME + "." + getScannerMethod(true) + "()", true, false, type, true).a);
                    source.append("\n}\n");
                }
            } else {
                source.append(variables.newVarIfNotExists(var,
                        Functions.SCANNER_NAME + "." +
                                (vars.length == 1 ? getScannerMethod(true) : getScannerMethod(false))
                                + "()", true).a).append("\n");
            }
        }

        return source.toString().trim();
    }

    protected String getScannerMethod(boolean preferNextLine) {
        return preferNextLine ? "nextLine" : "next";
    }
}
