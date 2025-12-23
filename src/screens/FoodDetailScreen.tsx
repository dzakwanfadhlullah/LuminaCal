/**
 * FoodDetailScreen - Food scan result and portion editing
 */

import React, { useState } from 'react';
import {
    StyleSheet,
    View,
    Text,
    ScrollView,
    Image,
    Pressable,
} from 'react-native';
import { ChevronLeft } from 'lucide-react-native';
import Slider from '@react-native-community/slider';
import { GlassCard, GlassButton } from '../components/ui';
import { SCAN_RESULTS } from '../constants/data';
import { COLORS } from '../constants/theme';

interface FoodDetailScreenProps {
    onBack: () => void;
    onAdd: (calories: number) => void;
    darkMode?: boolean;
}

export const FoodDetailScreen: React.FC<FoodDetailScreenProps> = ({
    onBack,
    onAdd,
    darkMode = false,
}) => {
    const [portion, setPortion] = useState(1);
    const colors = darkMode ? COLORS.dark : COLORS.light;
    const baseCals = SCAN_RESULTS.calories;
    const adjustedCals = Math.round(baseCals * portion);

    const macros = [
        { label: 'Protein', val: SCAN_RESULTS.macros.p, color: '#3B82F6', bg: 'rgba(59, 130, 246, 0.1)' },
        { label: 'Carbs', val: SCAN_RESULTS.macros.c, color: '#22C55E', bg: 'rgba(34, 197, 94, 0.1)' },
        { label: 'Fat', val: SCAN_RESULTS.macros.f, color: '#FB923C', bg: 'rgba(251, 146, 60, 0.1)' },
    ];

    return (
        <View style={[styles.container, { backgroundColor: colors.background }]}>
            {/* Hero Image */}
            <View style={styles.heroContainer}>
                <Image
                    source={{ uri: 'https://images.unsplash.com/photo-1550547660-d9450f859349?w=800&q=80' }}
                    style={styles.heroImage}
                />
                <View style={styles.heroOverlay} />
                <Pressable style={styles.backButton} onPress={onBack}>
                    <ChevronLeft size={24} color="#FFFFFF" />
                </Pressable>
            </View>

            {/* Content */}
            <ScrollView
                style={styles.scrollView}
                contentContainerStyle={styles.content}
                showsVerticalScrollIndicator={false}
            >
                <GlassCard style={styles.mainCard} darkMode={darkMode}>
                    <View style={styles.foodHeader}>
                        <View>
                            <Text style={[styles.foodName, { color: colors.text.primary }]}>
                                {SCAN_RESULTS.name}
                            </Text>
                            <Text style={[styles.foodMeta, { color: colors.text.muted }]}>
                                Lunch • High Protein
                            </Text>
                        </View>
                        <View style={styles.calorieDisplay}>
                            <Text style={styles.calorieValue}>{adjustedCals}</Text>
                            <Text style={styles.calorieUnit}>kcal</Text>
                        </View>
                    </View>

                    {/* Macros Grid */}
                    <View style={styles.macrosGrid}>
                        {macros.map((m, i) => (
                            <View key={i} style={[styles.macroBox, { backgroundColor: m.bg }]}>
                                <Text style={[styles.macroValue, { color: m.color }]}>
                                    {Math.round(m.val * portion)}g
                                </Text>
                                <Text style={[styles.macroLabel, { color: colors.text.muted }]}>
                                    {m.label}
                                </Text>
                            </View>
                        ))}
                    </View>
                </GlassCard>

                {/* Portion Slider */}
                <View style={styles.portionSection}>
                    <View style={styles.portionHeader}>
                        <Text style={[styles.sectionTitle, { color: colors.text.primary }]}>
                            Portion Size
                        </Text>
                        <Text style={styles.portionValue}>
                            {portion.toFixed(1)}x serving
                        </Text>
                    </View>

                    <Slider
                        style={styles.slider}
                        minimumValue={0.5}
                        maximumValue={2.0}
                        step={0.1}
                        value={portion}
                        onValueChange={setPortion}
                        minimumTrackTintColor="#3B82F6"
                        maximumTrackTintColor={darkMode ? 'rgba(255,255,255,0.1)' : 'rgba(0,0,0,0.1)'}
                        thumbTintColor="#3B82F6"
                    />
                </View>

                {/* Ingredients */}
                <View style={styles.ingredientsSection}>
                    <Text style={[styles.sectionTitle, { color: colors.text.primary }]}>
                        Detected Ingredients
                    </Text>
                    <View style={styles.ingredientsList}>
                        {SCAN_RESULTS.ingredients.map((ing, i) => (
                            <View
                                key={i}
                                style={[
                                    styles.ingredientChip,
                                    {
                                        backgroundColor: darkMode ? 'rgba(255,255,255,0.05)' : '#FFFFFF',
                                        borderColor: darkMode ? 'rgba(255,255,255,0.1)' : 'rgba(0,0,0,0.1)',
                                    },
                                ]}
                            >
                                <Text style={[styles.ingredientText, { color: colors.text.secondary }]}>
                                    {ing}
                                </Text>
                            </View>
                        ))}
                    </View>
                </View>
            </ScrollView>

            {/* Bottom CTA */}
            <View style={[styles.bottomCta, { backgroundColor: colors.background }]}>
                <GlassButton
                    variant="primary"
                    style={styles.addButton}
                    onPress={() => onAdd(adjustedCals)}
                    darkMode={darkMode}
                >
                    Add to Log
                </GlassButton>
            </View>
        </View>
    );
};

const styles = StyleSheet.create({
    container: {
        flex: 1,
    },
    heroContainer: {
        height: '40%',
        position: 'relative',
    },
    heroImage: {
        width: '100%',
        height: '100%',
    },
    heroOverlay: {
        ...StyleSheet.absoluteFillObject,
        backgroundColor: 'rgba(0,0,0,0.2)',
    },
    backButton: {
        position: 'absolute',
        top: 48,
        left: 24,
        width: 44,
        height: 44,
        borderRadius: 22,
        backgroundColor: 'rgba(0,0,0,0.3)',
        alignItems: 'center',
        justifyContent: 'center',
    },
    scrollView: {
        flex: 1,
        marginTop: -48,
    },
    content: {
        padding: 24,
        paddingBottom: 120,
    },
    mainCard: {
        padding: 24,
        marginBottom: 24,
    },
    foodHeader: {
        flexDirection: 'row',
        justifyContent: 'space-between',
        alignItems: 'flex-start',
        marginBottom: 24,
    },
    foodName: {
        fontSize: 24,
        fontWeight: '700',
    },
    foodMeta: {
        fontSize: 14,
        marginTop: 4,
    },
    calorieDisplay: {
        alignItems: 'flex-end',
    },
    calorieValue: {
        fontSize: 32,
        fontWeight: '700',
        color: '#FB923C',
    },
    calorieUnit: {
        fontSize: 10,
        fontWeight: '700',
        textTransform: 'uppercase',
        color: '#94A3B8',
        letterSpacing: 1,
    },
    macrosGrid: {
        flexDirection: 'row',
        gap: 16,
    },
    macroBox: {
        flex: 1,
        padding: 12,
        borderRadius: 16,
        alignItems: 'center',
    },
    macroValue: {
        fontSize: 18,
        fontWeight: '700',
    },
    macroLabel: {
        fontSize: 10,
        textTransform: 'uppercase',
        marginTop: 4,
    },
    portionSection: {
        marginBottom: 32,
    },
    portionHeader: {
        flexDirection: 'row',
        justifyContent: 'space-between',
        alignItems: 'center',
        marginBottom: 16,
    },
    sectionTitle: {
        fontSize: 16,
        fontWeight: '600',
    },
    portionValue: {
        fontSize: 14,
        fontWeight: '700',
        color: '#3B82F6',
    },
    slider: {
        width: '100%',
        height: 40,
    },
    ingredientsSection: {
        marginBottom: 24,
    },
    ingredientsList: {
        flexDirection: 'row',
        flexWrap: 'wrap',
        gap: 8,
        marginTop: 12,
    },
    ingredientChip: {
        paddingHorizontal: 12,
        paddingVertical: 8,
        borderRadius: 8,
        borderWidth: 1,
    },
    ingredientText: {
        fontSize: 14,
    },
    bottomCta: {
        position: 'absolute',
        bottom: 0,
        left: 0,
        right: 0,
        padding: 24,
        paddingBottom: 40,
    },
    addButton: {
        paddingVertical: 16,
    },
});

export default FoodDetailScreen;
