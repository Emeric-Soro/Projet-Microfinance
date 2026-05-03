/** @type {import('tailwindcss').Config} */
module.exports = {
    content: [
        './assets/**/*.{js,jsx,ts,tsx}',
        './templates/**/*.html.twig',
        './src/**/*.php',
    ],
    theme: {
        extend: {
            colors: {
                primary: {
                    50: '#e6f0f2',
                    100: '#b3d1d8',
                    200: '#80b2be',
                    300: '#4d93a4',
                    400: '#267b91',
                    500: '#004D61',
                    600: '#004557',
                    700: '#003b4b',
                    800: '#00313f',
                    900: '#00202a',
                },
                surface: {
                    DEFAULT: '#F0F4F5',
                    white: '#FFFFFF',
                },
                'on-surface': '#1F2937',
                'on-surface-variant': '#6B7280',
                'teal-border': '#C0D4D8',
                zebra: '#F8FAFA',
            },
            fontFamily: {
                heading: ['Manrope', 'sans-serif'],
                body: ['Inter', 'sans-serif'],
                mono: ['JetBrains Mono', 'monospace'],
            },
            boxShadow: {
                card: '0 1px 3px rgba(0, 77, 97, 0.04)',
                'card-hover': '0 4px 12px rgba(0, 77, 97, 0.08)',
                dropdown: '0 4px 16px rgba(0, 77, 97, 0.08)',
                modal: '0 8px 32px rgba(0, 77, 97, 0.12)',
            },
            borderRadius: {
                DEFAULT: '0.25rem',
                lg: '0.5rem',
                xl: '0.75rem',
            },
            maxWidth: {
                'app': '1440px',
            },
            spacing: {
                '4': '1rem',
                '6': '1.5rem',
                'sidebar': '256px',
                'topbar': '64px',
            },
        },
    },
    plugins: [],
};
