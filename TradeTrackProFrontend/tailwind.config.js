/** @type {import('tailwindcss').Config} */
module.exports = {
  content: [
    "./src/**/*.{html,ts}",
  ],
  theme: {
    extend: {
      colors: {
        'trade-main': '#0B1221',
        'trade-card': '#14213D',
        'trade-card-light': '#1B2436',
        'trade-accent': '#2563EB',
        'trade-success': '#10B981',
        'trade-danger': '#EF4444',
        'trade-text': '#A7B0C0',
      }
    },
  },
  plugins: [],
}

