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
 * The WHILE construct is used to specify a loop with a test at the top.
 * The beginning and ending of the loop are indicated by two keywords WHILE and END WHILE.
 * The general form is:
 * <br>
 * <pre>{@code
 *      i = 0
 *      WHILE i < 10
 *          i++
 *          print i
 *      (END WHILE)
 * }</pre>
 */
public class While implements Base {

    @Override
    public boolean isStatement() {
        return true;
    }

    @Override
    public String parse(Variables variables, String line) {
        return "while (" + Reformater.fixCondition(line) + ")";
    }
}
