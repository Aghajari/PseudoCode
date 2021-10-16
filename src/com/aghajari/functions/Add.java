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
 * <pre>{@code
 *      a = 24
 *      ADD 10 to a         // a = a + 10
 * }</pre>
 */
public class Add implements Base {

    @Override
    public boolean isStatement() {
        return false;
    }

    @Override
    public String parse(Variables variables, String line) {
        try {
            String[] a = line.trim().split(" to ");
            return variables.newVarIfNotExists(a[1], a[1] + " + " + a[0], false).a;
        } catch (Exception ignore) {
        }
        return null;
    }
}
