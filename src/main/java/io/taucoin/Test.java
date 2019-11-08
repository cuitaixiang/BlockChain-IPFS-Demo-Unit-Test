package io.taucoin;

import io.ipfs.api.IPFS;
import io.ipfs.cid.Cid;
import io.ipfs.multihash.Multihash;
import io.taucoin.core.AccountInfo;
import io.taucoin.core.Block;
import io.taucoin.core.Transaction;
import io.taucoin.core.VersionedChecksummedBytes;
import io.taucoin.crypto.HashUtil;
import io.taucoin.util.AdvancedDeviceUtils;
import io.taucoin.util.RLP;
import io.taucoin.util.RLPList;
import org.apache.commons.collections4.queue.CircularFifoQueue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spongycastle.util.encoders.Hex;

import java.io.IOException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.stream.Stream;

public class Test {
    public static void main(String[] args) {
        AdvancedDeviceUtils.configureDetailedTracing();
        Logger logger = LoggerFactory.getLogger("test");

        List<Long> list = new ArrayList<>(5);

        ConcurrentLinkedQueue<Object> hashcQueue = new ConcurrentLinkedQueue<>();
        IPFS ipfs = new IPFS("/ip4/127.0.0.1/tcp/5001");
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    logger.info("Start to sub from topic:[idc].");
//                    Thread.sleep(10000);
//                    Stream<Map<String, Object>> ob = ipfs.pubsub.sub("me");
//                    logger.info("------------{}", ob);
                    Cid cid = Cid.decode("QmZBk7rEoQJznXRLT81aM8xjj97nwb3fPY5Hp3qAtenqbw");
                    ipfs.block.get(cid);
//                    ipfs.pubsub.sub("idc", hashcQueue::add, x -> logger.error(x.getMessage(), x));
                    logger.info("000000000000000000000000");
                } /*catch (IOException e) {
                    logger.info("111111111111111111");
                    logger.error(e.getMessage(), e);
                    throw new RuntimeException(e);
                } */catch (Exception e) {
                    logger.info("22222222222222222222");
                    logger.error(e.getMessage(), e);
                    throw new RuntimeException(e);
                }
            }
        });
        try {
            thread.start();
            Thread.sleep(1000);
            logger.info("----------------");
            thread.interrupt();
            Thread.sleep(1000);
            logger.info("Alive:{}", thread.isAlive());
        } catch (Exception e) {
            logger.info("33333333333333333333");
            logger.error(e.getMessage(), e);
        }

//        Block block = new Block(Hex.decode("f916bb80845d0c8d7ff843a0d3af0dd6aaed018e6814b1080a200cb4cbf900fe67f3f3454d0719464ff79346a069fb34142547edf6c9a81cf10fd2ee04eca5aa3432bf93d26b9b9c6b6c1576311b941e9e00b027423d57db71cbdd2b78fd7ecb0de9f180f91657f870808088000000005d0c8cd7942f2c74c10baf92dd1fb682cead105ab919c2e6296482219888000000000000008c1ca0749cb81e313a02b2f618a869b4b6b280b70efe1d44f89be42509788cddca6a0ba05f81322810eb54bc9c6ba0a60ed2d32fe86239c22a665402c93d0722a4385e9ef870808088000000005d0c8cc9942f2c74c10baf92dd1fb682cead105ab919c2e6296482219888000000000000008c1ba0a6acb9191df6fae150f44092184f5bb15d166a3e951701c1062576da4a3d81f5a0239a46f03ace25b9a679d348c74f8e8c75435e5f6470db54062156713beb9d99f870808088000000005d0c8cd1942f2c74c10baf92dd1fb682cead105ab919c2e6296482219888000000000000008c1ca08f9ba8e311dcf48e6ce6c60f017567472d6d7c97e8e52bb98c7758fe2e0d3f99a0688d3cfecf88bcb85a07d389f87313e6e2fa87f18a780dc53041011886b2bc1af870808088000000005d0c8ca8942f2c74c10baf92dd1fb682cead105ab919c2e6296482219888000000000000008c1ba088c10c54920db101e17c03b02b28c796fbfd4e048f390ac8adc70db80f978674a01d7163e63c9865889663d84e2ef276863178bd4a9164da95abdfd9d60311d358f870808088000000005d0c8cbf942f2c74c10baf92dd1fb682cead105ab919c2e6296482219888000000000000008c1ba0517072aec1a279214c0a596e130dfc42faac93ec4e70dec3472805ffc88f850ca04a70c9d44ce94c19e95eb44f7a8751b669c0ecc44dabb748a969ed5c92c8db29f872808088000000005d0c8cba942f2c74c10baf92dd1fb682cead105ab919c2e62982019082219888000000000000008c1ba03b4c7488a5a1cdae5c15b4b273b3ada3340221b87a9da2d7a19f6facb2a5fdc8a02854f6a136a57c7166d4fa5a80e86188e2ded689ca22ecad37cae14ba899b36df870808088000000005d0c8c96942f2c74c10baf92dd1fb682cead105ab919c2e6296482219888000000000000008c1ca08ffe3a5ffd8455139fc0a028f687031040ffe491717a85458086b989899964eea06561733eeda77b1e8354700691c5121eacd9e39644755994cbb8676208020e87f870808088000000005d0c8b5894f0937482166c3e1757894502f0eedc72399c1291648221988800000000000000901ca011f544c14e1f912a72e43737bc44f776047274f125e7fc928370009d2554b2ada01f6141fc7f061ce92d7d641e969fbda577af0b0c07d76995d677f7c297103a49f870808088000000005d0c8c90942f2c74c10baf92dd1fb682cead105ab919c2e6296482219888000000000000008c1ca08cd7b075d322f6194b683a7df2ca95749433cec61af7dc36d1334f2698d406eea03f2f40ab5ddc0ee797d2773efe51165c6dcb60d6b35aa958d4464a7ffe7695f5f870808088000000005d0c8b3e94f0937482166c3e1757894502f0eedc72399c1291648221988800000000000000901ca07b865c4e019b039a3bd6774580cec26b8a098d8b221fd3751d23396be905eafba010e26366e84293c2622bf3d5837e327ca02f0b38b60804a14715ea1454f24d2af870808088000000005d0c8ca1942f2c74c10baf92dd1fb682cead105ab919c2e6296482219888000000000000008c1ba0ccfc70e8cb769ad7979037668647ade9e60b5b339b731ce1bad8a3f9b15b8ef1a06474d1e9c8f6ab8dafccb2cbab32e8503cd36bdffc05f443529da6b8be25dd89f870808088000000005d0c8b6894f0937482166c3e1757894502f0eedc72399c1291648221988800000000000000901ba0f000de08db27b43db8c2fe04e1e293b011ac9c8f655e0f2971463f7f5047c842a022b175eedffea2443f532d2e4e6c5c4e1e701f1252808db33c06d1107e6c212ef870808088000000005d0c8b1f94f0937482166c3e1757894502f0eedc72399c1291648221988800000000000000901ba0e26d5e3cd057beac656403c1edbf5f2fb04e76067b75609c04c918a62bc7377da0543161ccde6a5cdb08fa25bce2f089e8c4437f33808571454d2eb96c8e8b9f72f870808088000000005d0c8c8a942f2c74c10baf92dd1fb682cead105ab919c2e6296482219888000000000000008c1ba0d0809dd1f9b6d4e57deb97a846411cc75cb640f88ed7ad0efe3df938e1ff5748a002ab68944d3df20009d6cf8c4fcef4dc0d8d36d35e3a2ff8fb80cb6dca5b36ecf873808088000000005d0c8c6394641880170a0ddbb105f7305e028b4e8450485316830219088227108800000000000000901ca0deb4b91dba74e6fd0e8959bb0e0be9605db69bacc83dde15a3f9ca7964cdf924a05e326c35b03691efafecc3566fec3aa87d894ab9f00a2204ee3767dc4b726f9df870808088000000005d0c8ad6942f2c74c10baf92dd1fb682cead105ab919c2e6296482219888000000000000008c1ca06f10e321bcadd36f7c49169e2f436e4e03852e8046936f507c3dcb7306850bb6a04c723f4bb5c7948e46f66c427aad162866999f501716ecfbcc10f3ea0866d8b9f870808088000000005d0c8b2f942f2c74c10baf92dd1fb682cead105ab919c2e6296482219888000000000000008c1ca015c8a86501b76b812159e0cea5e06cce1e4a5a117cd9ea62c9b29981d26b5938a02c6898b0ea4ee68549d714554f7ce0059b0cb2df04648a8aef727e9d6eb0ec06f870808088000000005d0c8b7494f0937482166c3e1757894502f0eedc72399c1291648221988800000000000000901ca0ed4ab0c37bd5d0e136423d18b092b9fe1f75464d691fcee72eaabf809973adfda026419e5d1c18039e9c391a6d79babd2b564651d122795bed2dd04c0c1b15c323f870808088000000005d0c8b7b94f0937482166c3e1757894502f0eedc72399c1291648221988800000000000000901ba029f990d35a423e5baf169eaa92c0d91c5f0f4911d5f709c671545da9df93d6a2a00a4106441eaf306967853c1dbf7071db5696fbb0da60a2b067f34c2ff830636ff870808088000000005d0c8b10942f2c74c10baf92dd1fb682cead105ab919c2e6296482219888000000000000008c1ca0e5250d36b77e0547f059ea6f2ac992d666e1ac70ce4b6375ab32410483e987aba027b0c3a57eadaa1006abdd8a6c113c766c0edb24eb196ca8984f13cc7d607888f870808088000000005d0c8b3594f0937482166c3e1757894502f0eedc72399c1291648221988800000000000000901ca0241ff1f78314de65c1c6f557f5da9ac3f1a90484baf03159310fbbb1ecdf1b77a05eec59697282ac29996224e89d5a59ddfc1eff6047e47d796bb88df8c9208cedf870808088000000005d0c8c9b942f2c74c10baf92dd1fb682cead105ab919c2e6296482219888000000000000008c1ba0befe38d3de136c4e9f4e77719a48c502ad7ba2921a96dc8f316d79fdfd1c14e8a0384b4f1284a2c76acbfb30833a8e16ce518a900d2b510fa8ef0cdbca053e2fc6f872808088000000005d0c8c8a94b1599c73b6efe901af8704291381889b95fc47aa8303fa028227108800000000000000901c9fd8b3540f90fd3f22642e50d2db0f509bd69235cbeb773cb14740daba8f3895a0311829041c143303a7c91dde8c2acae192e96db33d46aa7d8559373f08e1aef3f870808088000000005d0c8b24942f2c74c10baf92dd1fb682cead105ab919c2e6296482219888000000000000008c1ba0d339a9a0ac60a4edcf85076b1c0ddb08e0373c154dd1758089e1970ab8a78abda0637f6aec5ef6815db49646355b3de2814a439114f222452e4238436ae979b380f870808088000000005d0c8b2a942f2c74c10baf92dd1fb682cead105ab919c2e6296482219888000000000000008c1ca0605fc34c2265959a197613f731f9b2da5d78ce02f51b180599e2128117b7d3b2a02827d6591d81719c783e803c1b1269270b2f799d0eb801e8e4a155855b320de7f873808088000000005d0c8c32941124b06e3a34b7bf4a1e2280f49394510cbf7bb2833b1a6e8223288800000000000000901ba0eee835f76821a75e3f0e6fe617504484d2b5baf15a323be80435a2f8dfd03ea9a01c8c891b26da243d4309af57ec2cd4226ae349f833d8ed74157613553a2e4ad5f870808088000000005d0c8af4942f2c74c10baf92dd1fb682cead105ab919c2e6296482219888000000000000008c1ba0e2ec840643566d53b6363aee722572f67d2eb5e355eeea7530918c292872a69ba06668a9841351bce0ff20d8dae599e3e29e08f66c3cabafd9765754de5a315b7ef870808088000000005d0c8a8d942f2c74c10baf92dd1fb682cead105ab919c2e6296482219888000000000000008c1ba018b1cf288a8dac62aaaf513d18faf4ebbd02f616c2cc780112b55baa2f6c464ca05946e294e204b14ca1107fb67a90cad42404ae368033c9bc3664eea67a5ab420f870808088000000005d0c8b4894f0937482166c3e1757894502f0eedc72399c1291648221988800000000000000901ca01a444948b60c78d8789a013321dd0c2df828d90e69ff8347ae9e1a3536bd880aa008791544dd8de8d084b915b35b155870cf562924a0161ad0c985a6110c560498f870808088000000005d0c8b34942f2c74c10baf92dd1fb682cead105ab919c2e6296482219888000000000000008c1ba03bbf680ce22757322a31e514807fa16e787875d9ed98977a2e24be1842adfc86a05975ddd461aae5cfe6e83d77d7c6fdcae66e7697e1041f84191e3783ccf3e596f870808088000000005d0c8b18942f2c74c10baf92dd1fb682cead105ab919c2e6296482219888000000000000008c1ca00692c6afb956bcaaa28069246eb6d2a6bc1bfce765262e4d6456148bcdaa1e68a064de9c96491deb749dc6777ecd85064183dfbeb461ab4606c25682e53a41dff2f870808088000000005d0c8ab0942f2c74c10baf92dd1fb682cead105ab919c2e6296482219888000000000000008c1ba0c538cb61720bb6cbae4b28724df299e5e03bf714d85dfe7263f4b59cb548941ea0193f9eb4b527ea2891ace55f1e595708df353af2f1f9da6d06570dd4414b739af870808088000000005d0c8aa3942f2c74c10baf92dd1fb682cead105ab919c2e6296482219888000000000000008c1ca013093f829d2784f18d2d41c20babb7c9063503d623578dbab06d00b01fa8c5eba05d24c9a49892ebcdde149304b20710860ee662393ba9fd357d41717bfb4390cff870808088000000005d0c8acb942f2c74c10baf92dd1fb682cead105ab919c2e6296482219888000000000000008c1ca09c29a4bb9d854512df401cf8d271f003d52b314762f3066573a161303562054aa01f71e540f45feee6028911a795bffde34e1ff8e5cee8eb876f3b457e381c669bf870808088000000005d0c8b01942f2c74c10baf92dd1fb682cead105ab919c2e6296482219888000000000000008c1ba0948d57059c57d19ad5257636f31d9c86d891d93df3f28536e85458f5aad842a1a00b89bf3cd25802787b878962198df88c360896d36c46cbf36345e74aa8766c30f870808088000000005d0c8ade942f2c74c10baf92dd1fb682cead105ab919c2e6296482219888000000000000008c1ca0794ca143ff6a4966087122d0b1bd46e06ea73e50f5410a2f2df964345fb138afa04549eafd80d903449813e5986cc15f8dab111965cbe9df46b5260ed61dfe3393f873808088000000005d0c8ca194b9e503e5ad32fc8918f501d2350a07f8ce3605d6832aa3b18223288800000000000000901ca045d95bf219b855c477dbd39011d35324469ab7aed048139f3d937338708451d6a02d1f8e28110fe66e3ef69d52bbea70f90e56457304c22c9eb6baed820e41d460f870808088000000005d0c8ae4942f2c74c10baf92dd1fb682cead105ab919c2e6296482219888000000000000008c1ca06c8f44a89e9f8ea21da1f400421ec9decddd83848257de6e1ca1bae8c099d738a01aa27a00f32d09157874554efdff32600b9d8d40864fe7f8aba4f9df0c475072f870808088000000005d0c8abf942f2c74c10baf92dd1fb682cead105ab919c2e6296482219888000000000000008c1ca0275d190a5bb7dab63ba27ce987a0fe0f3ed3c3288fde1969add5541f7a32cd5ea074eae2e1c44619c2904cc86c8f60196f29aa698d56492b781857899c880322b5f870808088000000005d0c8ad1942f2c74c10baf92dd1fb682cead105ab919c2e6296482219888000000000000008c1ca0f5e4968937c7609f62317b4c242f8b1b367d0220640134ca06ff4f808f4c578fa0275b4a7ef215eee810336d4bc84f597a478729ec250e913bb696918a21196f87f870808088000000005d0c8aec942f2c74c10baf92dd1fb682cead105ab919c2e6296482219888000000000000008c1ca04be9e4fca5d9762e54a2c3d5f3db76f4bbf7c8ebf1e5110abbd022c0c3150d26a037c9ab320ef479a6d7573b45d232d29ed4767252e9642be08ccd04d7495c18b5f870808088000000005d0c8afa942f2c74c10baf92dd1fb682cead105ab919c2e6296482219888000000000000008c1ca0f14918216c71111dfe92bf11d1de04f5fee49e18c82670db5e72ac842f0c5287a042a3f105eebb4b5a8b52977b940733ab72353c46b2b60dd25ee0890cbac1ccdff873808088000000005d0c8cb79489a903a0705cb5725dda7abf77d45e2da878d50a8307f4048227108800000000000000901ba0bcc836cfb4c61056cbb421c68e078ca7e77d006da8a0269a0402a784cae6d01aa03a9855af71cf1631a77cd750cb5d9a13e099d789962a399305c97a610e305bacf870808088000000005d0c8ab9942f2c74c10baf92dd1fb682cead105ab919c2e6296482219888000000000000008c1ba037f18b89dd61472fbcee9a846669e7c67000f4e475c1ce3e9968464c58a88164a037e5d37da40de5f86db3cee3e3f704f2d86d33e8a8fd3c219a81c24e27d26287f870808088000000005d0c8b6094f0937482166c3e1757894502f0eedc72399c1291648221988800000000000000901ba0829bbefde4ebfa89b6141129f3b0ca9c14423c043aeb5fa593e94897c97e0fc8a01bfeb97800de51f7cf4388da1056f0930a5217987ac071377952c166e0cae054f870808088000000005d0c8a96942f2c74c10baf92dd1fb682cead105ab919c2e6296482219888000000000000008c1ca02a6ade058ac0c7bdfcf18c86662d05b93b407e0fef55822d2fecf01492f08a80a061d6cc74a9fbfd1f1d0028d44021168f5f32969bb6472ae01b47aaeb4dc8a69ff870808088000000005d0c8aab942f2c74c10baf92dd1fb682cead105ab919c2e6296482219888000000000000008c1ca0061afa2b365bc3d6c941daa99e8f2fd3bf4848e96afb9b093dfea489d06cfe75a049306f3f1ba4f0a03a51315a9dd696d1bfc6f6bf4605329a2733e6b604069665f870808088000000005d0c8a9c942f2c74c10baf92dd1fb682cead105ab919c2e6296482219888000000000000008c1ba043a6f9a3dfd8ba260a2060c734014f7f9b83790a90233a30fd2c7651ade5428ca05c53e9613cc1f30ce6fa320134c174befe3ae6aa1208c0585d1d1f0eb7aa4b5ef873808088000000005d0c8c309426bcb05c80f95561f15ed502b94c56e929fea79f830bedf28227108800000000000000901ca00bbe7e390c130b4c3f3c7d31c311896461d3697bb169de7d2586bf515007e961a02c75588ea1b5ac1b7d377a6ecbd54ec70e88ce6872490b9097eeed27a5da6428f870808088000000005d0c8b1e942f2c74c10baf92dd1fb682cead105ab919c2e6296482219888000000000000008c1ca0e79531d6111653a0a8b79ee28fcd823d525f38a745c6a649b9c48ba200c20ea3a061c91bfd3ec15b4ed37f4ec3aa5f907e8261adcfd0d1c2ad1e80b852d0098059"), true);
//
//        System.out.println(Hex.toHexString(block.getHash()));
////        for (Transaction tx : block.getTransactionsList()) {
////            System.out.println(Hex.toHexString(tx.getHash()));
////        }
//        BigInteger integer = new BigInteger("2403600361950");
//        System.out.println(integer.toString(16));


//        BigInteger currentValue = new BigInteger("233100699130820481264908899368");
//        BigInteger targetValue = new BigInteger("369D0369D036978", 16);
//        System.out.println("current value:" + currentValue);
//        System.out.println("target value:" + targetValue);
//        BigInteger value = currentValue;
//        long count = 0;
//        while (value.compareTo(targetValue) == 1) {
//            value = value.subtract(value.divide(BigInteger.valueOf(1875)).
//                    multiply(BigInteger.valueOf(35)).multiply(BigInteger.valueOf(4)));
//            System.out.println(value);
//            count++;
//        }
//        System.out.println("Need block number:" + count);

        {
            AccountInfo accountInfo = new AccountInfo("ce84024f51078302ba5b84a046c297");
            System.out.println("Balance:" + accountInfo.getBalance());
            System.out.println("Power:" + accountInfo.getPower());
            System.out.println("Income:" + accountInfo.getIncome());
        }
//
//
        VersionedChecksummedBytes toEncoedAddress= new VersionedChecksummedBytes("TApnmUzYZp1pBgKyVjWsNWnoKxn921UkPz");
        System.out.println("" + Hex.toHexString(toEncoedAddress.getBytes()));
    }
}