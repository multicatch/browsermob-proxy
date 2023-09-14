package io.github.multicatch.bmp.mitm.tools


import org.junit.Test

import java.security.KeyPair

import static org.junit.Assert.assertNotNull

class RSAKeyGeneratorTest {
    @Test
    void testGenerateWithDefaults() {
        io.github.multicatch.bmp.mitm.keys.RSAKeyGenerator keyGenerator = new io.github.multicatch.bmp.mitm.keys.RSAKeyGenerator()
        KeyPair keyPair = keyGenerator.generate()

        assertNotNull(keyPair)
    }

    @Test
    void testGenerateWithExplicitKeySize() {
        io.github.multicatch.bmp.mitm.keys.RSAKeyGenerator keyGenerator = new io.github.multicatch.bmp.mitm.keys.RSAKeyGenerator(1024)
        KeyPair keyPair = keyGenerator.generate()

        assertNotNull(keyPair)
        // not much else to verify, other than successful generation
    }
}
