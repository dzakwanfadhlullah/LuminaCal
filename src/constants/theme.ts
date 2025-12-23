/**
 * Theme Constants for LuminaCal React Native
 * Centralized design tokens for colors, glass effects, and layout.
 */

export const COLORS = {
    // Primary Palette
    primary: {
        orange: '#FFB88C',
        pink: '#FF6B9D',
        gradient: ['#FFB88C', '#FF6B9D'] as const,
    },

    // Macro Colors
    macro: {
        protein: '#60A5FA', // blue-400
        carbs: '#4ADE80',   // green-400
        fat: '#FB923C',     // orange-400
    },

    // Status Colors
    status: {
        success: '#22C55E',
        warning: '#F59E0B',
        error: '#EF4444',
    },

    // Light Mode
    light: {
        background: '#F8FAFC',       // slate-50
        surface: '#FFFFFF',
        text: {
            primary: '#1E293B',        // slate-800
            secondary: '#64748B',      // slate-500
            muted: '#94A3B8',          // slate-400
        },
        glass: {
            background: 'rgba(255, 255, 255, 0.4)',
            border: 'rgba(255, 255, 255, 0.4)',
        },
        blob: {
            purple: 'rgba(168, 85, 247, 0.4)',
            blue: 'rgba(56, 189, 248, 0.4)',
            pink: 'rgba(244, 114, 182, 0.4)',
        },
    },

    // Dark Mode
    dark: {
        background: '#020617',       // slate-950
        surface: '#0F172A',          // slate-900
        text: {
            primary: '#F1F5F9',        // slate-100
            secondary: '#94A3B8',      // slate-400
            muted: '#64748B',          // slate-500
        },
        glass: {
            background: 'rgba(15, 23, 42, 0.6)',
            border: 'rgba(255, 255, 255, 0.1)',
        },
        blob: {
            purple: 'rgba(88, 28, 135, 0.4)',
            blue: 'rgba(30, 58, 138, 0.4)',
            pink: 'rgba(131, 24, 67, 0.4)',
        },
    },
} as const;

export const GLASS_TOKENS = {
    blur: 20,          // Standard blur for glass cards
    borderRadius: 32,  // 2rem in pixels
    borderWidth: 1,
} as const;

export const LAYOUT = {
    maxWidth: 448,     // max-w-md in pixels
    tabBarHeight: 80,
    safeAreaBottom: 20,
    padding: {
        screen: 16,
        card: 24,
    },
} as const;

export const ANIMATION_CONFIG = {
    blob: {
        duration: 7000,
        scale: {
            min: 0.9,
            max: 1.1,
        },
    },
    number: {
        duration: 1000,
    },
    ring: {
        duration: 1000,
    },
    transition: {
        fast: 200,
        normal: 300,
        slow: 500,
    },
} as const;

export const APP_CONFIG = {
    name: 'LuminaCal',
    version: '1.0.0',
    buildNumber: 204,
} as const;
