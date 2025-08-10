function alwaysThrow(message) {
  if (message === undefined || message === '') {
    return '';
  }
  throw new Error(message);
}

module.exports = { alwaysThrow };
