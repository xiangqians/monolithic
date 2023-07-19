/*
 * Copyright 2020-2023 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.xiangqian.monolithic.util;

import com.nimbusds.jose.jwk.Curve;
import com.nimbusds.jose.jwk.ECKey;
import com.nimbusds.jose.jwk.OctetSequenceKey;
import com.nimbusds.jose.jwk.RSAKey;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import java.math.BigInteger;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.interfaces.ECPrivateKey;
import java.security.interfaces.ECPublicKey;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.ECFieldFp;
import java.security.spec.ECParameterSpec;
import java.security.spec.ECPoint;
import java.security.spec.EllipticCurve;
import java.util.UUID;

/**
 * @author xiangqian revised in 21:23 2023/07/19
 * @author Joe Grandja
 * @since 1.1
 */
public class KeyUtil {

    /**
     * 生成 RSA KEY
     *
     * @return
     */
    public static RSAKey genRsaKey() {
        try {
            // KeyPair
            KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
            keyPairGenerator.initialize(2048);
            KeyPair keyPair = keyPairGenerator.generateKeyPair();

            // RSAKey
            RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();
            RSAPrivateKey privateKey = (RSAPrivateKey) keyPair.getPrivate();
            return new RSAKey.Builder(publicKey)
                    .privateKey(privateKey)
                    .keyID(UUID.randomUUID().toString())
                    .build();
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    /**
     * 生成 EC KEY
     *
     * @return
     */
    public static ECKey genEcKey() {
        try {
            // KeyPair
            EllipticCurve ellipticCurve = new EllipticCurve(
                    new ECFieldFp(new BigInteger("115792089210356248762697446949407573530086143415290314195533631308867097853951")),
                    new BigInteger("115792089210356248762697446949407573530086143415290314195533631308867097853948"),
                    new BigInteger("41058363725152142129326129780047268409114441015993725554835256314039467401291"));
            ECPoint ecPoint = new ECPoint(new BigInteger("48439561293906451759052585252797914202762949526041747995844080717082404635286"),
                    new BigInteger("36134250956749795798585127919587881956611106672985015071877198253568414405109"));
            ECParameterSpec ecParameterSpec = new ECParameterSpec(ellipticCurve,
                    ecPoint,
                    new BigInteger("115792089210356248762697446949407573529996955224135760342422259061068512044369"),
                    1);
            KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("EC");
            keyPairGenerator.initialize(ecParameterSpec);
            KeyPair keyPair = keyPairGenerator.generateKeyPair();

            // ECKey
            ECPublicKey publicKey = (ECPublicKey) keyPair.getPublic();
            ECPrivateKey privateKey = (ECPrivateKey) keyPair.getPrivate();
            Curve curve = Curve.forECParameterSpec(publicKey.getParams());
            return new ECKey.Builder(curve, publicKey)
                    .privateKey(privateKey)
                    .keyID(UUID.randomUUID().toString())
                    .build();
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    /**
     * 生成 OctetSequence KEY
     *
     * @return
     */
    public static OctetSequenceKey genOctetSequenceKey() {
        try {
            // SecretKey
            SecretKey secretKey = KeyGenerator.getInstance("HmacSha256").generateKey();

            // OctetSequenceKey
            return new OctetSequenceKey.Builder(secretKey)
                    .keyID(UUID.randomUUID().toString())
                    .build();
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

}
