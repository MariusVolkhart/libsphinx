package com.volkhart.sphinx;

import javax.annotation.Nonnull;

public final class Initial {
    @Nonnull
    private final byte[] blindingFactor;
    @Nonnull
    private final byte[] challenge;

    Initial(byte[] challenge, byte[] blindingFactor) {
        this.challenge = challenge;
        this.blindingFactor = blindingFactor;
    }

    @Nonnull
    public byte[] getBlindingFactor() {
        return blindingFactor;
    }

    @Nonnull
    public byte[] getChallenge() {
        return challenge;
    }
}
