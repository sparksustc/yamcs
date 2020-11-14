package org.yamcs.yarch;

import java.io.IOException;
import java.nio.ByteBuffer;

import org.yamcs.time.Instant;
import org.yamcs.utils.ByteArray;
import org.yamcs.utils.ByteArrayUtils;

public class ColumnSerializerV3 {
    static class ShortColumnSerializer implements ColumnSerializer<Short> {
        static short invertSign(short x) {
            return (short) (x ^ Short.MIN_VALUE);
        }

        @Override
        public Short deserialize(ByteArray byteArray, ColumnDefinition cd) throws IOException {
            return invertSign(byteArray.getShort());
        }

        @Override
        public Short deserialize(ByteBuffer buf, ColumnDefinition cd) {
            return invertSign(buf.getShort());
        }

        @Override
        public void serialize(ByteArray byteArray, Short v) {
            byteArray.addShort(invertSign((Short) v));
        }

        @Override
        public void serialize(ByteBuffer byteBuf, Short v) {
            byteBuf.putShort(invertSign((Short) v));
        }

        @Override
        public byte[] toByteArray(Short v) {
            short s = invertSign(v);
            return new byte[] { (byte) ((s >> 8) & 0xFF), (byte) (s & 0xFF) };
        }

        @Override
        public Short fromByteArray(byte[] b, ColumnDefinition cd) throws IOException {
            return invertSign((short) (((b[0] & 0xFF) << 8) + (b[1] & 0xFF)));
        }
    }

    static class IntegerColumnSerializer implements ColumnSerializer<Integer> {
        static int invertSign(int x) {
            return x ^ Integer.MIN_VALUE;
        }
        
        @Override
        public Integer deserialize(ByteArray byteArray, ColumnDefinition cd) throws IOException {
            return invertSign(byteArray.getInt());
        }

        @Override
        public void serialize(ByteArray byteArray, Integer v) {
            byteArray.addInt(invertSign(v));
        }

        @Override
        public Integer deserialize(ByteBuffer byteBuf, ColumnDefinition cd) {
            return invertSign(byteBuf.getInt());
        }

        @Override
        public void serialize(ByteBuffer byteBuf, Integer v) {
            byteBuf.putInt(invertSign(v));
        }
    }

    
    static class LongColumnSerializer implements ColumnSerializer<Long> {
        static long invertSign(long x) {
            return x ^ Long.MIN_VALUE;
        }
        

        @Override
        public Long deserialize(ByteArray byteArray, ColumnDefinition cd) throws IOException {
            return invertSign(byteArray.getLong());
        }

        @Override
        public Long deserialize(ByteBuffer byteBuf, ColumnDefinition cd) {
            return invertSign(byteBuf.getLong());
        }

        @Override
        public void serialize(ByteArray byteArray, Long v) {
            byteArray.addLong(invertSign(v));
        }

        @Override
        public void serialize(ByteBuffer byteBuf, Long v) {
            byteBuf.putLong(invertSign(v));
        }
    }

    static class DoubleColumnSerializer implements ColumnSerializer<Double> {
        
        static long doubleToLong(double x) {
            long v = Double.doubleToLongBits(x);
            
            //for negative values, flips all the bits
            //for positive values, flips only the sign=first bit
            v ^= (v >> 63) | Long.MIN_VALUE;
            return v;
        }

        static double longToDouble(long x) {
            x ^= (~x >> 63) | Long.MIN_VALUE;
            return Double.longBitsToDouble(x);
        }

        @Override
        public Double deserialize(ByteArray byteArray, ColumnDefinition cd) throws IOException {
            return longToDouble(byteArray.getLong());
        }

        @Override
        public Double deserialize(ByteBuffer byteBuf, ColumnDefinition cd) {
            return longToDouble(byteBuf.getLong());
        }

        @Override
        public void serialize(ByteArray byteArray, Double v) {
            byteArray.addLong(doubleToLong(v));
        }

        @Override
        public void serialize(ByteBuffer byteBuf, Double v) {
            byteBuf.putLong(doubleToLong(v));
        }
    }

    
    static class HresTimestampColumnSerializer implements ColumnSerializer<Instant> {
        static long invertSign(long x) {
            return x ^ Long.MIN_VALUE;
        }
        
        @Override
        public Instant deserialize(ByteArray byteArray, ColumnDefinition cd) throws IOException {
            long millis = invertSign(byteArray.getLong());
            int picos = byteArray.getInt();
            return Instant.get(millis, picos);
        }

        @Override
        public Instant deserialize(ByteBuffer byteBuf, ColumnDefinition cd) {
            long millis = invertSign(byteBuf.getLong());
            int picos = byteBuf.getInt();
            return Instant.get(millis, picos);
        }

        @Override
        public void serialize(ByteArray byteArray, Instant v) {
            byteArray.addLong(invertSign(v.getMillis()));
            byteArray.addInt(v.getPicos());
        }

        @Override
        public void serialize(ByteBuffer byteBuf, Instant v) {
            byteBuf.putLong(invertSign(v.getMillis()));
            byteBuf.putInt(v.getPicos());
        }

        @Override
        public byte[] toByteArray(Instant v) {
            byte[] b = new byte[12];
            ByteArrayUtils.encodeLong(invertSign(v.getMillis()), b, 0);
            ByteArrayUtils.encodeInt(v.getPicos(), b, 8);
            return b;
        }

        @Override
        public Instant fromByteArray(byte[] b, ColumnDefinition cd) throws IOException {
            long millis = invertSign(ByteArrayUtils.decodeLong(b, 0));
            int picos = ByteArrayUtils.decodeInt(b, 8);
            return Instant.get(millis, picos);
        }
    }

}
