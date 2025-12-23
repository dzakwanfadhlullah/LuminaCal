/**
 * GlassButton - Stylized button with glass effects
 * Matches the web version's button variants.
 */

import React from 'react';
import {
    StyleSheet,
    Text,
    Pressable,
    ViewStyle,
    TextStyle,
    View,
} from 'react-native';
import Animated, {
    useAnimatedStyle,
    useSharedValue,
    withSpring,
} from 'react-native-reanimated';
import { LinearGradient } from 'expo-linear-gradient';
import { ButtonVariant, COLORS } from '../../constants';

const AnimatedPressable = Animated.createAnimatedComponent(Pressable);

interface GlassButtonProps {
    children: React.ReactNode;
    variant?: ButtonVariant;
    style?: ViewStyle;
    textStyle?: TextStyle;
    onPress?: () => void;
    icon?: React.ReactNode;
    darkMode?: boolean;
}

const VARIANT_STYLES: Record<ButtonVariant, { gradient: string[]; textColor: string; hasBorder: boolean }> = {
    primary: {
        gradient: ['#1e293b', '#0f172a'], // slate-800 to slate-900
        textColor: '#ffffff',
        hasBorder: false,
    },
    secondary: {
        gradient: ['rgba(255,255,255,0.2)', 'rgba(255,255,255,0.15)'],
        textColor: '#1e293b',
        hasBorder: true,
    },
    accent: {
        gradient: ['#fb923c', '#ec4899'], // orange-400 to pink-500
        textColor: '#ffffff',
        hasBorder: false,
    },
    ghost: {
        gradient: ['transparent', 'transparent'],
        textColor: '#64748b',
        hasBorder: false,
    },
};

const DARK_VARIANT_STYLES: Record<ButtonVariant, { gradient: string[]; textColor: string; hasBorder: boolean }> = {
    primary: {
        gradient: ['#ffffff', '#e2e8f0'], // white to slate-200
        textColor: '#0f172a',
        hasBorder: false,
    },
    secondary: {
        gradient: ['rgba(255,255,255,0.2)', 'rgba(255,255,255,0.15)'],
        textColor: '#ffffff',
        hasBorder: true,
    },
    accent: {
        gradient: ['#fb923c', '#ec4899'],
        textColor: '#ffffff',
        hasBorder: false,
    },
    ghost: {
        gradient: ['transparent', 'transparent'],
        textColor: '#94a3b8',
        hasBorder: false,
    },
};

export const GlassButton: React.FC<GlassButtonProps> = ({
    children,
    variant = 'primary',
    style,
    textStyle,
    onPress,
    icon,
    darkMode = false,
}) => {
    const scale = useSharedValue(1);
    const variantConfig = darkMode ? DARK_VARIANT_STYLES[variant] : VARIANT_STYLES[variant];

    const handlePressIn = () => {
        scale.value = withSpring(0.95, { damping: 15, stiffness: 400 });
    };

    const handlePressOut = () => {
        scale.value = withSpring(1, { damping: 15, stiffness: 400 });
    };

    const animatedStyle = useAnimatedStyle(() => ({
        transform: [{ scale: scale.value }],
    }));

    return (
        <AnimatedPressable
            onPress={onPress}
            onPressIn={handlePressIn}
            onPressOut={handlePressOut}
            style={[styles.container, animatedStyle, style]}
        >
            <LinearGradient
                colors={variantConfig.gradient as [string, string]}
                start={{ x: 0, y: 0 }}
                end={{ x: 1, y: 0 }}
                style={[
                    styles.gradient,
                    variantConfig.hasBorder && styles.border,
                ]}
            >
                <View style={styles.content}>
                    {icon && <View style={styles.icon}>{icon}</View>}
                    <Text style={[styles.text, { color: variantConfig.textColor }, textStyle]}>
                        {children}
                    </Text>
                </View>
            </LinearGradient>
        </AnimatedPressable>
    );
};

const styles = StyleSheet.create({
    container: {
        borderRadius: 16,
        overflow: 'hidden',
    },
    gradient: {
        paddingHorizontal: 24,
        paddingVertical: 12,
        borderRadius: 16,
    },
    border: {
        borderWidth: 1,
        borderColor: 'rgba(255, 255, 255, 0.2)',
    },
    content: {
        flexDirection: 'row',
        alignItems: 'center',
        justifyContent: 'center',
        gap: 8,
    },
    icon: {
        marginRight: 4,
    },
    text: {
        fontSize: 14,
        fontWeight: '500',
        textAlign: 'center',
    },
});

export default GlassButton;
