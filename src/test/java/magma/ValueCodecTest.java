package magma;

import static org.junit.jupiter.api.Assertions.*;

import java.math.BigInteger;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;

public class ValueCodecTest {
	@Test
	public void decodeInt() {
		Value v = ValueCodec.fromEncoded("123");
		assertTrue(v instanceof Value.IntVal);
		assertEquals(new BigInteger("123"), ((Value.IntVal) v).value());
		assertEquals("123", ValueCodec.toEncoded(v));
	}

	@Test
	public void decodeBool() {
		Value vt = ValueCodec.fromEncoded("true");
		assertTrue(vt instanceof Value.BoolVal);
		assertTrue(((Value.BoolVal) vt).value());
		assertEquals("true", ValueCodec.toEncoded(vt));
		Value vf = ValueCodec.fromEncoded("false");
		assertFalse(((Value.BoolVal) vf).value());
		assertEquals("false", ValueCodec.toEncoded(vf));
	}

	@Test
	public void decodeArray() {
		Value v = ValueCodec.fromEncoded("@ARR:1|2");
		assertTrue(v instanceof Value.ArrayVal);
		List<Value> els = ((Value.ArrayVal) v).elements();
		assertEquals(2, els.size());
		assertEquals("1", ValueCodec.toEncoded(els.get(0)));
		assertEquals("2", ValueCodec.toEncoded(els.get(1)));
		assertEquals("@ARR:1|2", ValueCodec.toEncoded(v));
	}

	@Test
	public void structFieldStable() {
		Map<String, Value> fields = new LinkedHashMap<>();
		fields.put("field", new Value.IntVal(new BigInteger("100")));
		Value s = new Value.StructVal("Wrapper", fields);
		assertEquals("@STR:Wrapper|field=100", ValueCodec.toEncoded(s));
		Value dec = ValueCodec.fromEncoded("@STR:Wrapper|field=100");
		assertTrue(dec instanceof Value.StructVal);
		assertEquals("Wrapper", ((Value.StructVal) dec).typeName());
		assertEquals("100", ValueCodec.toEncoded(((Value.StructVal) dec).fields().get("field")));
	}

	@Test
	public void refsEncodeDecode() {
		Value rm = new Value.RefVal("x", true);
		assertEquals("@REFMUT:x", ValueCodec.toEncoded(rm));
		Value ri = new Value.RefVal("x", false);
		assertEquals("@REF:x", ValueCodec.toEncoded(ri));
		assertTrue(ValueCodec.fromEncoded("@REF:x") instanceof Value.RefVal);
		assertTrue(ValueCodec.fromEncoded("@REFMUT:x") instanceof Value.RefVal);
	}

	@Test
	public void unitEncodeDecode() {
		assertEquals(Value.UnitVal.INSTANCE, ValueCodec.fromEncoded(""));
		assertEquals("", ValueCodec.toEncoded(Value.UnitVal.INSTANCE));
	}
}
