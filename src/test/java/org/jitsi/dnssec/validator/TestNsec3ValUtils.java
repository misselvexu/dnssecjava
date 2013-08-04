/*
 * dnssecjava - a DNSSEC validating stub resolver for Java
 * Copyright (C) 2013 Ingo Bauersachs. All rights reserved.
 *
 * This file is part of dnssecjava.
 *
 * Dnssecjava is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * Dnssecjava is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with dnssecjava.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.jitsi.dnssec.validator;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import java.io.IOException;
import java.util.Properties;

import org.jitsi.dnssec.TestBase;
import org.junit.Test;
import org.xbill.DNS.Flags;
import org.xbill.DNS.Message;
import org.xbill.DNS.Rcode;

public class TestNsec3ValUtils extends TestBase {
    @Test(expected = IllegalArgumentException.class)
    public void testTooLargeIterationCountMustThrow() {
        Properties config = new Properties();
        config.put("org.jitsi.dnssec.nsec3.iterations.512", Integer.MAX_VALUE);
        NSEC3ValUtils val = new NSEC3ValUtils();
        val.init(config);
    }

    @Test
    public void testNsec3WithoutClosestEncloser() throws IOException {
        Message m = resolver.send(createMessage("gibtsnicht.gibtsnicht.nsec3.ingotronic.ch./A"));
        Message message = messageFromString(m.toString().replaceAll("((UDUMPS9J6F8348HFHH2FAED6I9DDE0U6)|(NTV3QJT4VQDVBPB6BNOVM40NMKJ3H29P))\\.nsec3.*", ""));
        add("gibtsnicht.gibtsnicht.nsec3.ingotronic.ch./A", message);

        Message response = resolver.send(createMessage("gibtsnicht.gibtsnicht.nsec3.ingotronic.ch./A"));
        assertFalse("AD flag must not be set", response.getHeader().getFlag(Flags.AD));
        assertEquals(Rcode.SERVFAIL, response.getRcode());
        assertEquals("failed.nxdomain.nsec3_bogus", getReason(response));
    }

    @Test
    public void testNsec3NodataChangedToNxdomainIsBogus() throws IOException {
        Message m = resolver.send(createMessage("a.b.nsec3.ingotronic.ch./MX"));
        Message message = messageFromString(m.toString().replaceAll("status: NOERROR", "status: NXDOMAIN"));
        add("a.b.nsec3.ingotronic.ch./A", message);

        Message response = resolver.send(createMessage("a.b.nsec3.ingotronic.ch./A"));
        assertFalse("AD flag must not be set", response.getHeader().getFlag(Flags.AD));
        assertEquals(Rcode.SERVFAIL, response.getRcode());
        assertEquals("failed.nxdomain.nsec3_bogus", getReason(response));
    }
}
