import { alwaysFails } from './alwaysFails';

test('alwaysFails should throw an error', () => {
    expect(() => alwaysFails()).toThrow('This function always fails');
});
