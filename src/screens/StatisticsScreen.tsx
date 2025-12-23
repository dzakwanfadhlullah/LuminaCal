/**
 * StatisticsScreen - Charts and progress tracking
 */

import React from 'react';
import {
    StyleSheet,
    View,
    Text,
    ScrollView,
} from 'react-native';
import { SafeAreaView } from 'react-native-safe-area-context';
import { Target, Droplet, Award } from 'lucide-react-native';
import { GlassCard } from '../components/ui';
import { StatisticsChart } from '../components/visuals';
import { WEEKLY_CALORIE_DATA, DAY_LABELS } from '../constants/data';
import { COLORS } from '../constants/theme';

interface StatisticsScreenProps {
    darkMode?: boolean;
}

export const StatisticsScreen: React.FC<StatisticsScreenProps> = ({
    darkMode = false,
}) => {
    const colors = darkMode ? COLORS.dark : COLORS.light;

    return (
        <SafeAreaView style={styles.container} edges={['top']}>
            <ScrollView
                style={styles.scrollView}
                contentContainerStyle={styles.content}
                showsVerticalScrollIndicator={false}
            >
                {/* Header */}
                <View style={styles.header}>
                    <Text style={[styles.headerTitle, { color: colors.text.primary }]}>
                        Statistics
                    </Text>
                    <View style={styles.dropdown}>
                        <Text style={[styles.dropdownText, { color: colors.text.primary }]}>
                            Last 7 Days
                        </Text>
                    </View>
                </View>

                {/* Calorie Trend Chart */}
                <GlassCard style={styles.chartCard} darkMode={darkMode}>
                    <Text style={[styles.chartTitle, { color: colors.text.muted }]}>
                        Calorie Intake Trend
                    </Text>
                    <View style={styles.chartContainer}>
                        <StatisticsChart
                            data={WEEKLY_CALORIE_DATA}
                            labels={DAY_LABELS}
                            width={280}
                            height={150}
                            darkMode={darkMode}
                        />
                    </View>
                </GlassCard>

                {/* Metric Cards */}
                <View style={styles.metricsRow}>
                    {/* Weight Goal */}
                    <GlassCard style={styles.metricCard} darkMode={darkMode}>
                        <View style={styles.metricHeader}>
                            <Target size={18} color="#EC4899" />
                            <Text style={styles.metricLabel}>Weight Goal</Text>
                        </View>
                        <Text style={[styles.metricValue, { color: colors.text.primary }]}>
                            65.0 <Text style={styles.metricUnit}>kg</Text>
                        </Text>
                        <View style={styles.progressTrack}>
                            <View style={[styles.progressFill, { width: '70%', backgroundColor: '#EC4899' }]} />
                        </View>
                        <Text style={[styles.metricHint, { color: colors.text.muted }]}>
                            3.5kg to go
                        </Text>
                    </GlassCard>

                    {/* Water */}
                    <GlassCard style={styles.metricCard} darkMode={darkMode}>
                        <View style={styles.metricHeader}>
                            <Droplet size={18} color="#6366F1" />
                            <Text style={styles.metricLabel}>Water</Text>
                        </View>
                        <Text style={[styles.metricValue, { color: colors.text.primary }]}>
                            1,250 <Text style={styles.metricUnit}>ml</Text>
                        </Text>
                        <View style={styles.waterBars}>
                            {[1, 2, 3, 4, 5].map((i) => (
                                <View
                                    key={i}
                                    style={[
                                        styles.waterBar,
                                        {
                                            backgroundColor: i <= 3 ? '#818CF8' : (darkMode ? 'rgba(255,255,255,0.1)' : 'rgba(0,0,0,0.1)'),
                                        },
                                    ]}
                                />
                            ))}
                        </View>
                    </GlassCard>
                </View>

                {/* Achievement */}
                <GlassCard style={styles.achievementCard} darkMode={darkMode}>
                    <View style={styles.achievementIcon}>
                        <Award size={24} color="#FFFFFF" />
                    </View>
                    <View style={styles.achievementInfo}>
                        <Text style={[styles.achievementTitle, { color: colors.text.primary }]}>
                            7 Day Streak!
                        </Text>
                        <Text style={[styles.achievementDesc, { color: colors.text.muted }]}>
                            You're on fire! Keep it up.
                        </Text>
                    </View>
                </GlassCard>
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
    headerTitle: {
        fontSize: 24,
        fontWeight: '700',
    },
    dropdown: {
        paddingHorizontal: 12,
        paddingVertical: 6,
    },
    dropdownText: {
        fontSize: 14,
        fontWeight: '500',
    },
    chartCard: {
        padding: 24,
        marginBottom: 16,
    },
    chartTitle: {
        fontSize: 12,
        fontWeight: '500',
        marginBottom: 24,
    },
    chartContainer: {
        alignItems: 'center',
    },
    metricsRow: {
        flexDirection: 'row',
        gap: 16,
        marginBottom: 16,
    },
    metricCard: {
        flex: 1,
        padding: 16,
    },
    metricHeader: {
        flexDirection: 'row',
        alignItems: 'center',
        gap: 8,
        marginBottom: 8,
    },
    metricLabel: {
        fontSize: 12,
        fontWeight: '700',
        color: '#EC4899',
    },
    metricValue: {
        fontSize: 24,
        fontWeight: '700',
    },
    metricUnit: {
        fontSize: 12,
        fontWeight: '400',
        opacity: 0.5,
    },
    progressTrack: {
        height: 6,
        backgroundColor: 'rgba(0,0,0,0.1)',
        borderRadius: 3,
        marginTop: 8,
        overflow: 'hidden',
    },
    progressFill: {
        height: '100%',
        borderRadius: 3,
    },
    metricHint: {
        fontSize: 10,
        marginTop: 8,
    },
    waterBars: {
        flexDirection: 'row',
        gap: 4,
        marginTop: 12,
    },
    waterBar: {
        flex: 1,
        height: 24,
        borderRadius: 2,
    },
    achievementCard: {
        flexDirection: 'row',
        alignItems: 'center',
        padding: 16,
        gap: 16,
        backgroundColor: 'rgba(251, 191, 36, 0.1)',
        borderColor: 'rgba(251, 191, 36, 0.2)',
    },
    achievementIcon: {
        width: 48,
        height: 48,
        borderRadius: 24,
        backgroundColor: '#F59E0B',
        alignItems: 'center',
        justifyContent: 'center',
    },
    achievementInfo: {
        flex: 1,
    },
    achievementTitle: {
        fontSize: 16,
        fontWeight: '700',
    },
    achievementDesc: {
        fontSize: 12,
        marginTop: 2,
    },
});

export default StatisticsScreen;
