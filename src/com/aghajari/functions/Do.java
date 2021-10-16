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

import com.aghajari.source.Pair;
import com.aghajari.source.Variables;


/**
 * This loop is similar to the WHILE loop except that the test is
 * performed at the bottom of the loop instead of at the top.
 * Two keywords, REPEAT and UNTIL are used. The general form is:
 * <br>
 * <pre>{@code
 *      i = 0
 *      REPEAT
 *          i++
 *          print i
 *      UNTIL i < 10
 * }</pre>
 */
public class Do extends While {

    @Override
    public String parse(Variables variables, String line) {
        return "do";
    }

    public String parseCondition(Variables variables, String line) {
        return super.parse(variables, line);
    }

    public boolean canParseAsCondition(Pair<String, String> pair) {
        return pair.a.trim().equalsIgnoreCase("until")
                || pair.a.trim().equalsIgnoreCase("til")
                || pair.a.trim().equalsIgnoreCase("while");
    }
}
