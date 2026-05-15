/** @type {import('tailwindcss').Config} */
module.exports = {
  content: [
    "./src/**/*.{html,ts}",
  ],
  theme: {
    extend: {
      colors: {
        habitalis: {
          navy: '#1B263B',
          olive: '#718355',
          cream: '#F7F5F0',
          gold: '#8C7851',
        },
      },
    },
  },
  plugins: [],
}
