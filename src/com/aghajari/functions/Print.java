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
import com.aghajari.source.Reformater;
import com.aghajari.source.Variables;

/**
 * Prints the output, known as print, log, write
 */
public class Print implements Base {

    @Override
    public boolean isStatement() {
        return false;
    }

    @Override
    public String parse(Variables variables, String line) {
        return "print(" + reformatLine(line) + ");";
    }

    private String reformatLine(String line) {
        if (line == null || line.isEmpty()) return "\"\"";
        line = Reformater.safeReplace(line, "&", " +\"\"+ ");

        StringBuilder newLine = new StringBuilder();
        boolean opened = false;
        for (int i = 0; i < line.length(); i++) {
            char ch = line.charAt(i);
            int plusState = -1;

            if (ch == '\"') {
                plusState = opened ? 0 : (newLine.toString().trim().length() > 0 ? 1 : -1);
                opened = !opened;
            }

            if (plusState == 0) {
                newLine.append(ch);
                newLine.append(" + ");
            } else if (plusState == 1) {
                newLine.append(" + ");
                newLine.append(ch);
            } else {
                newLine.append(ch);
            }
        }

        String out = newLine.toString().trim();
        if (out.endsWith("+"))
            out = out.substring(0, out.length() - 1).trim();

        out = Reformater.safeReplace(out, " ", "");
        String prev = null;
        while (!out.equals(prev) && out.contains("++")) {
            prev = out;
            out = Reformater.safeReplace(out, "\\+{2}", "+");
        }
        return out;
    }

}
