/**
 * Mock Data for LuminaCal
 * All static data structures ported directly from the web prototype.
 */

import { HistoryItem, Recipe, ScanResult, MacroGoals } from './types';

/**
 * Initial mock history for the dashboard timeline.
 */
export const MOCK_HISTORY: HistoryItem[] = [
    {
        id: 1,
        name: 'Oatmeal & Berries',
        time: '08:30 AM',
        calories: 320,
        protein: 12,
        carbs: 45,
        fat: 6,
        type: 'breakfast',
    },
    {
        id: 2,
        name: 'Iced Americano',
        time: '10:15 AM',
        calories: 15,
        protein: 0,
        carbs: 3,
        fat: 0,
        type: 'snack',
    },
    {
        id: 3,
        name: 'Grilled Salmon Bowl',
        time: '01:00 PM',
        calories: 540,
        protein: 42,
        carbs: 35,
        fat: 22,
        type: 'lunch',
    },
];

/**
 * Recipe suggestions for the Explore screen.
 */
export const MOCK_RECIPES: Recipe[] = [
    { id: 101, title: 'Avocado Toast Deluxe', cals: 340, time: '10m', image: '🥑', tag: 'Breakfast' },
    { id: 102, title: 'Quinoa Power Salad', cals: 420, time: '20m', image: '🥗', tag: 'Vegan' },
    { id: 103, title: 'Berry Smoothie Bowl', cals: 280, time: '5m', image: '🫐', tag: 'Snack' },
    { id: 104, title: 'Grilled Chicken Pesto', cals: 550, time: '35m', image: '🍗', tag: 'High Protein' },
    { id: 105, title: 'Zucchini Noodles', cals: 180, time: '15m', image: '🥒', tag: 'Keto' },
    { id: 106, title: 'Mango Chia Pudding', cals: 220, time: '1h', image: '🥭', tag: 'Dessert' },
];

/**
 * Default scan result (simulated AI detection).
 */
export const SCAN_RESULTS: ScanResult = {
    name: 'Caesar Salad',
    confidence: 0.98,
    calories: 350,
    macros: { p: 12, c: 18, f: 26 },
    ingredients: ['Romaine Lettuce', 'Grilled Chicken', 'Parmesan', 'Croutons', 'Caesar Dressing'],
};

/**
 * Default macro goals.
 */
export const DEFAULT_MACRO_GOALS: MacroGoals = {
    protein: 150,
    carbs: 200,
    fat: 70,
};

/**
 * Explore screen category filters.
 */
export const EXPLORE_CATEGORIES: string[] = [
    'All',
    'Breakfast',
    'Vegan',
    'Keto',
    'Snacks',
    'High Protein',
];

/**
 * Weekly calorie data for statistics chart.
 */
export const WEEKLY_CALORIE_DATA: number[] = [1800, 2100, 1950, 2400, 1600, 2000, 1850];

/**
 * Day labels for the chart.
 */
export const DAY_LABELS: string[] = ['Mon', 'Tue', 'Wed', 'Thu', 'Fri', 'Sat', 'Sun'];

/**
 * Get emoji icon for meal type.
 */
export const getMealTypeEmoji = (type: HistoryItem['type']): string => {
    const emojiMap: Record<HistoryItem['type'], string> = {
        breakfast: '🍳',
        lunch: '🥗',
        dinner: '🍽️',
        snack: '🍎',
    };
    return emojiMap[type] || '🍽️';
};
