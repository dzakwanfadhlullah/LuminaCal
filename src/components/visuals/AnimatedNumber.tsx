/**
 * AnimatedNumber - Counting number animation using Reanimated
 * Smooth number transition with easing.
 */

import React from 'react';
import { Text, TextStyle } from 'react-native';
import Animated, {
    useSharedValue,
    useAnimatedProps,
    useDerivedValue,
    withTiming,
    Easing,
    runOnJS,
} from 'react-native-reanimated';
import { ANIMATION_CONFIG } from '../../constants';

// Create animated text component
const AnimatedText = Animated.createAnimatedComponent(Text);

interface AnimatedNumberProps {
    value: number;
    duration?: number;
    style?: TextStyle;
    formatFn?: (value: number) => string;
}

export const AnimatedNumber: React.FC<AnimatedNumberProps> = ({
    value,
    duration = ANIMATION_CONFIG.number.duration,
    style,
    formatFn = (v) => Math.floor(v).toLocaleString(),
}) => {
    const animatedValue = useSharedValue(0);
    const [displayValue, setDisplayValue] = React.useState('0');

    React.useEffect(() => {
        animatedValue.value = withTiming(value, {
            duration,
            easing: Easing.out(Easing.quad),
        });
    }, [value, duration]);

    // Update display value using derived value
    useDerivedValue(() => {
        runOnJS(setDisplayValue)(formatFn(animatedValue.value));
    });

    return <Text style={style}>{displayValue}</Text>;
};

export default AnimatedNumber;
