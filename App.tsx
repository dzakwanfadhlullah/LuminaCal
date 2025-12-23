/**
 * App.tsx - Main application entry point
 */

import React from 'react';
import { StatusBar, View, StyleSheet, Text, ActivityIndicator } from 'react-native';
import { SafeAreaProvider } from 'react-native-safe-area-context';
import { GestureHandlerRootView } from 'react-native-gesture-handler';
import { Flame } from 'lucide-react-native';

import { MeshBackground } from './src/components/ui';
import { AppNavigator } from './src/navigation/AppNavigator';
import {
    DashboardScreen,
    StatisticsScreen,
    ExploreScreen,
    ProfileScreen,
    FoodDetailScreen,
    CameraScannerScreen,
} from './src/screens';
import { useAppLogic } from './src/hooks';
import { COLORS, LAYOUT } from './src/constants/theme';

export default function App() {
    const {
        screen,
        darkMode,
        showCamera,
        showFoodDetail,
        calories,
        macros,
        history,
        loading,
        setScreen,
        toggleDarkMode,
        openCamera,
        closeCamera,
        handleScanComplete,
        handleAddFood,
    } = useAppLogic();

    const colors = darkMode ? COLORS.dark : COLORS.light;

    // Loading Screen
    if (loading) {
        return (
            <View style={[styles.loadingContainer, { backgroundColor: colors.background }]}>
                <View style={styles.loadingContent}>
                    <View style={styles.loadingIcon}>
                        <Flame size={40} color="#FFFFFF" />
                    </View>
                    <Text style={[styles.loadingTitle, { color: colors.text.primary }]}>
                        LuminaCal
                    </Text>
                    <ActivityIndicator
                        size="small"
                        color={COLORS.primary.orange}
                        style={styles.spinner}
                    />
                </View>
            </View>
        );
    }

    return (
        <GestureHandlerRootView style={styles.container}>
            <SafeAreaProvider>
                <StatusBar
                    barStyle={darkMode ? 'light-content' : 'dark-content'}
                    backgroundColor="transparent"
                    translucent
                />

                {/* Dynamic Background */}
                <MeshBackground darkMode={darkMode} />

                {/* Main Content */}
                <View style={styles.content}>
                    {screen === 'home' && (
                        <DashboardScreen
                            history={history}
                            calories={calories}
                            macros={macros}
                            darkMode={darkMode}
                        />
                    )}
                    {screen === 'stats' && (
                        <StatisticsScreen darkMode={darkMode} />
                    )}
                    {screen === 'explore' && (
                        <ExploreScreen darkMode={darkMode} />
                    )}
                    {screen === 'profile' && (
                        <ProfileScreen
                            darkMode={darkMode}
                            onToggleDarkMode={toggleDarkMode}
                        />
                    )}
                </View>

                {/* Bottom Navigation */}
                {!showCamera && !showFoodDetail && (
                    <AppNavigator
                        activeTab={screen}
                        onTabChange={setScreen}
                        onScan={openCamera}
                        darkMode={darkMode}
                    />
                )}

                {/* Camera Modal */}
                {showCamera && (
                    <CameraScannerScreen
                        onClose={closeCamera}
                        onScanComplete={handleScanComplete}
                    />
                )}

                {/* Food Detail Modal */}
                {showFoodDetail && (
                    <FoodDetailScreen
                        onBack={() => {
                            // Go back to camera first
                            closeCamera();
                            openCamera();
                        }}
                        onAdd={handleAddFood}
                        darkMode={darkMode}
                    />
                )}
            </SafeAreaProvider>
        </GestureHandlerRootView>
    );
}

const styles = StyleSheet.create({
    container: {
        flex: 1,
    },
    content: {
        flex: 1,
        maxWidth: LAYOUT.maxWidth,
        alignSelf: 'center',
        width: '100%',
    },
    loadingContainer: {
        flex: 1,
        alignItems: 'center',
        justifyContent: 'center',
    },
    loadingContent: {
        alignItems: 'center',
    },
    loadingIcon: {
        width: 80,
        height: 80,
        borderRadius: 32,
        backgroundColor: COLORS.primary.orange,
        alignItems: 'center',
        justifyContent: 'center',
        marginBottom: 24,
        shadowColor: COLORS.primary.orange,
        shadowOffset: { width: 0, height: 8 },
        shadowOpacity: 0.4,
        shadowRadius: 16,
        elevation: 10,
    },
    loadingTitle: {
        fontSize: 24,
        fontWeight: '700',
        letterSpacing: -0.5,
    },
    spinner: {
        marginTop: 24,
    },
});
