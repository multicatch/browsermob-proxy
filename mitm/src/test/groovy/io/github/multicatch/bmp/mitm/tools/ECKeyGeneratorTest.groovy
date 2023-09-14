package io.github.multicatch.bmp.mitm.tools


import org.junit.Test

import java.security.KeyPair

import static org.junit.Assert.assertNotNull

class ECKeyGeneratorTest {
    @Test
    void testGenerateWithDefaults() {
        io.github.multicatch.bmp.mitm.keys.ECKeyGenerator keyGenerator = new io.github.multicatch.bmp.mitm.keys.ECKeyGenerator()
        KeyPair keyPair = keyGenerator.generate()

        assertNotNull(keyPair)
    }

    @Test
    void testGenerateWithExplicitNamedCurve() {
        io.github.multicatch.bmp.mitm.keys.ECKeyGenerator keyGenerator = new io.github.multicatch.bmp.mitm.keys.ECKeyGenerator("secp384r1")
        KeyPair keyPair = keyGenerator.generate()

        assertNotNull(keyPair)
        // not much else to verify, other than successful generation
    }
}
