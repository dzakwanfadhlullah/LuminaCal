/**
 * CameraScannerScreen - AI food scanning with Vision Camera
 * Scaffolded for react-native-vision-camera integration.
 */

import React, { useState, useEffect } from 'react';
import {
    StyleSheet,
    View,
    Text,
    Pressable,
    Image,
} from 'react-native';
import Animated, {
    useSharedValue,
    useAnimatedStyle,
    withRepeat,
    withTiming,
    withSequence,
    Easing,
    FadeIn,
    FadeInDown,
} from 'react-native-reanimated';
import { X, Zap, Settings } from 'lucide-react-native';
import { GlassCard } from '../components/ui';
import { SCAN_RESULTS } from '../constants/data';

// NOTE: In production, replace this with actual Vision Camera import:
// import { Camera, useCameraDevice } from 'react-native-vision-camera';

interface CameraScannerScreenProps {
    onClose: () => void;
    onScanComplete: () => void;
}

export const CameraScannerScreen: React.FC<CameraScannerScreenProps> = ({
    onClose,
    onScanComplete,
}) => {
    const [found, setFound] = useState(false);

    // Scanning animation
    const scanLineY = useSharedValue(0);

    useEffect(() => {
        // Animate scanning line
        scanLineY.value = withRepeat(
            withTiming(1, { duration: 2000, easing: Easing.linear }),
            -1,
            false
        );

        // Simulate finding food after 2.5 seconds
        const timer = setTimeout(() => {
            setFound(true);
        }, 2500);

        return () => clearTimeout(timer);
    }, []);

    const scanLineStyle = useAnimatedStyle(() => ({
        top: `${scanLineY.value * 100}%`,
        opacity: found ? 0 : 1,
    }));

    return (
        <View style={styles.container}>
            {/* Mock Camera Viewfinder */}
            <View style={styles.viewfinder}>
                {/* Placeholder Image (replace with actual Camera component) */}
                <Image
                    source={{ uri: 'https://images.unsplash.com/photo-1550547660-d9450f859349?w=800&q=80' }}
                    style={styles.cameraPreview}
                />

                {/* Top Controls */}
                <View style={styles.topControls}>
                    <Pressable style={styles.controlButton} onPress={onClose}>
                        <X size={24} color="#FFFFFF" />
                    </Pressable>
                    <View style={styles.modeBadge}>
                        <Text style={styles.modeBadgeText}>AI Mode Active</Text>
                    </View>
                    <Pressable style={styles.controlButton}>
                        <Zap size={24} color="#FFFFFF" />
                    </Pressable>
                </View>

                {/* Scanning Overlay */}
                <View style={styles.scanOverlay}>
                    <View
                        style={[
                            styles.scanFrame,
                            found && styles.scanFrameFound,
                        ]}
                    >
                        {/* Corner Markers */}
                        <View style={[styles.corner, styles.cornerTL]} />
                        <View style={[styles.corner, styles.cornerTR]} />
                        <View style={[styles.corner, styles.cornerBL]} />
                        <View style={[styles.corner, styles.cornerBR]} />

                        {/* Scanning Line */}
                        {!found && (
                            <Animated.View style={[styles.scanLine, scanLineStyle]} />
                        )}

                        {/* Found Tag */}
                        {found && (
                            <Animated.View
                                entering={FadeInDown.duration(400).springify()}
                                style={styles.foundTag}
                            >
                                <GlassCard style={styles.foundCard}>
                                    <View style={styles.foundDot} />
                                    <View>
                                        <Text style={styles.foundName}>{SCAN_RESULTS.name}</Text>
                                        <Text style={styles.foundCals}>~{SCAN_RESULTS.calories} kcal</Text>
                                    </View>
                                </GlassCard>
                            </Animated.View>
                        )}
                    </View>
                </View>

                {/* AR Bubbles */}
                {found && (
                    <>
                        <Animated.View
                            entering={FadeIn.delay(100).duration(300)}
                            style={[styles.arBubble, { top: '33%', left: '25%' }]}
                        >
                            <Text style={styles.arBubbleText}>Lettuce</Text>
                        </Animated.View>
                        <Animated.View
                            entering={FadeIn.delay(200).duration(300)}
                            style={[styles.arBubble, { bottom: '33%', right: '25%' }]}
                        >
                            <Text style={styles.arBubbleText}>Chicken</Text>
                        </Animated.View>
                    </>
                )}
            </View>

            {/* Bottom Controls */}
            <View style={styles.bottomControls}>
                <Text style={styles.instruction}>
                    {found ? 'Tap shutter to analyze' : 'Point camera at food'}
                </Text>

                <View style={styles.controlsRow}>
                    {/* Gallery Button */}
                    <Pressable style={styles.sideButton}>
                        <ImageIcon size={20} color="rgba(255,255,255,0.5)" />
                    </Pressable>

                    {/* Shutter Button */}
                    <Pressable
                        onPress={onScanComplete}
                        style={[
                            styles.shutterButton,
                            found && styles.shutterButtonActive,
                        ]}
                    >
                        <View style={[
                            styles.shutterInner,
                            found && styles.shutterInnerActive,
                        ]} />
                    </Pressable>

                    {/* Settings Button */}
                    <Pressable style={styles.sideButton}>
                        <Settings size={20} color="rgba(255,255,255,0.5)" />
                    </Pressable>
                </View>
            </View>
        </View>
    );
};

// Simple Image Icon component
const ImageIcon: React.FC<{ size: number; color: string }> = ({ size, color }) => (
    <View style={{ width: size, height: size }}>
        <View style={{
            width: size,
            height: size,
            borderWidth: 2,
            borderColor: color,
            borderRadius: 4,
        }}>
            <View style={{
                position: 'absolute',
                top: 4,
                left: 4,
                width: 4,
                height: 4,
                borderRadius: 2,
                backgroundColor: color,
            }} />
        </View>
    </View>
);

const styles = StyleSheet.create({
    container: {
        flex: 1,
        backgroundColor: '#000000',
    },
    viewfinder: {
        flex: 1,
        position: 'relative',
    },
    cameraPreview: {
        ...StyleSheet.absoluteFillObject,
        opacity: 0.8,
    },
    topControls: {
        position: 'absolute',
        top: 48,
        left: 24,
        right: 24,
        flexDirection: 'row',
        justifyContent: 'space-between',
        alignItems: 'center',
        zIndex: 10,
    },
    controlButton: {
        width: 48,
        height: 48,
        borderRadius: 24,
        backgroundColor: 'rgba(0,0,0,0.4)',
        alignItems: 'center',
        justifyContent: 'center',
    },
    modeBadge: {
        paddingHorizontal: 16,
        paddingVertical: 6,
        borderRadius: 16,
        backgroundColor: 'rgba(0,0,0,0.4)',
        borderWidth: 1,
        borderColor: 'rgba(255,255,255,0.1)',
    },
    modeBadgeText: {
        fontSize: 12,
        fontWeight: '500',
        color: '#FFFFFF',
    },
    scanOverlay: {
        ...StyleSheet.absoluteFillObject,
        alignItems: 'center',
        justifyContent: 'center',
    },
    scanFrame: {
        width: 256,
        height: 256,
        borderWidth: 2,
        borderColor: 'rgba(255,255,255,0.5)',
        borderRadius: 32,
        position: 'relative',
        overflow: 'hidden',
    },
    scanFrameFound: {
        borderColor: '#4ADE80',
    },
    corner: {
        position: 'absolute',
        width: 24,
        height: 24,
        borderColor: '#FFFFFF',
    },
    cornerTL: {
        top: -1,
        left: -1,
        borderTopWidth: 4,
        borderLeftWidth: 4,
        borderTopLeftRadius: 12,
    },
    cornerTR: {
        top: -1,
        right: -1,
        borderTopWidth: 4,
        borderRightWidth: 4,
        borderTopRightRadius: 12,
    },
    cornerBL: {
        bottom: -1,
        left: -1,
        borderBottomWidth: 4,
        borderLeftWidth: 4,
        borderBottomLeftRadius: 12,
    },
    cornerBR: {
        bottom: -1,
        right: -1,
        borderBottomWidth: 4,
        borderRightWidth: 4,
        borderBottomRightRadius: 12,
    },
    scanLine: {
        position: 'absolute',
        left: 0,
        right: 0,
        height: 4,
        backgroundColor: '#60A5FA',
        shadowColor: '#60A5FA',
        shadowOffset: { width: 0, height: 0 },
        shadowOpacity: 0.8,
        shadowRadius: 15,
        elevation: 10,
    },
    foundTag: {
        position: 'absolute',
        top: -64,
        left: '50%',
        transform: [{ translateX: -75 }],
    },
    foundCard: {
        flexDirection: 'row',
        alignItems: 'center',
        paddingHorizontal: 12,
        paddingVertical: 8,
        gap: 8,
        borderRadius: 12,
        borderColor: 'rgba(74, 222, 128, 0.5)',
    },
    foundDot: {
        width: 8,
        height: 8,
        borderRadius: 4,
        backgroundColor: '#22C55E',
    },
    foundName: {
        fontSize: 12,
        fontWeight: '700',
        color: '#FFFFFF',
    },
    foundCals: {
        fontSize: 10,
        fontWeight: '500',
        color: 'rgba(255,255,255,0.5)',
    },
    arBubble: {
        position: 'absolute',
        paddingHorizontal: 12,
        paddingVertical: 6,
        borderRadius: 8,
        backgroundColor: 'rgba(0,0,0,0.6)',
        borderWidth: 1,
        borderColor: 'rgba(255,255,255,0.1)',
    },
    arBubbleText: {
        fontSize: 10,
        color: '#FFFFFF',
    },
    bottomControls: {
        height: 192,
        backgroundColor: '#000000',
        alignItems: 'center',
        justifyContent: 'center',
        paddingBottom: 20,
    },
    instruction: {
        position: 'absolute',
        top: -24,
        fontSize: 14,
        fontWeight: '500',
        color: 'rgba(255,255,255,0.8)',
    },
    controlsRow: {
        flexDirection: 'row',
        alignItems: 'center',
        gap: 48,
    },
    sideButton: {
        width: 48,
        height: 48,
        borderRadius: 24,
        backgroundColor: 'rgba(255,255,255,0.1)',
        borderWidth: 1,
        borderColor: 'rgba(255,255,255,0.1)',
        alignItems: 'center',
        justifyContent: 'center',
    },
    shutterButton: {
        width: 80,
        height: 80,
        borderRadius: 40,
        borderWidth: 4,
        borderColor: '#FFFFFF',
        alignItems: 'center',
        justifyContent: 'center',
    },
    shutterButtonActive: {
        borderColor: '#4ADE80',
        transform: [{ scale: 1.1 }],
    },
    shutterInner: {
        width: 64,
        height: 64,
        borderRadius: 32,
        backgroundColor: '#FFFFFF',
    },
    shutterInnerActive: {
        transform: [{ scale: 0.9 }],
    },
});

export default CameraScannerScreen;
