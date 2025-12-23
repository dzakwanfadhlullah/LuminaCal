/**
 * AppleRing - Activity ring component using Skia
 * Replicates the Apple Watch-style progress ring from the web version.
 */

import React from 'react';
import { StyleSheet, View, Text } from 'react-native';
import {
    Canvas,
    Path,
    Skia,
    SweepGradient,
    vec,
    Shadow,
    Circle,
} from '@shopify/react-native-skia';
import Animated, {
    useSharedValue,
    useDerivedValue,
    withTiming,
    Easing,
} from 'react-native-reanimated';
import { ANIMATION_CONFIG } from '../../constants';
import { AnimatedNumber } from './AnimatedNumber';

interface AppleRingProps {
    size?: number;
    strokeWidth?: number;
    progress: number; // 0-100
    color?: string;
    icon?: React.ReactNode;
    label?: string | number;
    subLabel?: string;
    darkMode?: boolean;
}

export const AppleRing: React.FC<AppleRingProps> = ({
    size = 200,
    strokeWidth = 15,
    progress,
    color = '#FFB88C',
    icon,
    label,
    subLabel,
    darkMode = false,
}) => {
    const animatedProgress = useSharedValue(0);

    // Animate progress on mount and value change
    React.useEffect(() => {
        animatedProgress.value = withTiming(progress / 100, {
            duration: ANIMATION_CONFIG.ring.duration,
            easing: Easing.out(Easing.quad),
        });
    }, [progress]);

    const center = size / 2;
    const radius = center - strokeWidth;

    // Create the arc path
    const backgroundPath = React.useMemo(() => {
        const path = Skia.Path.Make();
        path.addCircle(center, center, radius);
        return path;
    }, [center, radius]);

    // Animated progress path
    const progressPath = useDerivedValue(() => {
        const path = Skia.Path.Make();
        const startAngle = -90; // Start from top
        const sweepAngle = animatedProgress.value * 360;

        // Create arc
        path.addArc(
            {
                x: strokeWidth,
                y: strokeWidth,
                width: radius * 2,
                height: radius * 2,
            },
            startAngle,
            sweepAngle
        );

        return path;
    }, [animatedProgress]);

    const bgColor = darkMode ? 'rgba(255, 255, 255, 0.05)' : 'rgba(0, 0, 0, 0.05)';
    const textColor = darkMode ? '#F1F5F9' : '#1E293B';

    return (
        <View style={[styles.container, { width: size, height: size }]}>
            <Canvas style={StyleSheet.absoluteFill}>
                {/* Background Circle */}
                <Path
                    path={backgroundPath}
                    style="stroke"
                    strokeWidth={strokeWidth}
                    color={bgColor}
                    strokeCap="round"
                />

                {/* Progress Arc */}
                <Path
                    path={progressPath}
                    style="stroke"
                    strokeWidth={strokeWidth}
                    strokeCap="round"
                    color={color}
                >
                    <Shadow dx={0} dy={0} blur={15} color={color} />
                </Path>
            </Canvas>

            {/* Inner Content */}
            <View style={styles.content}>
                {icon && <View style={styles.icon}>{icon}</View>}
                <Text style={[styles.label, { color: textColor }]}>
                    {typeof label === 'number' ? (
                        <AnimatedNumber value={label} />
                    ) : (
                        label
                    )}
                </Text>
                {subLabel && (
                    <Text style={[styles.subLabel, { color: textColor }]}>
                        {subLabel}
                    </Text>
                )}
            </View>
        </View>
    );
};

const styles = StyleSheet.create({
    container: {
        position: 'relative',
        alignItems: 'center',
        justifyContent: 'center',
    },
    content: {
        alignItems: 'center',
        justifyContent: 'center',
    },
    icon: {
        marginBottom: 4,
        opacity: 0.8,
    },
    label: {
        fontSize: 32,
        fontWeight: '700',
        letterSpacing: -1,
    },
    subLabel: {
        fontSize: 10,
        fontWeight: '500',
        textTransform: 'uppercase',
        letterSpacing: 2,
        opacity: 0.5,
    },
});

export default AppleRing;
