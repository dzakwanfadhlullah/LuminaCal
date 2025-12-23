/**
 * MeshBackground - Animated gradient mesh using Skia
 * Replicates the breathing blob effect from the web version.
 */

import React from 'react';
import { StyleSheet, useWindowDimensions, View } from 'react-native';
import {
    Canvas,
    Circle,
    BlurMask,
    vec,
    Group,
} from '@shopify/react-native-skia';
import Animated, {
    useSharedValue,
    useAnimatedStyle,
    withRepeat,
    withTiming,
    withDelay,
    Easing,
    useDerivedValue,
} from 'react-native-reanimated';
import { COLORS, ANIMATION_CONFIG } from '../../constants';

interface MeshBackgroundProps {
    darkMode?: boolean;
}

const AnimatedCanvas = Animated.createAnimatedComponent(Canvas);

export const MeshBackground: React.FC<MeshBackgroundProps> = ({ darkMode = false }) => {
    const { width, height } = useWindowDimensions();
    const colors = darkMode ? COLORS.dark : COLORS.light;

    // Shared values for blob animations
    const blob1Progress = useSharedValue(0);
    const blob2Progress = useSharedValue(0);
    const blob3Progress = useSharedValue(0);

    // Start animations on mount
    React.useEffect(() => {
        const { duration } = ANIMATION_CONFIG.blob;

        blob1Progress.value = withRepeat(
            withTiming(1, { duration, easing: Easing.inOut(Easing.sin) }),
            -1,
            true
        );

        blob2Progress.value = withDelay(
            2000,
            withRepeat(
                withTiming(1, { duration, easing: Easing.inOut(Easing.sin) }),
                -1,
                true
            )
        );

        blob3Progress.value = withDelay(
            4000,
            withRepeat(
                withTiming(1, { duration, easing: Easing.inOut(Easing.sin) }),
                -1,
                true
            )
        );
    }, []);

    // Derived animated values for blob positions and scales
    const blob1X = useDerivedValue(() => {
        return width * -0.1 + blob1Progress.value * 30;
    });
    const blob1Y = useDerivedValue(() => {
        return height * -0.1 - blob1Progress.value * 50;
    });
    const blob1Scale = useDerivedValue(() => {
        const { min, max } = ANIMATION_CONFIG.blob.scale;
        return min + blob1Progress.value * (max - min);
    });

    const blob2X = useDerivedValue(() => {
        return width * 0.6 - blob2Progress.value * 20;
    });
    const blob2Y = useDerivedValue(() => {
        return height * 0.2 + blob2Progress.value * 20;
    });
    const blob2Scale = useDerivedValue(() => {
        const { min, max } = ANIMATION_CONFIG.blob.scale;
        return max - blob2Progress.value * (max - min);
    });

    const blob3X = useDerivedValue(() => {
        return width * 0.2 + blob3Progress.value * 20;
    });
    const blob3Y = useDerivedValue(() => {
        return height * 0.8 - blob3Progress.value * 30;
    });
    const blob3Scale = useDerivedValue(() => {
        const { min, max } = ANIMATION_CONFIG.blob.scale;
        return min + blob3Progress.value * (max - min) * 0.5;
    });

    // Base blob radius
    const baseRadius = Math.min(width, height) * 0.3;

    return (
        <View style={[styles.container, { backgroundColor: colors.background }]}>
            <Canvas style={styles.canvas}>
                {/* Purple Blob */}
                <Group
                    transform={[
                        { translateX: blob1X.value },
                        { translateY: blob1Y.value },
                        { scale: blob1Scale.value },
                    ]}
                >
                    <Circle
                        cx={baseRadius}
                        cy={baseRadius}
                        r={baseRadius}
                        color={colors.blob.purple}
                    >
                        <BlurMask blur={80} style="normal" />
                    </Circle>
                </Group>

                {/* Blue Blob */}
                <Group
                    transform={[
                        { translateX: blob2X.value },
                        { translateY: blob2Y.value },
                        { scale: blob2Scale.value },
                    ]}
                >
                    <Circle
                        cx={baseRadius * 1.2}
                        cy={baseRadius * 1.2}
                        r={baseRadius * 1.2}
                        color={colors.blob.blue}
                    >
                        <BlurMask blur={100} style="normal" />
                    </Circle>
                </Group>

                {/* Pink Blob */}
                <Group
                    transform={[
                        { translateX: blob3X.value },
                        { translateY: blob3Y.value },
                        { scale: blob3Scale.value },
                    ]}
                >
                    <Circle
                        cx={baseRadius}
                        cy={baseRadius}
                        r={baseRadius}
                        color={colors.blob.pink}
                    >
                        <BlurMask blur={80} style="normal" />
                    </Circle>
                </Group>
            </Canvas>
        </View>
    );
};

const styles = StyleSheet.create({
    container: {
        ...StyleSheet.absoluteFillObject,
        zIndex: -1,
    },
    canvas: {
        flex: 1,
    },
});

export default MeshBackground;
