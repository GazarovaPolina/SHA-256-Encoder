import java.nio.charset.StandardCharsets;

public class Main {
    public static void main(String[] args) {
        System.out.println(sha256Encrypt("Hello, world!"));
    }

    private static int rotateRight(int num, int n) {
        int mask = (1 << n) - 1;
        int k = num & mask;
        int leftBits = (k) << (32 - n);
        return (num >>> n) | leftBits;
    }

    public static String sha256Encrypt(String data) {

        int h0 = 0x6A09E667;
        int h1 = 0xBB67AE85;
        int h2 = 0x3C6EF372;
        int h3 = 0xA54FF53A;
        int h4 = 0x510E527F;
        int h5 = 0x9B05688C;
        int h6 = 0x1F83D9AB;
        int h7 = 0x5BE0CD19;

        int[] k = {0x428A2F98, 0x71374491, 0xB5C0FBCF, 0xE9B5DBA5, 0x3956C25B, 0x59F111F1, 0x923F82A4, 0xAB1C5ED5,
                0xD807AA98, 0x12835B01, 0x243185BE, 0x550C7DC3, 0x72BE5D74, 0x80DEB1FE, 0x9BDC06A7, 0xC19BF174,
                0xE49B69C1, 0xEFBE4786, 0x0FC19DC6, 0x240CA1CC, 0x2DE92C6F, 0x4A7484AA, 0x5CB0A9DC, 0x76F988DA,
                0x983E5152, 0xA831C66D, 0xB00327C8, 0xBF597FC7, 0xC6E00BF3, 0xD5A79147, 0x06CA6351, 0x14292967,
                0x27B70A85, 0x2E1B2138, 0x4D2C6DFC, 0x53380D13, 0x650A7354, 0x766A0ABB, 0x81C2C92E, 0x92722C85,
                0xA2BFE8A1, 0xA81A664B, 0xC24B8B70, 0xC76C51A3, 0xD192E819, 0xD6990624, 0xF40E3585, 0x106AA070,
                0x19A4C116, 0x1E376C08, 0x2748774C, 0x34B0BCB5, 0x391C0CB3, 0x4ED8AA4A, 0x5B9CCA4F, 0x682E6FF3,
                0x748F82EE, 0x78A5636F, 0x84C87814, 0x8CC70208, 0x90BEFFFA, 0xA4506CEB, 0xBEF9A3F7, 0xC67178F2};

        byte[] dataBytes = data.getBytes(StandardCharsets.UTF_8);

        int remainder = (dataBytes.length + 8 + 1) % 64;

        int paddingSize = remainder == 0 ? 0 : 64 - remainder;
        byte[] paddedMessage = new byte[dataBytes.length + 1 + paddingSize + 8];

        System.arraycopy(dataBytes, 0, paddedMessage, 0, dataBytes.length);
        paddedMessage[dataBytes.length] = (byte) 0b1000_0000;

        int lengthBuf = dataBytes.length * 8;
        for (int pos = 0; pos < 4; pos++) {
            paddedMessage[paddedMessage.length - 1 - pos] = (byte) (lengthBuf & 0xFF);
            lengthBuf >>>= 8;
        }

        byte[] chunk = new byte[64];

        for (int pos = 0; pos < paddedMessage.length; pos += chunk.length) {
            System.arraycopy(paddedMessage, pos, chunk, 0, chunk.length);

            int[] words = new int[64];

            for (int chunkPos = 0, wordPos = 0; chunkPos < chunk.length; chunkPos += 4, wordPos++) {
                words[wordPos] = (chunk[chunkPos] & 0xFF) << 24 | (chunk[chunkPos + 1] & 0xFF) << 16 |
                        (chunk[chunkPos + 2] & 0xFF) << 8 | chunk[chunkPos + 3] & 0xFF;
            }

            for (int wordPos = 16; wordPos < 64; wordPos++) {

                int s0 = rotateRight(words[wordPos - 15], 7) ^ rotateRight(words[wordPos - 15], 18) ^ (words[wordPos - 15] >>> 3);
                int s1 = rotateRight(words[wordPos - 2], 17) ^ rotateRight(words[wordPos - 2], 19) ^ (words[wordPos - 2] >>> 10);
                words[wordPos] = words[wordPos - 16] + s0 + words[wordPos - 7] + s1;
            }

            int a = h0;
            int b = h1;
            int c = h2;
            int d = h3;
            int e = h4;
            int f = h5;
            int g = h6;
            int h = h7;

            for (int wordPos = 0; wordPos < 64; wordPos++) {
                int s1 = rotateRight(e, 6) ^ rotateRight(e, 11) ^ rotateRight(e, 25);
                int ch = (e & f) ^ ((~e) & g);
                int temp1 = h + s1 + ch + k[wordPos] + words[wordPos];
                int s0 = rotateRight(a, 2) ^ rotateRight(a, 13) ^ rotateRight(a, 22);
                int maj = (a & b) ^ (a & c) ^ (b & c);
                int temp2 = s0 + maj;

                h = g;
                g = f;
                f = e;
                e = d + temp1;
                d = c;
                c = b;
                b = a;
                a = temp1 + temp2;
            }

            h0 = h0 + a;
            h1 = h1 + b;
            h2 = h2 + c;
            h3 = h3 + d;
            h4 = h4 + e;
            h5 = h5 + f;
            h6 = h6 + g;
            h7 = h7 + h;
        }

        int[] hashValues = {h0, h1, h2, h3, h4, h5, h6, h7};

        StringBuilder builder = new StringBuilder();
        for (int value : hashValues) {
            builder.append(Integer.toHexString(value));
        }
        return builder.toString();
    }
}




