import { INTEGER_TYPES, getIntegerType, supportedIntegerNames } from '../src/types';

test('supported integer names contains U8..U64 and I8..I64', () => {
  const names = supportedIntegerNames().sort();
  expect(names).toEqual(Object.keys(INTEGER_TYPES).sort());
});

test('each integer type has correct properties', () => {
  for (const name of Object.keys(INTEGER_TYPES)) {
    const t = getIntegerType(name);
    expect(t).toBeDefined();
    if (!t) continue;

    // bits should match name
    const bits = parseInt(name.replace(/[^0-9]/g, ''), 10);
    expect(t.bits).toBe(bits);

    // signedness and cType prefix
    if (name.startsWith('U')) {
      expect(t.signed).toBe(false);
      expect(t.cType).toBe(`uint${bits}_t`);
      expect(t.min).toBe(0n);
      expect(t.max).toBe((1n << BigInt(bits)) - 1n);
    } else {
      expect(t.signed).toBe(true);
      expect(t.cType).toBe(`int${bits}_t`);
      const half = BigInt(bits - 1);
      expect(t.min).toBe(-(1n << half));
      expect(t.max).toBe((1n << half) - 1n);
    }
  }
});
