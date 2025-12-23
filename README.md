# LuminaCal

A React Native calorie tracking app with a premium glassmorphism design, powered by Skia for smooth visual effects.

## Tech Stack

- **Framework**: React Native + Expo SDK 51
- **Graphics**: @shopify/react-native-skia (Glassmorphism & Mesh Background)
- **Animations**: react-native-reanimated v3
- **Styling**: NativeWind v4 (Tailwind for React Native)
- **Icons**: lucide-react-native

## Getting Started

```bash
# Install dependencies
npm install --legacy-peer-deps

# Generate native projects
npx expo prebuild

# Run on Android
npx expo run:android

# Run on iOS
npx expo run:ios
```

## Project Structure

```
/src
├── components/
│   ├── ui/          # GlassCard, GlassButton, MeshBackground
│   └── visuals/     # AppleRing, StatisticsChart, AnimatedNumber
├── constants/       # theme, types, data
├── hooks/           # useAppLogic
├── navigation/      # AppNavigator
├── screens/         # All 6 screens
└── utils/           # helpers
```

## Features

- 🔥 Apple Watch-style activity rings
- 📊 Animated statistics charts
- 📸 AI food scanner UI (Vision Camera scaffold)
- 🌓 Dark/Light mode support
- ✨ Glassmorphism blur effects with Skia
