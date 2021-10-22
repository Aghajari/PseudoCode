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

import java.util.Arrays;

/**
 * All functions that our code can use,
 *
 * <pre>{@code
 *      print "Enter the number of integers"
 *      read n
 *      print "Enter " n " integers"
 *      read x[n]
 *      print "Average = " & SUM(x) / x.length
 * }</pre>
 */
public class MainFunctions {

    public static int gcd(int a, int b) {
        return a == 0 ? b : gcd(b % a, a);
    }

    public static int gcd(int[] x) {
        int result = 0;
        for (int value : x)
            result = gcd(value, result);
        return result;
    }

    public static int[] simplify(int[] x) {
        int sign = 0;
        for (int value : x) {
            if (value > 0) {
                sign = 1;
                break;
            } else if (value < 0) {
                sign = -1;
                break;
            }
        }
        int[] y = x.clone();
        if (sign == 0)
            return y;
        int g = gcd(x) * sign;
        for (int i = 0; i < y.length; i++)
            y[i] /= g;
        return y;
    }

    public static int sign(int n) {
        return Integer.compare(n, 0);
    }

    public static boolean not(boolean a) {
        return !a;
    }

    public static boolean xor(boolean a, boolean b) {
        return a ^ b;
    }

    public static int sum(int[] x) {
        int sum = 0;
        for (int a : x)
            sum += a;
        return sum;
    }

    public static int[] range(int a, int b) {
        int[] v = new int[b - a];
        for (int i = 0; i < v.length; i++) {
            v[i] = a + i;
        }
        return v;
    }

    public static double log(double a, double base) {
        return Math.log(a) / Math.log(base);
    }

    public static int[] range(int b) {
        return range(0, b);
    }

    public static void print(Object o) {
        if (o == null)
            System.out.println("null");
        else if (o.getClass().isArray()) {
            if (o.getClass() == int[].class) {
                print((int[]) o);
            } else if (o.getClass() == String[].class) {
                print((String[]) o);
            } else if (o.getClass() == float[].class) {
                print((float[]) o);
            } else if (o.getClass() == long[].class) {
                print((long[]) o);
            } else if (o.getClass() == short[].class) {
                print((short[]) o);
            } else if (o.getClass() == double[].class) {
                print((double[]) o);
            } else {
                print((Object[]) o);
            }
        } else
            System.out.println(o);
    }

    public static void print(Object... o) {
        System.out.println(Arrays.toString(o));
    }

    public static void print(int... o) {
        System.out.println(Arrays.toString(o));
    }

    public static void print(String... o) {
        System.out.println(Arrays.toString(o));
    }

    public static void print(float... o) {
        System.out.println(Arrays.toString(o));
    }

    public static void print(long... o) {
        System.out.println(Arrays.toString(o));
    }

    public static void print(double... o) {
        System.out.println(Arrays.toString(o));
    }

    public static void print(short... o) {
        System.out.println(Arrays.toString(o));
    }

}
