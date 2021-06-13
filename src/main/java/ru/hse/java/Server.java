package ru.hse.java;

import java.io.IOException;

public abstract class Server {

    public abstract void run() throws IOException;

    public abstract void close() throws IOException;
}
