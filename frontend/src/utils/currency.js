// Simple currency formatter for Sri Lankan Rupees (LKR)
// Usage: import { formatLKR } from '../utils/currency'; formatLKR(12345.67)
export function formatLKR(amount, options = {}) {
  if (amount === null || amount === undefined || Number.isNaN(amount)) return 'රු 0.00';
  // Allow overriding locale/currency/min/max fraction digits via options
  const {
    locale = 'en-LK',
    currency = 'LKR',
    minimumFractionDigits = 2,
    maximumFractionDigits = 2,
  } = options;

  try {
    return new Intl.NumberFormat(locale, {
      style: 'currency',
      currency,
      minimumFractionDigits,
      maximumFractionDigits,
    }).format(Number(amount));
  } catch (e) {
    // Fallback if Intl not available for some reason
    const n = Number(amount).toFixed(2);
    return `LKR ${n}`;
  }
}
