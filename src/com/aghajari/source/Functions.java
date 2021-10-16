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

import com.aghajari.functions.*;

import java.util.HashMap;
import java.util.Map;

public class Functions {

    public static String SCANNER_NAME = "scanner";
    private static Map<String, Base> functions = new HashMap<>();

    static {
        functions.put("write", new Print());
        functions.put("print", new Print());
        functions.put("log", new Print());
        functions.put("read", new Read());
        functions.put("scan", new Read());
        functions.put("readw", new ReadW());
        functions.put("scanw", new ReadW());
        functions.put("if", new If());
        functions.put("elseif", new ElseIf());
        functions.put("else", new Else());
        functions.put("while", new While());
        functions.put("for", new For());
        functions.put("return", new Return());
        functions.put("do", new Do());
        functions.put("repeat", new Do());
        functions.put("set", new Set());
        functions.put("subtract", new Subtract());
        functions.put("add", new Add());
    }

    public static Base get(Pair<String, ?> pair) {
        return functions.getOrDefault(pair.a.trim().toLowerCase(), null);
    }
}
