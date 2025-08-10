import { alwaysFails } from './alwaysFails';
test('alwaysFails should throw an error', function () {
    expect(function () { return alwaysFails(); }).toThrow('This function always fails');
});
