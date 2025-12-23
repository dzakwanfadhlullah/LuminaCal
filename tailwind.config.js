/** @type {import('tailwindcss').Config} */
module.exports = {
    // NOTE: NativeWind v4 uses 'content' for class scanning
    content: [
        './App.{js,jsx,ts,tsx}',
        './src/**/*.{js,jsx,ts,tsx}',
    ],
    presets: [require('nativewind/preset')],
    theme: {
        extend: {
            colors: {
                // Custom LuminaCal colors
                primary: {
                    orange: '#FFB88C',
                    pink: '#DE6262',
                    blue: '#60A5FA',
                },
                macro: {
                    protein: '#3B82F6',
                    carbs: '#22C55E',
                    fat: '#FB923C',
                },
            },
            borderRadius: {
                '4xl': '2rem',
            },
        },
    },
    plugins: [],
};
