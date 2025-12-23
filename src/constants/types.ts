/**
 * Type Definitions for LuminaCal
 * All shared interfaces and types.
 */

export interface HistoryItem {
    id: number;
    name: string;
    time: string;
    calories: number;
    protein: number;
    carbs: number;
    fat: number;
    type: 'breakfast' | 'lunch' | 'dinner' | 'snack';
}

export interface Recipe {
    id: number;
    title: string;
    cals: number;
    time: string;
    image: string; // Emoji or image URL
    tag: string;
}

export interface ScanResult {
    name: string;
    confidence: number;
    calories: number;
    macros: {
        p: number; // Protein
        c: number; // Carbs
        f: number; // Fat
    };
    ingredients: string[];
}

export interface Calories {
    consumed: number;
    target: number;
}

export interface Macros {
    protein: number;
    carbs: number;
    fat: number;
}

export interface MacroGoals {
    protein: number;
    carbs: number;
    fat: number;
}

export type TabId = 'home' | 'stats' | 'scan' | 'explore' | 'profile';

export interface TabItem {
    id: TabId;
    label: string;
    primary?: boolean;
}

export type ButtonVariant = 'primary' | 'secondary' | 'accent' | 'ghost';

export interface AppState {
    screen: TabId;
    darkMode: boolean;
    showCamera: boolean;
    showFoodDetail: boolean;
    calories: Calories;
    macros: Macros;
    history: HistoryItem[];
    loading: boolean;
}
