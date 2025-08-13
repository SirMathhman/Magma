module.exports = function alwaysThrows(input) {
  if (typeof input === 'string' && input.length === 0) {
    return "Input was empty.";
  }
  throw new Error("This function always throws an error unless the input is an empty string.");
};
