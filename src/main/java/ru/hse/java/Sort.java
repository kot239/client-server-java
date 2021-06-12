package ru.hse.java;

public class Sort {
    public static int[] bubbleSort(int[] input) {
        int n = input.length;
        for (int i = 0; i < n - 1; i++) {
            for (int j = 0; j < n - 1 - i; j++) {
                if (input[j] > input[j + 1]) {
                    int t = input[j];
                    input[j] = input[j + 1];
                    input[j + 1] = t;
                }
            }
        }
        return input;
    }
}
