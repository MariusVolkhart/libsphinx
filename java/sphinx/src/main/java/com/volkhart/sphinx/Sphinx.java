package com.volkhart.sphinx;

import javax.annotation.Nonnull;
import java.security.GeneralSecurityException;

public interface Sphinx {
    /**
     * @param masterPassword A populated array containing the master password.
     * @param blindingFactor The array into which the blinding factor will be written. Must be 32 bytes.
     * @return The challenge.
     * @throws IllegalArgumentException If any of the parameters are not appropriately sized.
     */
    @Nonnull
    Initial challenge(byte[] masterPassword) throws IllegalArgumentException;

    /**
     * @param challenge A 32 byte array with the result of the challenge from the [challenge] function.
     * @param secret    The "secret" contribution from the device. Must be 32 bytes.
     * @return The response.
     * @throws IllegalArgumentException If any of the parameters are not appropriately sized.
     * @throws GeneralSecurityException If responding to the challenge fails.
     */
    @Nonnull
    byte[] respond(byte[] challenge, byte[] secret) throws IllegalArgumentException, GeneralSecurityException;

    /**
     * @param masterPassword A populated array containing the master password.
     * @param blindingFactor The blinding factor from [challenge]. Must be 32 bytes.
     * @param response       The response from [respond]. Must be 32 bytes.
     * @return The derived (binary) password.
     * @throws IllegalArgumentException If any of the parameters are not appropriately sized.
     * @throws GeneralSecurityException If responding to the challenge fails.
     */
    @Nonnull
    byte[] finish(byte[] masterPassword, byte[] blindingFactor, byte[] response) throws IllegalArgumentException, GeneralSecurityException;
}
