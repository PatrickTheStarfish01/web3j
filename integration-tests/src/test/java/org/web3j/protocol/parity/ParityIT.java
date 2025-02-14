/*
 * Copyright 2019 Web3 Labs Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */
package org.web3j.protocol.parity;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import org.web3j.EVMTest;
import org.web3j.NodeType;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.admin.methods.response.NewAccountIdentifier;
import org.web3j.protocol.admin.methods.response.PersonalListAccounts;
import org.web3j.protocol.admin.methods.response.PersonalSign;
import org.web3j.protocol.admin.methods.response.PersonalUnlockAccount;
import org.web3j.protocol.http.HttpService;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/** JSON-RPC 2.0 Integration Tests. */
@Disabled
@EVMTest(type = NodeType.BESU)
public class ParityIT {

    private static String PASSWORD = "1n5ecur3P@55w0rd";
    private Parity parity;

    @BeforeEach
    public void setUp() {
        this.parity = Parity.build(new HttpService());
    }

    @Test
    public void testPersonalListAccounts(Web3j web3j) throws Exception {
        web3j.web3ClientVersion().send();

        PersonalListAccounts personalListAccounts = parity.personalListAccounts().send();
        assertNotNull(personalListAccounts.getAccountIds());
    }

    @Test
    public void testPersonalNewAccount() throws Exception {
        NewAccountIdentifier newAccountIdentifier = createAccount();
        assertFalse(newAccountIdentifier.getAccountId().isEmpty());
    }

    @Test
    public void testPersonalUnlockAccount() throws Exception {
        NewAccountIdentifier newAccountIdentifier = createAccount();
        PersonalUnlockAccount personalUnlockAccount =
                parity.personalUnlockAccount(newAccountIdentifier.getAccountId(), PASSWORD).send();
        assertTrue(personalUnlockAccount.accountUnlocked());
    }

    @Test
    public void testPersonalSign() throws Exception {
        PersonalListAccounts personalListAccounts = parity.personalListAccounts().send();
        assertNotNull(personalListAccounts.getAccountIds());

        PersonalSign personalSign =
                parity.paritySignMessage(
                                "0xdeadbeaf", personalListAccounts.getAccountIds().get(0), "123")
                        .send();
        // address : 0xadfc0262bbed8c1f4bd24a4a763ac616803a8c54
        assertNotNull(personalSign.getSignedMessage());
        // result : 0x80ab45a65bd5acce92eac60b52235a34eee647c8dbef8e62108be90a4ac9a22222f87dd8934f
        // c71545cf2ea1b71d8b62146e6d741ac6ee12fd1d1d740adca9021b
    }

    private NewAccountIdentifier createAccount() throws Exception {
        return parity.personalNewAccount(PASSWORD).send();
    }
}
