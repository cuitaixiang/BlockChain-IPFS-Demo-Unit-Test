package io.taucoin;

import com.google.gson.JsonObject;
import io.ipfs.api.IPFS;
import io.ipfs.api.JSONParser;
import io.ipfs.api.MerkleNode;
import io.ipfs.api.Peer;
import io.ipfs.api.cbor.CborObject;
import io.ipfs.cid.Cid;
import io.ipfs.multiaddr.MultiAddress;
import io.ipfs.multihash.Multihash;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Base64;
import java.util.LinkedHashMap;
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

    @Test
    public void testMerkleDAG() throws Exception {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("data", "hello");
//        String original = "{\"data\":1234}";
//        byte[] object = original.getBytes();
        byte[] object = jsonObject.toString().getBytes();
        MerkleNode put = ipfs.dag.put("json", object);

//        Cid expected = Cid.decode("zdpuAs3whHmb9T1NkHSLGF45ykcKrEBxSLiEx6YpLzmKbQLEB");

        Multihash result = put.hash;
        logger.info("{}", result.toString());
//        Assert.assertTrue("Correct cid returned", result.equals(expected));
//
//        byte[] get = ipfs.dag.get(expected);
//        Assert.assertTrue("Raw data equal", original.equals(new String(get).trim()));
    }

    @Test
    public void testdagCbor() throws IOException {
        Map<String, CborObject> tmp = new LinkedHashMap<>();
        tmp.put("version", new CborObject.CborString("1"));
//        tmp.put("option", new CborObject.CborString("2"));
        CborObject original = CborObject.CborMap.build(tmp);
        byte[] object = original.toByteArray();
        MerkleNode put = ipfs.dag.put("cbor", object);

        Cid cid = (Cid) put.hash;
        logger.info("cid:{}", cid.toString());

//        byte[] get = ipfs.dag.get(cid);
//        logger.info("{}", ((Map) JSONParser.parse(new String(get))).get("version"));
    }

    @Test
    public void issue() throws Exception {

        try {
            IPFS ipfs = new IPFS("localhost", 5001);

            // JSON document
            String json = "{\"name\":\"blogpost\",\"documents\":[]}";
            logger.info("json: {}", json);

            // Add a DAG node to IPFS
            MerkleNode merkleNode = ipfs.dag.put("json", json.getBytes());
            logger.info("store [json: {}] - {}", json, merkleNode.toJSON());
            Assert.assertEquals("expected to be zdpuAknRh1Kro2r2xBDKiXyTiwA3Nu5XcmvjRPA1VNjH41NF7" , "zdpuAvQHo4UMMFvGiYJ5yptX4JFZtX77jz457ebwQiToG26TJ", merkleNode.hash.toString());

            // Get a DAG node
            byte[] res = ipfs.dag.get(Cid.buildCidV0(merkleNode.hash));
            logger.info("fetch({}): {}", merkleNode.hash.toString(), new String(res));
            Assert.assertEquals("Should be equals", json, new String(res));

            // Publish to IPNS
            Map result = ipfs.name.publish(merkleNode.hash);
            logger.info("result {}", result);

            // Resolve from IPNS
            String resolved = ipfs.name.resolve(Multihash.fromBase58((String) result.get("Name")));
            logger.info("resolved {}", resolved);
            Assert.assertEquals("Should be equals", resolved, merkleNode.hash.toBase58());

        } catch(Exception e) {
            logger.error("Error", e);
            throw e;
        }
    }

    @Test
    public void dag() throws IOException {
        String original = "{\"data\":1234}";
        byte[] object = original.getBytes();
        MerkleNode put = ipfs.dag.put("json", object);

        Cid expected = Cid.decode("zdpuAs3whHmb9T1NkHSLGF45ykcKrEBxSLiEx6YpLzmKbQLEB");

        Multihash result = put.hash;
        Assert.assertTrue("Correct cid returned", result.equals(expected));

        byte[] get = ipfs.dag.get(expected);
        Assert.assertTrue("Raw data equal", original.equals(new String(get).trim()));
    }

    @Test
    public void dagCbor() throws IOException {
        Map<String, CborObject> tmp = new LinkedHashMap<>();
        String value = "G'day mate!";
        tmp.put("data", new CborObject.CborString(value));
        CborObject original = CborObject.CborMap.build(tmp);
        byte[] object = original.toByteArray();
        MerkleNode put = ipfs.dag.put("cbor", object);

        Cid cid = (Cid) put.hash;

        byte[] get = ipfs.dag.get(cid);
        Assert.assertTrue("Raw data equal", ((Map) JSONParser.parse(new String(get))).get("data").equals(value));

        Cid expected = Cid.decode("zdpuApemz4XMURSCkBr9W5y974MXkSbeDfLeZmiQTPpvkatFF");
        Assert.assertTrue("Correct cid returned", cid.equals(expected));
    }

    @Test
    public void testTransactionDAG() throws Exception {
    }

}
