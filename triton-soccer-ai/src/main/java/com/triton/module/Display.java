package com.triton.module;

import com.triton.Module;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class Display extends Module {
    public static void main(String[] args) {
        try {
            new Display();
        } catch (IOException | TimeoutException e) {
            e.printStackTrace();
        }
    }

    public Display() throws IOException, TimeoutException {
        super();
    }
}
