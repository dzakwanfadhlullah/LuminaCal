/**
 * AppNavigator - Bottom tab navigation with glassmorphism
 */

import React from 'react';
import {
    StyleSheet,
    View,
    Text,
    Pressable,
} from 'react-native';
import Animated, {
    useAnimatedStyle,
    withSpring,
    useSharedValue,
} from 'react-native-reanimated';
import {
    Home,
    PieChart,
    ScanLine,
    Compass,
    User,
} from 'lucide-react-native';
import { TabId } from '../constants/types';
import { COLORS, LAYOUT } from '../constants/theme';

interface TabItem {
    id: TabId;
    icon: React.ComponentType<{ size: number; strokeWidth: number; color: string }>;
    label: string;
    primary?: boolean;
}

const TABS: TabItem[] = [
    { id: 'home', icon: Home, label: 'Home' },
    { id: 'stats', icon: PieChart, label: 'Stats' },
    { id: 'scan', icon: ScanLine, label: 'Scan', primary: true },
    { id: 'explore', icon: Compass, label: 'Explore' },
    { id: 'profile', icon: User, label: 'Profile' },
];

interface AppNavigatorProps {
    activeTab: TabId;
    onTabChange: (tab: TabId) => void;
    onScan: () => void;
    darkMode?: boolean;
}

export const AppNavigator: React.FC<AppNavigatorProps> = ({
    activeTab,
    onTabChange,
    onScan,
    darkMode = false,
}) => {
    const colors = darkMode ? COLORS.dark : COLORS.light;

    return (
        <View style={styles.container}>
            {/* Glass Background */}
            <View
                style={[
                    styles.background,
                    {
                        backgroundColor: darkMode
                            ? 'rgba(15, 23, 42, 0.8)'
                            : 'rgba(255, 255, 255, 0.8)',
                        borderTopColor: darkMode
                            ? 'rgba(255, 255, 255, 0.05)'
                            : 'rgba(255, 255, 255, 0.2)',
                    },
                ]}
            />

            {/* Tab Buttons */}
            <View style={styles.tabsRow}>
                {TABS.map((tab) => {
                    const isActive = activeTab === tab.id;
                    const Icon = tab.icon;

                    if (tab.primary) {
                        return (
                            <View key={tab.id} style={styles.primaryContainer}>
                                <Pressable
                                    onPress={onScan}
                                    style={[
                                        styles.primaryButton,
                                        {
                                            backgroundColor: darkMode ? '#FFFFFF' : '#0F172A',
                                        },
                                    ]}
                                >
                                    <ScanLine
                                        size={24}
                                        strokeWidth={2}
                                        color={darkMode ? '#0F172A' : '#FFFFFF'}
                                    />
                                </Pressable>
                            </View>
                        );
                    }

                    return (
                        <TabButton
                            key={tab.id}
                            icon={Icon}
                            label={tab.label}
                            isActive={isActive}
                            onPress={() => onTabChange(tab.id)}
                            darkMode={darkMode}
                        />
                    );
                })}
            </View>
        </View>
    );
};

interface TabButtonProps {
    icon: React.ComponentType<{ size: number; strokeWidth: number; color: string }>;
    label: string;
    isActive: boolean;
    onPress: () => void;
    darkMode?: boolean;
}

const TabButton: React.FC<TabButtonProps> = ({
    icon: Icon,
    label,
    isActive,
    onPress,
    darkMode = false,
}) => {
    const scale = useSharedValue(1);
    const colors = darkMode ? COLORS.dark : COLORS.light;

    const handlePressIn = () => {
        scale.value = withSpring(0.9, { damping: 15, stiffness: 400 });
    };

    const handlePressOut = () => {
        scale.value = withSpring(1, { damping: 15, stiffness: 400 });
    };

    const animatedStyle = useAnimatedStyle(() => ({
        transform: [{ scale: scale.value }],
    }));

    const activeColor = isActive ? colors.text.primary : colors.text.muted;

    return (
        <Pressable
            onPress={onPress}
            onPressIn={handlePressIn}
            onPressOut={handlePressOut}
            style={styles.tabButton}
        >
            <Animated.View style={[styles.tabContent, animatedStyle]}>
                <View
                    style={[
                        styles.iconContainer,
                        isActive && {
                            backgroundColor: darkMode
                                ? 'rgba(255, 255, 255, 0.1)'
                                : 'rgba(0, 0, 0, 0.05)',
                        },
                    ]}
                >
                    <Icon
                        size={20}
                        strokeWidth={isActive ? 2.5 : 2}
                        color={activeColor}
                    />
                </View>
                {isActive && (
                    <Text style={[styles.tabLabel, { color: activeColor }]}>
                        {label}
                    </Text>
                )}
            </Animated.View>
        </Pressable>
    );
};

const styles = StyleSheet.create({
    container: {
        position: 'absolute',
        bottom: 0,
        left: 0,
        right: 0,
        height: LAYOUT.tabBarHeight,
        zIndex: 30,
    },
    background: {
        ...StyleSheet.absoluteFillObject,
        borderTopWidth: 1,
    },
    tabsRow: {
        flex: 1,
        flexDirection: 'row',
        justifyContent: 'space-between',
        alignItems: 'center',
        paddingHorizontal: 24,
        paddingVertical: 8,
    },
    tabButton: {
        flex: 1,
        alignItems: 'center',
        justifyContent: 'center',
    },
    tabContent: {
        alignItems: 'center',
        gap: 4,
    },
    iconContainer: {
        padding: 6,
        borderRadius: 12,
    },
    tabLabel: {
        fontSize: 10,
        fontWeight: '500',
    },
    primaryContainer: {
        flex: 1,
        alignItems: 'center',
        marginTop: -40,
    },
    primaryButton: {
        width: 64,
        height: 64,
        borderRadius: 32,
        alignItems: 'center',
        justifyContent: 'center',
        shadowColor: '#000',
        shadowOffset: { width: 0, height: 4 },
        shadowOpacity: 0.3,
        shadowRadius: 8,
        elevation: 8,
    },
});

export default AppNavigator;
