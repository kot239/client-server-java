package ru.hse.java.utils;

public class ClientNumbers {
    private int numberClients = 0;

    public synchronized void incClients() {
        numberClients++;
    }

    public synchronized void decClients() {
        numberClients--;
    }

    public synchronized int getNumber() {
        return numberClients;
    }

    public synchronized void setZero() {
        numberClients = 0;
    }
}
