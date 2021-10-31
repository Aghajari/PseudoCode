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
package com.aghajari;

import com.aghajari.compiler.DynamicCompilerException;
import com.aghajari.source.Pair;
import com.aghajari.source.Parser;
import com.aghajari.compiler.DynamicCompiler;

import java.util.*;

public class Main {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        try {
            System.getProperties().put("org.apache.commons.logging.simplelog.defaultlog", "fatal");
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("\u001B[36m" + "--------------------------------------------------------" + "\u001B[0m");
        System.out.println("\u001B[01m" + "Hello," + "\u001B[0m");
        System.out.println("\u001B[35m" + "GitHub/Aghajari : https://github.com/Aghajari/PseudoCode" + "\u001B[0m");
        System.out.println("\u001B[36m" + "Enter your PseudoCode here and enter exit at the end...");
        System.out.println("--------------------------------------------------------" + "\u001B[0m");

        List<String> lines = new ArrayList<>();
        while (true) {
            String line = scanner.nextLine();
            if (line.trim().equalsIgnoreCase("exit"))
                break;
            lines.add(line);
        }

/*

 */
        System.out.println("\u001B[33m" + "----------------");
        System.out.println("Wait a moment...");
        System.out.println("----------------" + "\u001B[0m");

        compile(Parser.parse(lines), scanner);
    }

    public static void compile(Parser.Output source, Scanner scanner) {
        try {
            DynamicCompiler<?> compiler = new DynamicCompiler<>();

            String javaCode = "package com.aghajari;\n" +
                    "import java.util.Scanner;\n" +
                    "import static com.aghajari.MainFunctions.*;\n" +
                    "import static java.lang.Math.*;\n" +
                    "import static java.util.Arrays.*;\n";

            for (String imp : source.imports) {
                javaCode += imp + "\n";
            }

            javaCode += "public class PseudoCodeCompiler {\n" +
                    "    Scanner scanner;\n" +
                    "    public void run(Scanner scanner) {\n" +
                    "       this.scanner = scanner;\n" +
                    "       " + source.firstFunctionName + "();\n" +
                    "    }\n\n" +
                    source.source +
                    "}\n";

            source.lineDelta = source.imports.size() + 12;

            Class<?> cls = compiler.compile("com.aghajari", "PseudoCodeCompiler", javaCode, source);

            System.out.println("\u001B[34m" + "----------------");
            System.out.println("Let's start :)");
            System.out.println("----------------" + "\u001B[0m");

            cls.getMethod("run", Scanner.class).invoke(cls.getConstructor().newInstance(), scanner);

            System.out.println("\u001B[32m" + "----------------");
            System.out.println("DONE");
            System.out.println("----------------" + "\u001B[0m");

        } catch (Exception e) {
            System.out.println("\u001B[31m" + "----------------");
            System.out.println("Oops, Something went wrong :(");
            System.out.println("----------------" + "\u001B[0m");
            handleError(source, e);
        }
    }

    private static void handleError(Parser.Output output, Exception e) {
        if (e instanceof DynamicCompilerException) {
            System.err.println(e.toString());
        } else {
            try {
                boolean done = handleError(output, e, e.getStackTrace());
                if (!done)
                    done = handleError(output, e.getCause(), e.getCause().getStackTrace());
                if (!done)
                    e.printStackTrace();
            } catch (Exception ignore) {
                e.printStackTrace();
            }
        }
    }

    private static boolean handleError(Parser.Output output, Throwable throwable, StackTraceElement[] stackTraceElements) {
        for (StackTraceElement stackTraceElement : stackTraceElements) {
            if (stackTraceElement.getClassName().equals("com.aghajari.PseudoCodeCompiler")) {
                if (output.lines.containsKey(stackTraceElement.getLineNumber() - output.lineDelta)) {
                    Pair<Integer, String> pair = output.lines.get(stackTraceElement.getLineNumber() - output.lineDelta);
                    System.err.println(String.format("Error on line %d:\n>  %s\n",
                            pair.a, pair.b));

                    System.err.println(throwable.getMessage());
                    for (StackTraceElement st : stackTraceElements) {
                        if (st.getClassName().equals("com.aghajari.PseudoCodeCompiler")) {
                            break;
                        }
                        System.err.println("\tat " + st);
                    }
                }
                return true;
            }
        }
        return false;
    }

}
