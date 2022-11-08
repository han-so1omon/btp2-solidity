/*
 * Copyright 2022 ICON Foundation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package foundation.icon.btp.bmc;

import foundation.icon.btp.lib.BTPAddress;
import foundation.icon.btp.test.BTPIntegrationTest;
import foundation.icon.btp.test.MockBMVIntegrationTest;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class RouteManagementTest implements BMCIntegrationTest {
    static BTPAddress link = BTPIntegrationTest.Faker.btpLink();
    static String dst = BTPIntegrationTest.Faker.btpNetwork();

    static List<BMCManagement.Route> getRoutes() {
        try {
            return bmcManagement.getRoutes().send();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @SuppressWarnings("unchecked")
    static boolean isExistsRoute(String dst, String link) {
        return getRoutes().stream()
                .anyMatch((v) -> v.dst.equals(dst) && v.next.equals(link));
    }

    static boolean isExistsRoute(String dst) {
        return getRoutes().stream()
                .anyMatch((v) -> v.dst.equals(dst));
    }

    static void addRoute(String dst, String link) {
        try {
            bmcManagement.addRoute(dst, link).send();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        assertTrue(isExistsRoute(dst, link));
    }

    static void removeRoute(String dst) {
        try {
            bmcManagement.removeRoute(dst).send();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        assertFalse(isExistsRoute(dst));
    }

    static void clearRoute(String dst) {
        if (isExistsRoute(dst)) {
            System.out.println("clear route dst:" + dst);
            removeRoute(dst);
        }
    }

    @BeforeAll
    static void beforeAll() {
        System.out.println("RouteManagementTest:beforeAll start");
        String mockBMVAddress = MockBMVIntegrationTest.mockBMV.getContractAddress();
        BMVManagementTest.addVerifier(link.net(), mockBMVAddress);
        LinkManagementTest.addLink(link.toString());
        System.out.println("RouteManagementTest:beforeAll end");
    }

    @AfterAll
    static void afterAll() {
        System.out.println("RouteManagementTest:afterAll start");
        LinkManagementTest.clearLink(link.toString());
        BMVManagementTest.clearVerifier(link.net());
        System.out.println("RouteManagementTest:afterAll end");
    }

    @Override
    public void clearIfExists(TestInfo testInfo) {
        clearRoute(dst);
    }

    @Test
    void addRouteShouldSuccess() {
        addRoute(dst, link.net());
    }

    @Test
    void addRouteShouldRevertAlreadyExists() {
        addRoute(dst, link.net());

        AssertBMCException.assertUnknown(() -> addRoute(dst, link.net()));
    }

    @Test
    void addRouteShouldRevertNotExistsLink() {
        AssertBMCException.assertNotExistsLink(
                () -> addRoute(dst, Faker.btpNetwork()));
    }

    @Test
    void removeRouteShouldSuccess() {
        addRoute(dst, link.net());

        removeRoute(dst);
    }

    @Test
    void removeRouteShouldRevertNotExists() {
        AssertBMCException.assertUnknown(() -> removeRoute(dst));
    }

    @Test
    void removeLinkShouldRevertReferred() {
        addRoute(dst, link.net());

        AssertBMCException.assertUnknown(() -> LinkManagementTest.removeLink(link.toString()));
    }
}
