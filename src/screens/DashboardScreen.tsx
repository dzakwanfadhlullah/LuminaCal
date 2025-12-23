/**
 * DashboardScreen - Main home screen with ring and timeline
 */

import React from 'react';
import {
    StyleSheet,
    View,
    Text,
    ScrollView,
    Image,
} from 'react-native';
import { SafeAreaView } from 'react-native-safe-area-context';
import { TrendingUp, Flame } from 'lucide-react-native';
import { GlassCard } from '../components/ui';
import { AppleRing, MacroProgressBar } from '../components/visuals';
import { HistoryItem, Calories, Macros } from '../constants/types';
import { getMealTypeEmoji, DEFAULT_MACRO_GOALS } from '../constants/data';
import { calculateProgress } from '../utils';
import { COLORS } from '../constants/theme';

interface DashboardScreenProps {
    history: HistoryItem[];
    calories: Calories;
    macros: Macros;
    darkMode?: boolean;
}

export const DashboardScreen: React.FC<DashboardScreenProps> = ({
    history,
    calories,
    macros,
    darkMode = false,
}) => {
    const colors = darkMode ? COLORS.dark : COLORS.light;
    const remaining = calories.target - calories.consumed;
    const progress = calculateProgress(calories.consumed, calories.target);

    return (
        <SafeAreaView style={styles.container} edges={['top']}>
            <ScrollView
                style={styles.scrollView}
                contentContainerStyle={styles.content}
                showsVerticalScrollIndicator={false}
            >
                {/* Header */}
                <View style={styles.header}>
                    <View>
                        <Text style={[styles.headerSubtitle, { color: colors.text.muted }]}>
                            Today
                        </Text>
                        <Text style={[styles.headerTitle, { color: colors.text.primary }]}>
                            Dashboard
                        </Text>
                    </View>
                    <View style={styles.avatarContainer}>
                        <Image
                            source={{ uri: 'https://api.dicebear.com/7.x/avataaars/svg?seed=Felix' }}
                            style={styles.avatar}
                        />
                    </View>
                </View>

                {/* Main Ring Card */}
                <GlassCard style={styles.ringCard} darkMode={darkMode}>
                    {/* On Track Badge */}
                    <View style={styles.badge}>
                        <TrendingUp size={12} color="#22C55E" />
                        <Text style={styles.badgeText}>On Track</Text>
                    </View>

                    {/* Apple Ring */}
                    <AppleRing
                        size={240}
                        strokeWidth={24}
                        progress={progress}
                        label={remaining}
                        subLabel="Remaining"
                        color={COLORS.primary.orange}
                        icon={<Flame size={24} color={COLORS.primary.orange} />}
                        darkMode={darkMode}
                    />

                    {/* Macros Breakdown */}
                    <View style={styles.macrosGrid}>
                        <MacroProgressBar
                            label="Protein"
                            value={macros.protein}
                            max={DEFAULT_MACRO_GOALS.protein}
                            color={COLORS.macro.protein}
                            darkMode={darkMode}
                        />
                        <MacroProgressBar
                            label="Carbs"
                            value={macros.carbs}
                            max={DEFAULT_MACRO_GOALS.carbs}
                            color={COLORS.macro.carbs}
                            darkMode={darkMode}
                        />
                        <MacroProgressBar
                            label="Fat"
                            value={macros.fat}
                            max={DEFAULT_MACRO_GOALS.fat}
                            color={COLORS.macro.fat}
                            darkMode={darkMode}
                        />
                    </View>
                </GlassCard>

                {/* Timeline */}
                <View style={styles.timelineSection}>
                    <View style={styles.timelineHeader}>
                        <Text style={[styles.sectionTitle, { color: colors.text.primary }]}>
                            Recent Logs
                        </Text>
                        <Text style={styles.viewAll}>View All</Text>
                    </View>

                    <View style={styles.timeline}>
                        {/* Vertical Line */}
                        <View style={styles.timelineLine} />

                        {history.map((item, idx) => (
                            <GlassCard
                                key={item.id}
                                style={styles.timelineCard}
                                delay={idx * 100}
                                darkMode={darkMode}
                            >
                                <View style={styles.timelineItem}>
                                    <View style={styles.mealIcon}>
                                        <Text style={styles.mealEmoji}>
                                            {getMealTypeEmoji(item.type)}
                                        </Text>
                                    </View>
                                    <View style={styles.mealInfo}>
                                        <Text style={[styles.mealName, { color: colors.text.primary }]}>
                                            {item.name}
                                        </Text>
                                        <Text style={[styles.mealMeta, { color: colors.text.muted }]}>
                                            {item.time} • {item.type}
                                        </Text>
                                    </View>
                                    <View style={styles.mealCalories}>
                                        <Text style={[styles.calorieValue, { color: colors.text.primary }]}>
                                            {item.calories}
                                        </Text>
                                        <Text style={[styles.calorieUnit, { color: colors.text.muted }]}>
                                            kcal
                                        </Text>
                                    </View>
                                </View>
                            </GlassCard>
                        ))}
                    </View>
                </View>
            </ScrollView>
        </SafeAreaView>
    );
};

const styles = StyleSheet.create({
    container: {
        flex: 1,
    },
    scrollView: {
        flex: 1,
    },
    content: {
        padding: 16,
        paddingBottom: 100,
    },
    header: {
        flexDirection: 'row',
        justifyContent: 'space-between',
        alignItems: 'center',
        paddingHorizontal: 8,
        marginBottom: 24,
    },
    headerSubtitle: {
        fontSize: 12,
        fontWeight: '500',
        textTransform: 'uppercase',
        letterSpacing: 2,
    },
    headerTitle: {
        fontSize: 24,
        fontWeight: '700',
    },
    avatarContainer: {
        width: 40,
        height: 40,
        borderRadius: 20,
        padding: 2,
        backgroundColor: 'transparent',
        borderWidth: 2,
        borderColor: COLORS.primary.orange,
    },
    avatar: {
        width: '100%',
        height: '100%',
        borderRadius: 18,
    },
    ringCard: {
        padding: 32,
        alignItems: 'center',
        marginBottom: 24,
    },
    badge: {
        position: 'absolute',
        top: 16,
        right: 16,
        flexDirection: 'row',
        alignItems: 'center',
        gap: 4,
        paddingHorizontal: 8,
        paddingVertical: 4,
        borderRadius: 12,
        backgroundColor: 'rgba(34, 197, 94, 0.1)',
        borderWidth: 1,
        borderColor: 'rgba(34, 197, 94, 0.2)',
    },
    badgeText: {
        fontSize: 10,
        fontWeight: '600',
        color: '#22C55E',
    },
    macrosGrid: {
        flexDirection: 'row',
        gap: 16,
        width: '100%',
        marginTop: 32,
    },
    timelineSection: {
        marginTop: 8,
    },
    timelineHeader: {
        flexDirection: 'row',
        justifyContent: 'space-between',
        alignItems: 'center',
        paddingHorizontal: 8,
        marginBottom: 16,
    },
    sectionTitle: {
        fontSize: 16,
        fontWeight: '600',
    },
    viewAll: {
        fontSize: 12,
        fontWeight: '500',
        color: '#3B82F6',
    },
    timeline: {
        position: 'relative',
        paddingLeft: 16,
    },
    timelineLine: {
        position: 'absolute',
        left: 16,
        top: 8,
        bottom: 8,
        width: 2,
        backgroundColor: 'rgba(59, 130, 246, 0.3)',
        borderRadius: 1,
    },
    timelineCard: {
        marginLeft: 16,
        marginBottom: 12,
        padding: 16,
    },
    timelineItem: {
        flexDirection: 'row',
        alignItems: 'center',
        gap: 12,
    },
    mealIcon: {
        width: 40,
        height: 40,
        borderRadius: 16,
        backgroundColor: 'rgba(59, 130, 246, 0.1)',
        alignItems: 'center',
        justifyContent: 'center',
    },
    mealEmoji: {
        fontSize: 20,
    },
    mealInfo: {
        flex: 1,
    },
    mealName: {
        fontSize: 14,
        fontWeight: '500',
    },
    mealMeta: {
        fontSize: 12,
        marginTop: 2,
    },
    mealCalories: {
        alignItems: 'flex-end',
    },
    calorieValue: {
        fontSize: 14,
        fontWeight: '700',
    },
    calorieUnit: {
        fontSize: 10,
    },
});

export default DashboardScreen;
