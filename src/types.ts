export type IntegerTypeName =
  | 'U8'
  | 'U16'
  | 'U32'
  | 'U64'
  | 'I8'
  | 'I16'
  | 'I32'
  | 'I64';

export interface IntegerType {
  name: IntegerTypeName;
  signed: boolean;
  bits: number;
  cType: string; // corresponding C integer type like uint8_t / int32_t
  min?: bigint;
  max?: bigint;
}

function makeUnsigned(name: IntegerTypeName, bits: number): IntegerType {
  const max = (1n << BigInt(bits)) - 1n;
  return {
    name,
    signed: false,
    bits,
    cType: `uint${bits}_t`,
    min: 0n,
    max,
  };
}

function makeSigned(name: IntegerTypeName, bits: number): IntegerType {
  const half = BigInt(bits - 1);
  const min = -(1n << half);
  const max = (1n << half) - 1n;
  return {
    name,
    signed: true,
    bits,
    cType: `int${bits}_t`,
    min,
    max,
  };
}

export const INTEGER_TYPES: Record<IntegerTypeName, IntegerType> = {
  U8: makeUnsigned('U8', 8),
  U16: makeUnsigned('U16', 16),
  U32: makeUnsigned('U32', 32),
  U64: makeUnsigned('U64', 64),
  I8: makeSigned('I8', 8),
  I16: makeSigned('I16', 16),
  I32: makeSigned('I32', 32),
  I64: makeSigned('I64', 64),
};

export function getIntegerType(name: string): IntegerType | undefined {
  return (INTEGER_TYPES as Record<string, IntegerType>)[name];
}

export function supportedIntegerNames(): string[] {
  return Object.keys(INTEGER_TYPES);
}
