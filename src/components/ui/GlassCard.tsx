/**
 * GlassCard - Frosted glass card component using Skia
 * Replicates the backdrop-filter: blur() effect from web.
 */

import React from 'react';
import {
    StyleSheet,
    View,
    ViewStyle,
    Pressable,
    LayoutChangeEvent,
} from 'react-native';
import {
    Canvas,
    RoundedRect,
    BackdropBlur,
    Fill,
    LinearGradient,
    vec,
} from '@shopify/react-native-skia';
import Animated, {
    useAnimatedStyle,
    useSharedValue,
    withSpring,
    withTiming,
    interpolate,
} from 'react-native-reanimated';
import { GLASS_TOKENS, COLORS } from '../../constants';

const AnimatedPressable = Animated.createAnimatedComponent(Pressable);

interface GlassCardProps {
    children: React.ReactNode;
    className?: string;
    style?: ViewStyle;
    onPress?: () => void;
    active?: boolean;
    delay?: number;
    darkMode?: boolean;
}

export const GlassCard: React.FC<GlassCardProps> = ({
    children,
    style,
    onPress,
    active = false,
    delay = 0,
    darkMode = false,
}) => {
    const [dimensions, setDimensions] = React.useState({ width: 0, height: 0 });
    const scale = useSharedValue(1);
    const opacity = useSharedValue(0);
    const colors = darkMode ? COLORS.dark : COLORS.light;

    // Entry animation
    React.useEffect(() => {
        const timeout = setTimeout(() => {
            opacity.value = withTiming(1, { duration: 500 });
        }, delay);
        return () => clearTimeout(timeout);
    }, [delay]);

    const handleLayout = (event: LayoutChangeEvent) => {
        const { width, height } = event.nativeEvent.layout;
        setDimensions({ width, height });
    };

    const handlePressIn = () => {
        scale.value = withSpring(0.98, { damping: 15, stiffness: 300 });
    };

    const handlePressOut = () => {
        scale.value = withSpring(1, { damping: 15, stiffness: 300 });
    };

    const animatedStyle = useAnimatedStyle(() => ({
        opacity: opacity.value,
        transform: [
            { scale: scale.value },
            { translateY: interpolate(opacity.value, [0, 1], [10, 0]) },
        ],
    }));

    const borderRadius = GLASS_TOKENS.borderRadius;
    const { width, height } = dimensions;

    return (
        <AnimatedPressable
            onLayout={handleLayout}
            onPress={onPress}
            onPressIn={handlePressIn}
            onPressOut={handlePressOut}
            style={[styles.container, animatedStyle, style]}
        >
            {/* Skia Glass Layer */}
            {width > 0 && height > 0 && (
                <Canvas style={StyleSheet.absoluteFill} pointerEvents="none">
                    {/* Backdrop Blur */}
                    <BackdropBlur blur={GLASS_TOKENS.blur} clip={{ x: 0, y: 0, width, height }}>
                        <RoundedRect
                            x={0}
                            y={0}
                            width={width}
                            height={height}
                            r={borderRadius}
                        />
                    </BackdropBlur>

                    {/* Glass Fill */}
                    <RoundedRect
                        x={0}
                        y={0}
                        width={width}
                        height={height}
                        r={borderRadius}
                        color={active ? 'rgba(255, 255, 255, 0.2)' : colors.glass.background}
                    />

                    {/* Shine Gradient Overlay */}
                    <RoundedRect
                        x={0}
                        y={0}
                        width={width}
                        height={height}
                        r={borderRadius}
                    >
                        <LinearGradient
                            start={vec(0, 0)}
                            end={vec(width, height)}
                            colors={['rgba(255, 255, 255, 0.1)', 'transparent']}
                        />
                    </RoundedRect>
                </Canvas>
            )}

            {/* Border Overlay */}
            <View
                style={[
                    styles.border,
                    {
                        borderRadius,
                        borderColor: active
                            ? 'rgba(255, 255, 255, 0.5)'
                            : colors.glass.border,
                    },
                ]}
                pointerEvents="none"
            />

            {/* Content */}
            <View style={styles.content}>{children}</View>
        </AnimatedPressable>
    );
};

const styles = StyleSheet.create({
    container: {
        position: 'relative',
        overflow: 'hidden',
        borderRadius: GLASS_TOKENS.borderRadius,
    },
    border: {
        ...StyleSheet.absoluteFillObject,
        borderWidth: GLASS_TOKENS.borderWidth,
    },
    content: {
        position: 'relative',
        zIndex: 1,
    },
});

export default GlassCard;
