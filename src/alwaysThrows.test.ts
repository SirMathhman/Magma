import { alwaysThrows } from './alwaysThrows';

describe('alwaysThrows', () => {
    it('should throw an error', () => {
        expect(() => alwaysThrows()).toThrow('This function always throws');
    });
});
