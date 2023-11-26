export const isBoolean = (value: unknown): value is boolean => {
  return value === 'true' || value === 'false';
}

export const isFloat = (value: unknown): value is number => {
  return typeof value === 'number' &&
    !Number.isNaN(value) &&
    !Number.isInteger(value);
}
