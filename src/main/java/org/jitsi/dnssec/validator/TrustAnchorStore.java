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
 * 
 * 
 * This file is based on work under the following copyright and permission
 * notice:
 * 
 *     Copyright (c) 2005 VeriSign. All rights reserved.
 * 
 *     Redistribution and use in source and binary forms, with or without
 *     modification, are permitted provided that the following conditions are
 *     met:
 * 
 *     1. Redistributions of source code must retain the above copyright
 *        notice, this list of conditions and the following disclaimer.
 *     2. Redistributions in binary form must reproduce the above copyright
 *        notice, this list of conditions and the following disclaimer in the
 *        documentation and/or other materials provided with the distribution.
 *     3. The name of the author may not be used to endorse or promote
 *        products derived from this software without specific prior written
 *        permission.
 * 
 *     THIS SOFTWARE IS PROVIDED BY THE AUTHOR ``AS IS'' AND ANY EXPRESS OR
 *     IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 *     WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 *     ARE DISCLAIMED. IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY DIRECT,
 *     INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 *     (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 *     SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION)
 *     HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT,
 *     STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING
 *     IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 *     POSSIBILITY OF SUCH DAMAGE.
 */

package org.jitsi.dnssec.validator;

import java.util.HashMap;
import java.util.Map;

import org.jitsi.dnssec.SRRset;
import org.jitsi.dnssec.SecurityStatus;
import org.xbill.DNS.Name;

/**
 * @author davidb
 * @version $Revision: 286 $
 */
public class TrustAnchorStore {
    private Map<String, SRRset> map;

    public TrustAnchorStore() {
        map = new HashMap<String, SRRset>();
    }

    private String key(Name n, int dclass) {
        return "T" + dclass + "/" + n;
    }

    public void store(SRRset rrset) {
        String k = key(rrset.getName(), rrset.getDClass());
        rrset.setSecurityStatus(SecurityStatus.SECURE);
        map.put(k, rrset);
    }

    private SRRset lookup(String key) {
        return map.get(key);
    }

    public SRRset find(Name n, int dclass) {
        while (n.labels() > 0) {
            String k = key(n, dclass);
            SRRset r = lookup(k);
            if (r != null) {
                return r;
            }

            n = new Name(n, 1);
        }

        return null;
    }

}