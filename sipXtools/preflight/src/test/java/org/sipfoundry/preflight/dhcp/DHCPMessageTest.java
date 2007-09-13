/**
 * Copyright (C) 2007 Pingtel Corp., certain elements licensed under a Contributor Agreement.
 * Contributors retain copyright to elements licensed under a Contributor Agreement.
 * Licensed to the User under the LGPL license.
 */
package org.sipfoundry.preflight.dhcp;

import java.net.InetAddress;
import java.net.UnknownHostException;

import org.sipfoundry.preflight.dhcp.DHCPMessage;
import org.sipfoundry.preflight.dhcp.DomainNameOption;
import org.sipfoundry.preflight.dhcp.DomainNameServerOption;
import org.sipfoundry.preflight.dhcp.LeaseTimeOption;
import org.sipfoundry.preflight.dhcp.MessageTypeOption;
import org.sipfoundry.preflight.dhcp.RouterOption;
import org.sipfoundry.preflight.dhcp.ServerIdentifierOption;
import org.sipfoundry.preflight.dhcp.SubnetMaskOption;

import junit.framework.TestCase;

import static org.sipfoundry.preflight.dhcp.HardwareAddressType.*;
import static org.sipfoundry.preflight.dhcp.MessageType.*;
import static org.sipfoundry.preflight.dhcp.MessageTypeOption.Type.*;

/**
 * [Enter descriptive text here]
 * <p>
 * 
 * @author Mardy Marshall
 */
public class DHCPMessageTest extends TestCase {

    static byte[] referencePacket = { 0x02, 0x01, 0x06, 0x00, 0x3b, 0x46, 0x5a, (byte) 0xfc, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
            0x00, 0x00, (byte) 0xc0, (byte) 0xa8, 0x01, 0x67, (byte) 0xc0, (byte) 0xa8, 0x01, 0x01, 0x00, 0x00, 0x00, 0x00, 0x00,
            (byte) 0xd0, 0x1e, (byte) 0xFF, (byte) 0xFF, (byte) 0xFE, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
            0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
            0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
            0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
            0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
            0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
            0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
            0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
            0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
            0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
            0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x63, (byte) 0x82, 0x53, 0x63, 0x35, 0x01,
            0x02, 0x01, 0x04, (byte) 0xff, (byte) 0xff, (byte) 0xff, 0x00, 0x03, 0x04, (byte) 0xc0, (byte) 0xa8, 0x01, 0x01, 0x06,
            0x08, 0x0a, 0x01, 0x01, 0x47, (byte) 0x9b, (byte) 0xd4, (byte) 0xc6, 0x7a, 0x0f, 0x0b, 0x70, 0x69, 0x6e, 0x67, 0x74,
            0x65, 0x6c, 0x2e, 0x63, 0x6f, 0x6d, 0x33, 0x04, 0x00, 0x00, 0x02, 0x58, 0x36, 0x04, (byte) 0xc0, (byte) 0xa8, 0x01,
            0x01, (byte) 0xff };

    public void testMarshal() throws UnknownHostException {
        InetAddress ciaddr = InetAddress.getByName("0.0.0.0");
        InetAddress yiaddr = InetAddress.getByName("192.168.1.103");
        InetAddress siaddr = InetAddress.getByName("192.168.1.1");
        InetAddress giaddr = InetAddress.getByName("0.0.0.0");
        String chaddr = "00:D0:1E:FF:FF:FE";

        DHCPMessage testMessage = new DHCPMessage();

        testMessage.setOp(BOOTREPLY);
        testMessage.setHtype(ETHERNET);
        testMessage.setHlen(6);
        testMessage.setHops(0);
        testMessage.setXid(0x3B465AFC);
        testMessage.setSecs(0);
        testMessage.setBroadcast(false);
        testMessage.setCiaddr(ciaddr);
        testMessage.setYiaddr(yiaddr);
        testMessage.setSiaddr(siaddr);
        testMessage.setGiaddr(giaddr);
        testMessage.setChaddr(chaddr);

        testMessage.addOption(new MessageTypeOption(DHCPOFFER));
        testMessage.addOption(new SubnetMaskOption(InetAddress.getByName("255.255.255.0")));

        RouterOption routerOption = new RouterOption();
        routerOption.addServer(InetAddress.getByName("192.168.1.1"));
        testMessage.addOption(routerOption);

        DomainNameServerOption domainNameServerOption = new DomainNameServerOption();
        domainNameServerOption.addServer(InetAddress.getByName("10.1.1.71"));
        domainNameServerOption.addServer(InetAddress.getByName("155.212.198.122"));
        testMessage.addOption(domainNameServerOption);

        testMessage.addOption(new DomainNameOption("pingtel.com"));
        testMessage.addOption(new LeaseTimeOption(10 * 60));
        testMessage.addOption(new ServerIdentifierOption(InetAddress.getByName("192.168.1.1")));

        byte[] packet = testMessage.marshal();

        for (int x = 0; x < packet.length; x++) {
            if (packet[x] != referencePacket[x]) {
                String failMessage = "packet does not match reference at location: " + x;
                fail(failMessage);
            }
        }

    }

    public void testUnmarshal() throws UnknownHostException {
        DHCPMessage testMessage = new DHCPMessage();

        testMessage.unmarshal(referencePacket);

        byte[] packet = testMessage.marshal();

        for (int x = 0; x < packet.length; x++) {
            if (packet[x] != referencePacket[x]) {
                String failMessage = "packet does not match reference at location: " + x;
                failMessage += "\n" + packet[x] + " " + referencePacket[x];
                fail(failMessage);
            }
        }
    }
}
