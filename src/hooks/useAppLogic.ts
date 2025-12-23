/**
 * useAppLogic - Core application state hook
 * Migrated from LuminaCal.jsx main App component.
 */

import { useState, useEffect, useCallback } from 'react';
import {
    HistoryItem,
    Calories,
    Macros,
    TabId,
} from '../constants/types';
import {
    MOCK_HISTORY,
    SCAN_RESULTS,
} from '../constants/data';

interface AppLogicState {
    screen: TabId;
    darkMode: boolean;
    showCamera: boolean;
    showFoodDetail: boolean;
    calories: Calories;
    macros: Macros;
    history: HistoryItem[];
    loading: boolean;
}

interface AppLogicActions {
    setScreen: (screen: TabId) => void;
    setDarkMode: (darkMode: boolean) => void;
    toggleDarkMode: () => void;
    openCamera: () => void;
    closeCamera: () => void;
    openFoodDetail: () => void;
    closeFoodDetail: () => void;
    handleScanComplete: () => void;
    handleAddFood: (calories: number) => void;
}

export interface UseAppLogicReturn extends AppLogicState, AppLogicActions { }

export const useAppLogic = (): UseAppLogicReturn => {
    // Navigation state
    const [screen, setScreen] = useState<TabId>('home');
    const [showCamera, setShowCamera] = useState(false);
    const [showFoodDetail, setShowFoodDetail] = useState(false);

    // Theme state
    const [darkMode, setDarkMode] = useState(false);

    // App data state
    const [calories, setCalories] = useState<Calories>({
        consumed: 840,
        target: 2000,
    });
    const [macros, setMacros] = useState<Macros>({
        protein: 45,
        carbs: 120,
        fat: 35,
    });
    const [history, setHistory] = useState<HistoryItem[]>(MOCK_HISTORY);

    // Loading state
    const [loading, setLoading] = useState(true);

    // Simulate initial loading
    useEffect(() => {
        const timer = setTimeout(() => setLoading(false), 1500);
        return () => clearTimeout(timer);
    }, []);

    // Actions
    const toggleDarkMode = useCallback(() => {
        setDarkMode((prev) => !prev);
    }, []);

    const openCamera = useCallback(() => {
        setShowCamera(true);
    }, []);

    const closeCamera = useCallback(() => {
        setShowCamera(false);
    }, []);

    const openFoodDetail = useCallback(() => {
        setShowFoodDetail(true);
    }, []);

    const closeFoodDetail = useCallback(() => {
        setShowFoodDetail(false);
    }, []);

    const handleScanComplete = useCallback(() => {
        setShowCamera(false);
        setShowFoodDetail(true);
    }, []);

    const handleAddFood = useCallback((cal: number) => {
        // Update calories
        setCalories((prev) => ({
            ...prev,
            consumed: prev.consumed + cal,
        }));

        // Update macros
        setMacros((prev) => ({
            protein: prev.protein + SCAN_RESULTS.macros.p,
            carbs: prev.carbs + SCAN_RESULTS.macros.c,
            fat: prev.fat + SCAN_RESULTS.macros.f,
        }));

        // Add to history
        const newItem: HistoryItem = {
            id: Date.now(),
            name: SCAN_RESULTS.name,
            time: new Date().toLocaleTimeString([], {
                hour: '2-digit',
                minute: '2-digit',
            }),
            calories: cal,
            type: 'lunch',
            protein: SCAN_RESULTS.macros.p,
            carbs: SCAN_RESULTS.macros.c,
            fat: SCAN_RESULTS.macros.f,
        };

        setHistory((prev) => [newItem, ...prev]);

        // Close modal and return to home
        setShowFoodDetail(false);
        setScreen('home');
    }, []);

    return {
        // State
        screen,
        darkMode,
        showCamera,
        showFoodDetail,
        calories,
        macros,
        history,
        loading,

        // Actions
        setScreen,
        setDarkMode,
        toggleDarkMode,
        openCamera,
        closeCamera,
        openFoodDetail,
        closeFoodDetail,
        handleScanComplete,
        handleAddFood,
    };
};

export default useAppLogic;
