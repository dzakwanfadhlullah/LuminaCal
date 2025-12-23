/**
 * MacroProgressBar - Simple progress bar for macros
 * Displays protein/carbs/fat progress.
 */

import React from 'react';
import { StyleSheet, View, Text } from 'react-native';
import Animated, {
    useSharedValue,
    useAnimatedStyle,
    withTiming,
    Easing,
} from 'react-native-reanimated';

interface MacroProgressBarProps {
    label: string;
    value: number;
    max: number;
    color: string;
    darkMode?: boolean;
}

export const MacroProgressBar: React.FC<MacroProgressBarProps> = ({
    label,
    value,
    max,
    color,
    darkMode = false,
}) => {
    const progress = useSharedValue(0);
    const percentage = Math.min((value / max) * 100, 100);

    React.useEffect(() => {
        progress.value = withTiming(percentage, {
            duration: 1000,
            easing: Easing.out(Easing.quad),
        });
    }, [percentage]);

    const animatedStyle = useAnimatedStyle(() => ({
        width: `${progress.value}%`,
    }));

    const textColor = darkMode ? 'rgba(255,255,255,0.7)' : 'rgba(0,0,0,0.7)';
    const bgColor = darkMode ? 'rgba(255,255,255,0.1)' : 'rgba(0,0,0,0.05)';

    return (
        <View style={styles.container}>
            <View style={styles.header}>
                <Text style={[styles.label, { color: textColor }]}>{label}</Text>
                <Text style={[styles.value, { color: textColor }]}>{value}g</Text>
            </View>
            <View style={[styles.track, { backgroundColor: bgColor }]}>
                <Animated.View
                    style={[
                        styles.fill,
                        { backgroundColor: color },
                        animatedStyle,
                    ]}
                />
            </View>
        </View>
    );
};

const styles = StyleSheet.create({
    container: {
        flex: 1,
        gap: 8,
    },
    header: {
        flexDirection: 'row',
        justifyContent: 'space-between',
    },
    label: {
        fontSize: 12,
    },
    value: {
        fontSize: 12,
    },
    track: {
        height: 8,
        borderRadius: 4,
        overflow: 'hidden',
    },
    fill: {
        height: '100%',
        borderRadius: 4,
    },
});

export default MacroProgressBar;
