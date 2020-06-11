package com.o0live0o.app.appearance.service;

import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class ByteArrayBuffer {
    private ByteArrayOutputStream output;

    public ByteArrayBuffer() {
        output = new ByteArrayOutputStream();
    }

    public ByteArrayBuffer append(byte b) {
        output.write(b);
        return this;
    }

    public ByteArrayBuffer append(byte[] bytes) {
        try {
            output.write(bytes);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return this;
    }

    public byte[] toByteArray() {
        byte[] out = output.toByteArray();
        //Log.i("Racine", "ByteArrayBuffer " + HexUtil.encodeHexStr(out));
        return out;
    }
}