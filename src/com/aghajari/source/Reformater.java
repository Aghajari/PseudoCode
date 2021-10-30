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

import com.aghajari.MainFunctions;

import java.lang.reflect.Method;

public class Reformater {

    public static Pair<String, Integer> reformat(String line) {
        if (line == null || line.trim().isEmpty())
            return null;

        int tabs = numberOfTabs(line);
        line = line.trim();
        line = removeLineIndex(line);
        line = removeSpecialCharacters(line);
        line = safeReplace(line, "mod", "%");
        line = safeReplace(line, ":=", "=");
        line = safeReplace(line, "≠", "!=");
        line = safeReplace(line, "≤", "<=");
        line = safeReplace(line, "≥", ">=");
        line = safeReplace(line, "×", "*");
        line = safeReplace(line, "else if", "elseif");

        Method[] methods = MainFunctions.class.getMethods();
        for (Method method : methods) {
            line = safeReplace(line, method.getName(), method.getName());
        }

        methods = Math.class.getMethods();
        for (Method method : methods) {
            line = safeReplace(line, method.getName(), method.getName());
        }

        return new Pair<>(line.trim(), tabs);
    }

    private static int numberOfTabs(String line) {
        int count = 0;
        int index = 0;
        char ch;
        while ((ch = line.charAt(index++)) == '\t' || ch == ' ') {
            count++;
        }
        return count;
    }

    private static String removeLineIndex(String line) {
        while (Character.isDigit(line.charAt(0)) || line.charAt(0) == '-') {
            line = line.substring(1);
        }
        return line.trim();
    }

    private static String removeSpecialCharacters(String line) {
        if (line.endsWith(";"))
            line = line.substring(0, line.length() - 1);

        line = line.replaceAll("–", "-");
        return line.replaceAll("”", "\"").replaceAll("“", "\"");
    }

    public static String safeReplace(String line, String a, String b) {
        return line.replaceAll("(?i)(?=(([^\\\"]*\\\"){2})*[^\\\"]*$)" + a, b);
    }

    public static String fixCondition(String line) {
        line = safeReplace(line, "=", "==");
        line = safeReplace(line, "====", "==");
        line = safeReplace(line, "===", "==");
        line = safeReplace(line, "!==", "!=");
        line = safeReplace(line, ">==", ">=");
        line = safeReplace(line, "<==", "<=");
        line = safeReplace(line, " or ", "||");
        line = safeReplace(line, " and ", "&&");
        return line.trim();
    }
}
