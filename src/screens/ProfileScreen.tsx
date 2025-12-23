/**
 * ProfileScreen - User profile and settings
 */

import React from 'react';
import {
    StyleSheet,
    View,
    Text,
    ScrollView,
    Image,
    Pressable,
} from 'react-native';
import { SafeAreaView } from 'react-native-safe-area-context';
import {
    Settings,
    Target,
    Smartphone,
    User,
    Zap,
    ChevronRight,
} from 'lucide-react-native';
import { GlassCard, GlassButton } from '../components/ui';
import { COLORS, APP_CONFIG } from '../constants/theme';

interface ProfileScreenProps {
    darkMode: boolean;
    onToggleDarkMode: () => void;
}

export const ProfileScreen: React.FC<ProfileScreenProps> = ({
    darkMode,
    onToggleDarkMode,
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
                        Profile
                    </Text>
                    <Pressable style={styles.settingsButton}>
                        <Settings size={20} color={colors.text.muted} />
                    </Pressable>
                </View>

                {/* Profile Card */}
                <View style={styles.profileSection}>
                    <View style={styles.avatarWrapper}>
                        <View style={styles.avatarRing}>
                            <Image
                                source={{ uri: 'https://api.dicebear.com/7.x/avataaars/svg?seed=Felix' }}
                                style={styles.avatar}
                            />
                        </View>
                        <View style={styles.editBadge}>
                            <Text style={styles.editEmoji}>✏️</Text>
                        </View>
                    </View>
                    <Text style={[styles.userName, { color: colors.text.primary }]}>
                        Alex Morgan
                    </Text>
                    <Text style={[styles.userStatus, { color: colors.text.muted }]}>
                        Premium Member
                    </Text>
                </View>

                {/* Settings Groups */}
                <GlassCard style={styles.settingsCard} darkMode={darkMode}>
                    {/* Goals */}
                    <Pressable style={styles.settingsRow}>
                        <View style={[styles.iconBox, { backgroundColor: 'rgba(251, 146, 60, 0.1)' }]}>
                            <Target size={18} color="#FB923C" />
                        </View>
                        <Text style={[styles.settingsLabel, { color: colors.text.primary }]}>
                            Goals
                        </Text>
                        <ChevronRight size={16} color={colors.text.muted} />
                    </Pressable>

                    <View style={[styles.divider, { backgroundColor: darkMode ? 'rgba(255,255,255,0.1)' : 'rgba(0,0,0,0.05)' }]} />

                    {/* Devices */}
                    <Pressable style={styles.settingsRow}>
                        <View style={[styles.iconBox, { backgroundColor: 'rgba(34, 197, 94, 0.1)' }]}>
                            <Smartphone size={18} color="#22C55E" />
                        </View>
                        <Text style={[styles.settingsLabel, { color: colors.text.primary }]}>
                            Devices & Apps
                        </Text>
                        <Text style={styles.connectedBadge}>2 Connected</Text>
                    </Pressable>
                </GlassCard>

                <Text style={[styles.sectionTitle, { color: colors.text.muted }]}>
                    PREFERENCES
                </Text>

                <GlassCard style={styles.settingsCard} darkMode={darkMode}>
                    {/* Dietary Needs */}
                    <Pressable style={styles.settingsRow}>
                        <View style={[styles.iconBox, { backgroundColor: 'rgba(147, 51, 234, 0.1)' }]}>
                            <User size={18} color="#9333EA" />
                        </View>
                        <Text style={[styles.settingsLabel, { color: colors.text.primary }]}>
                            Dietary Needs
                        </Text>
                        <ChevronRight size={16} color={colors.text.muted} />
                    </Pressable>

                    <View style={[styles.divider, { backgroundColor: darkMode ? 'rgba(255,255,255,0.1)' : 'rgba(0,0,0,0.05)' }]} />

                    {/* Dark Mode Toggle */}
                    <Pressable style={styles.settingsRow} onPress={onToggleDarkMode}>
                        <View style={[styles.iconBox, { backgroundColor: darkMode ? 'rgba(255,255,255,0.1)' : 'rgba(0,0,0,0.05)' }]}>
                            <Zap size={18} color={colors.text.muted} />
                        </View>
                        <Text style={[styles.settingsLabel, { color: colors.text.primary }]}>
                            Dark Mode
                        </Text>
                        <View style={[styles.toggle, { backgroundColor: darkMode ? '#3B82F6' : '#CBD5E1' }]}>
                            <View style={[styles.toggleThumb, { left: darkMode ? 24 : 4 }]} />
                        </View>
                    </Pressable>
                </GlassCard>

                {/* Sign Out */}
                <GlassButton
                    variant="ghost"
                    style={styles.signOutButton}
                    textStyle={styles.signOutText}
                    darkMode={darkMode}
                >
                    Sign Out
                </GlassButton>

                <Text style={[styles.versionText, { color: colors.text.muted }]}>
                    Version {APP_CONFIG.version} (Build {APP_CONFIG.buildNumber})
                </Text>
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
        marginBottom: 32,
    },
    headerTitle: {
        fontSize: 24,
        fontWeight: '700',
    },
    settingsButton: {
        padding: 8,
        borderRadius: 12,
    },
    profileSection: {
        alignItems: 'center',
        marginBottom: 32,
    },
    avatarWrapper: {
        position: 'relative',
        marginBottom: 16,
    },
    avatarRing: {
        width: 96,
        height: 96,
        borderRadius: 48,
        borderWidth: 4,
        borderColor: '#60A5FA',
        padding: 6,
    },
    avatar: {
        width: '100%',
        height: '100%',
        borderRadius: 40,
    },
    editBadge: {
        position: 'absolute',
        bottom: 0,
        right: 0,
        width: 32,
        height: 32,
        borderRadius: 16,
        backgroundColor: '#FFFFFF',
        alignItems: 'center',
        justifyContent: 'center',
        shadowColor: '#000',
        shadowOffset: { width: 0, height: 2 },
        shadowOpacity: 0.1,
        shadowRadius: 4,
        elevation: 2,
    },
    editEmoji: {
        fontSize: 14,
    },
    userName: {
        fontSize: 20,
        fontWeight: '700',
    },
    userStatus: {
        fontSize: 14,
        marginTop: 4,
    },
    settingsCard: {
        padding: 0,
        overflow: 'hidden',
        marginBottom: 16,
    },
    settingsRow: {
        flexDirection: 'row',
        alignItems: 'center',
        padding: 16,
        gap: 12,
    },
    iconBox: {
        width: 36,
        height: 36,
        borderRadius: 12,
        alignItems: 'center',
        justifyContent: 'center',
    },
    settingsLabel: {
        flex: 1,
        fontSize: 16,
        fontWeight: '500',
    },
    connectedBadge: {
        fontSize: 12,
        fontWeight: '500',
        color: '#22C55E',
    },
    divider: {
        height: 1,
        marginHorizontal: 16,
    },
    sectionTitle: {
        fontSize: 10,
        fontWeight: '700',
        letterSpacing: 1,
        marginLeft: 8,
        marginTop: 8,
        marginBottom: 8,
    },
    toggle: {
        width: 44,
        height: 24,
        borderRadius: 12,
        position: 'relative',
    },
    toggleThumb: {
        position: 'absolute',
        top: 4,
        width: 16,
        height: 16,
        borderRadius: 8,
        backgroundColor: '#FFFFFF',
    },
    signOutButton: {
        marginTop: 16,
    },
    signOutText: {
        color: '#EF4444',
    },
    versionText: {
        textAlign: 'center',
        fontSize: 10,
        marginTop: 16,
    },
});

export default ProfileScreen;
