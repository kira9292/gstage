/** @type {import('tailwindcss').Config} */
module.exports = {
  content: [
    "./src/**/*.{html,ts}",  // Pour prendre en compte tous les fichiers .html et .ts
  ],
  theme: {
    extend: {
      colors: {
        'sonatel-orange': '#FF7900',
        'sonatel-black': '#000000',
      },
    },
  },
  plugins: [],
}
