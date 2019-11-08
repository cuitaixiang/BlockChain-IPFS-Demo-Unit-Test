package io.taucoin;

import io.ipfs.api.IPFS;
import io.ipfs.api.Peer;
import io.ipfs.multiaddr.MultiAddress;
import io.ipfs.multihash.Multihash;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class TestIpfs {
    Logger logger = LoggerFactory.getLogger("test");
    private final MultiAddress localAddress = new MultiAddress("/ip4/127.0.0.1/tcp/5001");
    private final IPFS ipfs = new IPFS(localAddress);

    @Test
    public void testPeer() throws IOException {
        List<Peer> swarmPeers = ipfs.swarm.peers();
        for (Peer peer : swarmPeers) {
            logger.info("Peer: {}, id:{}", peer.toString(), peer.id);
        }
    }

    @Test
    public void testMultiAddress() throws IOException {
        Multihash multihash = Multihash.fromHex("Qme8g49gm3q4Acp7xWBKg3nAa9fxZ1YmyDJdyGgoG6LsXh");
        MultiAddress multiAddress = new MultiAddress(multihash);
//        logger.info("host:{}", multiAddress.getHost());
//        logger.info("tcp:{}", multiAddress.getTCPPort());
    }

    @Test
    public void testPubsubSubNonSync() throws Exception {
        logger.info("----------sub start-------------");
        Stream<Map<String, Object>> sub = ipfs.pubsub.sub("idc");
        logger.info("----------sub end-------------");
        List<Map> results = sub.limit(1).collect(Collectors.toList());
        logger.info("----------1-------------");
        Map msg = results.get(0);
        logger.info("----------2-------------");
//        String from = Base58.encode(Base64.getDecoder().decode(msg.get("from").toString()));
//                    String topicId = msg.get("topicIDs").toString();
//                    String seqno = new BigInteger(Base64.getDecoder().decode(msg.get("seqno").toString())).toString();
        String data = new String(Base64.getDecoder().decode(msg.get("data").toString()));
        logger.info("sub size:{} cid:{}", results.size(), data);
//        Assert.assertTrue( ! results.get(0).equals(Collections.emptyMap()));
    }

    @Test
    public void testBootstrap() throws Exception {
        List<MultiAddress> multiAddressList = ipfs.bootstrap.list();
        for (MultiAddress multiAddress : multiAddressList) {
            logger.info("bootstrap: {}", multiAddress.toString());
        }
    }

}
