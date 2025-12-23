/**
 * ExploreScreen - Recipe discovery and search
 */

import React, { useState } from 'react';
import {
    StyleSheet,
    View,
    Text,
    ScrollView,
    TextInput,
    Pressable,
} from 'react-native';
import { SafeAreaView } from 'react-native-safe-area-context';
import { Search, Plus } from 'lucide-react-native';
import { GlassCard } from '../components/ui';
import { MOCK_RECIPES, EXPLORE_CATEGORIES } from '../constants/data';
import { COLORS } from '../constants/theme';

interface ExploreScreenProps {
    darkMode?: boolean;
}

export const ExploreScreen: React.FC<ExploreScreenProps> = ({
    darkMode = false,
}) => {
    const [selectedCategory, setSelectedCategory] = useState(0);
    const colors = darkMode ? COLORS.dark : COLORS.light;

    return (
        <SafeAreaView style={styles.container} edges={['top']}>
            <ScrollView
                style={styles.scrollView}
                contentContainerStyle={styles.content}
                showsVerticalScrollIndicator={false}
                stickyHeaderIndices={[0]}
            >
                {/* Search Header */}
                <View style={[styles.searchHeader, { backgroundColor: darkMode ? 'rgba(2,6,23,0.8)' : 'rgba(248,250,252,0.8)' }]}>
                    <Text style={[styles.headerTitle, { color: colors.text.primary }]}>
                        Explore
                    </Text>
                    <View style={[styles.searchBar, { backgroundColor: darkMode ? COLORS.dark.surface : '#FFFFFF', borderColor: darkMode ? 'rgba(255,255,255,0.1)' : 'rgba(0,0,0,0.1)' }]}>
                        <Search size={18} color={colors.text.muted} />
                        <TextInput
                            placeholder="Search recipes, ingredients..."
                            placeholderTextColor={colors.text.muted}
                            style={[styles.searchInput, { color: colors.text.primary }]}
                        />
                    </View>
                </View>

                {/* Categories */}
                <ScrollView
                    horizontal
                    showsHorizontalScrollIndicator={false}
                    style={styles.categoriesScroll}
                    contentContainerStyle={styles.categoriesContent}
                >
                    {EXPLORE_CATEGORIES.map((cat, i) => (
                        <Pressable
                            key={i}
                            onPress={() => setSelectedCategory(i)}
                            style={[
                                styles.categoryChip,
                                i === selectedCategory
                                    ? styles.categoryChipActive
                                    : [styles.categoryChipInactive, { backgroundColor: darkMode ? COLORS.dark.surface : '#FFFFFF', borderColor: darkMode ? 'rgba(255,255,255,0.1)' : 'rgba(0,0,0,0.1)' }],
                            ]}
                        >
                            <Text
                                style={[
                                    styles.categoryText,
                                    { color: i === selectedCategory ? '#FFFFFF' : colors.text.secondary },
                                ]}
                            >
                                {cat}
                            </Text>
                        </Pressable>
                    ))}
                </ScrollView>

                {/* Recipe Grid */}
                <View style={styles.recipeGrid}>
                    {MOCK_RECIPES.map((recipe) => (
                        <GlassCard key={recipe.id} style={styles.recipeCard} darkMode={darkMode}>
                            <View style={styles.recipeImage}>
                                <Text style={styles.recipeEmoji}>{recipe.image}</Text>
                                <View style={styles.recipeTime}>
                                    <Text style={styles.recipeTimeText}>{recipe.time}</Text>
                                </View>
                            </View>
                            <View style={styles.recipeInfo}>
                                <Text style={styles.recipeTag}>{recipe.tag}</Text>
                                <Text style={[styles.recipeTitle, { color: colors.text.primary }]} numberOfLines={2}>
                                    {recipe.title}
                                </Text>
                                <View style={styles.recipeFooter}>
                                    <Text style={[styles.recipeCals, { color: colors.text.muted }]}>
                                        {recipe.cals} kcal
                                    </Text>
                                    <View style={styles.addButton}>
                                        <Plus size={12} color={colors.text.muted} />
                                    </View>
                                </View>
                            </View>
                        </GlassCard>
                    ))}
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
        paddingBottom: 100,
    },
    searchHeader: {
        paddingHorizontal: 16,
        paddingTop: 16,
        paddingBottom: 16,
    },
    headerTitle: {
        fontSize: 24,
        fontWeight: '700',
        marginBottom: 16,
    },
    searchBar: {
        flexDirection: 'row',
        alignItems: 'center',
        paddingHorizontal: 12,
        paddingVertical: 12,
        borderRadius: 16,
        borderWidth: 1,
        gap: 8,
    },
    searchInput: {
        flex: 1,
        fontSize: 14,
    },
    categoriesScroll: {
        marginBottom: 16,
    },
    categoriesContent: {
        paddingHorizontal: 16,
        gap: 8,
    },
    categoryChip: {
        paddingHorizontal: 16,
        paddingVertical: 8,
        borderRadius: 12,
        marginRight: 8,
    },
    categoryChipActive: {
        backgroundColor: '#1E293B',
    },
    categoryChipInactive: {
        borderWidth: 1,
    },
    categoryText: {
        fontSize: 14,
        fontWeight: '500',
    },
    recipeGrid: {
        flexDirection: 'row',
        flexWrap: 'wrap',
        paddingHorizontal: 16,
        gap: 16,
    },
    recipeCard: {
        width: '47%',
        overflow: 'hidden',
        padding: 0,
    },
    recipeImage: {
        height: 100,
        backgroundColor: 'rgba(0,0,0,0.05)',
        alignItems: 'center',
        justifyContent: 'center',
        position: 'relative',
    },
    recipeEmoji: {
        fontSize: 40,
    },
    recipeTime: {
        position: 'absolute',
        top: 8,
        right: 8,
        paddingHorizontal: 8,
        paddingVertical: 4,
        borderRadius: 8,
        backgroundColor: 'rgba(0,0,0,0.5)',
    },
    recipeTimeText: {
        fontSize: 10,
        fontWeight: '500',
        color: '#FFFFFF',
    },
    recipeInfo: {
        padding: 12,
    },
    recipeTag: {
        fontSize: 10,
        fontWeight: '700',
        color: '#3B82F6',
        textTransform: 'uppercase',
        letterSpacing: 1,
        marginBottom: 4,
    },
    recipeTitle: {
        fontSize: 14,
        fontWeight: '700',
        lineHeight: 18,
        marginBottom: 8,
    },
    recipeFooter: {
        flexDirection: 'row',
        justifyContent: 'space-between',
        alignItems: 'center',
    },
    recipeCals: {
        fontSize: 12,
    },
    addButton: {
        width: 20,
        height: 20,
        borderRadius: 10,
        backgroundColor: 'rgba(0,0,0,0.05)',
        alignItems: 'center',
        justifyContent: 'center',
    },
});

export default ExploreScreen;
