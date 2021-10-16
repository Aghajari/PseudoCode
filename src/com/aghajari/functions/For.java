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
import com.aghajari.source.Pair;
import com.aghajari.source.Reformater;
import com.aghajari.source.Variables;

/**
 * This loop is a specialized construct for iterating a specific number of times,
 * often called a "counting" loop.  Two keywords, FOR and END FOR are used.
 * The general forms are:
 * <br>
 * <pre>{@code
 *      FOR i = 0 to 10
 *          print i
 *      (END FOR)
 *
 *      FOR i = 10 to 0 STEP -1
 *          print i
 *      (END FOR)
 *
 *      FOR i in range(0, 10)
 *          print i
 *
 *      SET x to [1, 2, 3]
 *      FOR EACH i on x
 *          print i
 * }</pre>
 */
public class For implements Base {
    @Override
    public boolean isStatement() {
        return true;
    }

    @Override
    public String parse(Variables variables, String line) {
        try {
            line = Reformater.safeReplace(line, " to ", " to ");
            line = Reformater.safeReplace(line, " on ", " on ");
            line = Reformater.safeReplace(line, " in ", " in ");
            line = Reformater.safeReplace(line, " of ", " of ");
            line = Reformater.safeReplace(line, "each ", "each ");

            if (line.contains(" to ")) {
                line = line.trim();
                if (line.startsWith("(")) {
                    line = line.substring(1);
                    if (line.endsWith(")"))
                        line = line.substring(0, line.length() - 1);
                }
                line = line.trim();

                String[] arg = line.split("=");
                arg[0] = arg[0].trim();
                arg[1] = arg[1].trim();

                String[] var;
                while (arg[0].contains("  "))
                    arg[0] = arg[0].replaceAll(" {2}", " ");

                if (arg[0].contains(" ")) {
                    var = arg[0].split(" ");
                    var[0] = var[0].trim();
                    var[1] = var[1].trim();
                } else {
                    var = new String[]{"int", arg[0]};
                }

                Pair<String, Pair<String, String>> pair =
                        variables.newVarIfNotExists(var[1], "", false, true, var[0], false);
                var[0] = pair.b.a.trim();

                arg = arg[1].split("to");
                arg[0] = arg[0].trim();
                arg[1] = arg[1].trim();

                String fromValue = arg[0];
                String toValue = arg[1];
                String condition = "<=";
                String step = var[1] + "++";

                toValue = Reformater.safeReplace(toValue, "step", "step");

                if (toValue.contains("step")) {
                    arg = toValue.split("step");
                    toValue = arg[0].trim();
                    step = var[1] + " = " + var[1] + " " + arg[1].trim();

                    if (step.contains("-") || step.contains("/")) {
                        condition = ">=";
                    }
                }
                return "for (" + var[0] + " " + var[1] + " = " + fromValue + "; " + var[1] + " " + condition + " " + toValue + "; " + step + ")";
            } else if (line.trim().contains("each ") || line.contains(" on ") || line.contains(" of ") || line.contains(" in ") || line.contains(":")) {
                if (line.trim().contains("each ")) {
                    line = line.substring(line.indexOf("each ") + 5).trim();
                }
                String spk = line.contains(" on ") ? " on " : (line.contains(" of ") ? " of " : (line.contains(" in ") ? " in " : ":"));
                String[] arg = line.split(spk);

                String type = variables.vars.getOrDefault(arg[1].toLowerCase().trim(), "int[]");
                if (type.endsWith("[]")) type = type.substring(0, type.length() - 2);

                return "for (" + type + " " + arg[0].trim() + " : " + arg[1].trim() + ")";
            }
        } catch (Exception ignore) {
        }
        return null;
    }
}
