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
import com.aghajari.source.Variables;

/**
 * Return on a function based on the function's return type, known as return
 */
public class Return implements Base {

    public String returnType = "";

    @Override
    public boolean isStatement() {
        return false;
    }

    @Override
    public String parse(Variables variables, String line) {
        if (returnType.equalsIgnoreCase("void")) return "return;";
        if (line == null || line.trim().isEmpty()) {
            return returnValue(returnType);
        }
        return "return " + line + ";";
    }

    public static String returnValue(String returnType) {
        if (returnType.equalsIgnoreCase("void"))
            return "return;";

        if (returnType.equalsIgnoreCase("int")
                || returnType.equalsIgnoreCase("integer")
                || returnType.equalsIgnoreCase("float")
                || returnType.equalsIgnoreCase("double")
                || returnType.equalsIgnoreCase("long")
                || returnType.equalsIgnoreCase("short")
                || returnType.equalsIgnoreCase("byte")) {
            return "return 0;";
        } else if (returnType.equalsIgnoreCase("boolean")) {
            return "return false;";
        } else if (returnType.equalsIgnoreCase("char")) {
            return "return '\\0';";
        }

        return "return null;";
    }
}
