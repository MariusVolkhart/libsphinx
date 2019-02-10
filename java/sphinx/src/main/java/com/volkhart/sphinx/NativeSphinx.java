package com.volkhart.sphinx;

import javax.annotation.Nonnull;
import java.security.GeneralSecurityException;

public final class NativeSphinx implements Sphinx {

    private static final int SPHINX_ARRAY_SIZE = 32;

    public NativeSphinx() {
        NativeLoader.loadLibrary("sphinxJni");
    }

    @Nonnull
    @Override
    public Initial challenge(byte[] masterPassword) throws IllegalArgumentException {
        byte[] blindingFactor = new byte[SPHINX_ARRAY_SIZE];
        byte[] challenge = new byte[SPHINX_ARRAY_SIZE];
        nativeChallenge(masterPassword, blindingFactor, challenge);
        return new Initial(challenge, blindingFactor);
    }

    @Nonnull
    @Override
    public byte[] respond(byte[] challenge, byte[] secret) throws IllegalArgumentException, GeneralSecurityException {
        if (challenge.length != SPHINX_ARRAY_SIZE) {
            throw new IllegalArgumentException("Challenge must be " + SPHINX_ARRAY_SIZE + " bytes, was " + challenge.length);
        }
        if (secret.length != SPHINX_ARRAY_SIZE)
            throw new IllegalArgumentException("Secret must be " + SPHINX_ARRAY_SIZE + " bytes, was " + secret.length);
        byte[] response = new byte[SPHINX_ARRAY_SIZE];
        if (nativeRespond(challenge, secret, response) != 0) {
            throw new GeneralSecurityException("Sphinx: responding to challenge failed");
        }
        return response;
    }

    @Nonnull
    @Override
    public byte[] finish(byte[] masterPassword, byte[] blindingFactor, byte[] response) throws IllegalArgumentException, GeneralSecurityException {
        if (blindingFactor.length != SPHINX_ARRAY_SIZE) {
            throw new IllegalArgumentException("Challenge must be " + SPHINX_ARRAY_SIZE + " bytes, was " + blindingFactor.length);
        }
        if (response.length != SPHINX_ARRAY_SIZE) {
            throw new IllegalArgumentException("Secret must be " + SPHINX_ARRAY_SIZE + " bytes, was " + response.length);
        }
        byte[] rwd = new byte[SPHINX_ARRAY_SIZE];
        nativeFinish(masterPassword, blindingFactor, response, rwd);
        return rwd;
    }

    private native void nativeChallenge(byte[] masterPassword, byte[] blindingFactor, byte[] challenge);

    private native int nativeRespond(byte[] challenge, byte[] secret, byte[] response);

    private native int nativeFinish(byte[] masterPassword, byte[] blindingFactor, byte[] response, byte[] rwd);
}
