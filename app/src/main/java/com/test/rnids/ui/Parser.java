package com.test.rnids.ui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Parser {

    public static void main(String args[]) {

        String source = "% The data in the Whois database are provided by RNIDS\n" +
                "% for information purposes only and to assist persons in obtaining\n" +
                "% information about or related to a domain name registration record.\n" +
                "% Data in the database are created by registrants and we do not guarantee\n" +
                "% their accuracy. We reserve the right to remove access\n" +
                "% for entities abusing the data, without notice.\n" +
                "% All timestamps are given in Serbian local time.\n" +
                "%\n" +
                "Domain name: blic.rs\n" +
                "Domain status: Active https://www.rnids.rs/en/domain-name-status-codes#Active\n" +
                "Registration date: 26.04.2008 11:26:40\n" +
                "Modification date: 22.03.2021 14:31:13\n" +
                "Expiration date: 26.04.2022 11:26:40\n" +
                "Confirmed: 26.04.2008 11:26:40\n" +
                "Registrar: mCloud doo\n" +
                "\n" +
                "\n" +
                "Registrant: Ringier Axel Springer d.o.o.\n" +
                "Address: Kosovska 10, Beograd, Serbia\n" +
                "Postal Code: 11000\n" +
                "ID Number: 17134990\n" +
                "Tax ID: 100000889\n" +
                "\n" +
                "Administrative contact: Ringier Axel Springer d.o.o.\n" +
                "Address: Kosovska 10, Beograd, Serbia\n" +
                "Postal Code: 11000\n" +
                "ID Number: 17134990\n" +
                "Tax ID:\n" +
                "\n" +
                "Technical contact: Mainstream d.o.o.\n" +
                "Address: Studentski Trg 4, Beograd, Serbia\n" +
                "Postal Code: 11000\n" +
                "ID Number: 20076682\n" +
                "Tax ID:\n" +
                "\n" +
                "\n" +
                "DNS: ns.ha.rs - 91.222.5.5\n" +
                "DNS: bg1.mns-dns.net -\n" +
                "DNS: ns.bg2.ha.rs - 91.222.7.7\n" +
                "DNS: ns.bg3.ha.rs - 87.237.206.34\n" +
                "DNS: us1.mns-dns.com -\n" +
                "\n" +
                "\n" +
                "DNSSEC signed: no\n" +
                "\n" +
                "Whois Timestamp: 25.09.2021 04:34:58";

        String ianaSource = "% IANA WHOIS server\n" +
                "% for more information on IANA, visit http://www.iana.org\n" +
                "% This query returned 1 object\n" +
                "\n" +
                "domain:       RS\n" +
                "\n" +
                "organisation: Serbian National Internet Domain Registry (RNIDS)\n" +
                "address:      Zorza Klemansoa 18a\n" +
                "address:      Belgrade  11008\n" +
                "address:      Serbia\n" +
                "\n" +
                "contact:      administrative\n" +
                "name:         RNIDS Admin Contact\n" +
                "organisation: Serbian National Internet Domain Registry (RNIDS)\n" +
                "address:      Zorza Klemansoa 18a\n" +
                "address:      Belgrade  11008\n" +
                "address:      Serbia\n" +
                "phone:        +381 11 7281281\n" +
                "fax-no:       +381 11 7281282\n" +
                "e-mail:       tld-admin@rnids.rs\n" +
                "\n" +
                "contact:      technical\n" +
                "name:         RNIDS Tech Contact\n" +
                "organisation: Serbian National Internet Domain Registry (RNIDS)\n" +
                "address:      Zorza Klemansoa 18a\n" +
                "address:      Belgrade 11000\n" +
                "address:      Serbia\n" +
                "phone:        +381 11 7281281\n" +
                "fax-no:       +381 11 7281282\n" +
                "e-mail:       tld-tech@rnids.rs\n" +
                "\n" +
                "nserver:      A.NIC.RS 2001:067c:069c:0000:0000:0000:0000:0059 91.199.17.59\n" +
                "nserver:      B.NIC.RS 195.178.32.2 2a00:e90:0:3:0:0:0:3\n" +
                "nserver:      F.NIC.RS 2001:500:14:6032:ad:0:0:1 204.61.216.32\n" +
                "nserver:      G.NIC.RS 147.91.8.6\n" +
                "nserver:      H.NIC.RS 2001:067c:069c:0000:0000:0000:0000:0060 91.199.17.60\n" +
                "nserver:      L.NIC.RS 194.146.106.114 2001:67c:1010:29:0:0:0:53\n" +
                "ds-rdata:     57382 13 2 9aca8316c4ce272097297cf5700e8a66e00ad0c83c4165bfcc90659438dc1794\n" +
                "\n" +
                "whois:        whois.rnids.rs\n" +
                "\n" +
                "status:       ACTIVE\n" +
                "remarks:      Registration information: http://www.rnids.rs\n" +
                "\n" +
                "created:      2007-09-24\n" +
                "changed:      2021-07-20\n" +
                "source:       IANA";

//        List<String> keys = List.of("Technical contact", "Domain name");
//        Map<String, Object> details = parseDetails(keys, source);
//        System.out.println(details);

        List<String> keys = new ArrayList<>();
        keys.add("whois");
        keys.add("organisation");
        Map<String, Object> details = parseDetails(keys, ianaSource);
//        System.out.println(details);
        List<String> list = parseDnsList(source);
        System.out.println(list);
    }

    private static List<String> parseDnsList(String source) {
        List<String> dnsList = new ArrayList<>();
        source = source.substring(422);

        int firstDnsIndex = source.indexOf("DNS: ");
        int lastDnsIndex = source.lastIndexOf("DNSSEC");

        String dnsInfo = source.substring(firstDnsIndex, lastDnsIndex);

        for (String s : dnsInfo.split("DNS:")) {
            if (!s.isEmpty())
                dnsList.add(s.split("-")[0].trim());
        }
        return dnsList;
    }

    public static Map<String, Object> parseDetails(List<String> keys, String source) {

        Map<String, Object> searchResult = new HashMap<>();

        for (String key : keys) {
            int startingIndex = source.indexOf(key);
            int lastIndex = source.indexOf("\n", startingIndex);
            String line = source.substring(startingIndex, lastIndex).trim();
            String value = line.split(":")[1].trim();
            searchResult.put(key, value);
        }
        return searchResult;
    }

}
