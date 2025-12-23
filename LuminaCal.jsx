import React, { useState, useEffect, useRef, useMemo } from 'react';
import {
    Home,
    Camera,
    PieChart,
    Compass,
    User,
    Plus,
    ChevronLeft,
    ScanLine,
    Flame,
    Droplet,
    Zap,
    Search,
    Settings,
    Bell,
    Share2,
    Target,
    ChevronRight,
    ArrowUpRight,
    TrendingUp,
    Award,
    Calendar,
    MoreHorizontal,
    X,
    Check,
    Smartphone,
    Info
} from 'lucide-react';

/**
 * -----------------------------------------------------------------------
 * DATA & CONFIGURATION
 * -----------------------------------------------------------------------
 */

const APP_CONFIG = {
    name: 'LuminaCal',
    version: '1.0.0',
    theme: {
        light: {
            bg: 'bg-slate-50',
            text: 'text-slate-800',
            subtext: 'text-slate-500',
            glass: 'bg-white/40 border-white/40 shadow-xl shadow-slate-200/50',
            glassHover: 'hover:bg-white/50',
            accent: 'text-blue-500',
        },
        dark: {
            bg: 'bg-slate-950',
            text: 'text-slate-100',
            subtext: 'text-slate-400',
            glass: 'bg-slate-900/40 border-white/10 shadow-2xl shadow-black/50',
            glassHover: 'hover:bg-slate-800/40',
            accent: 'text-blue-400',
        }
    }
};

const MOCK_HISTORY = [
    { id: 1, name: 'Oatmeal & Berries', time: '08:30 AM', calories: 320, protein: 12, carbs: 45, fat: 6, type: 'breakfast' },
    { id: 2, name: 'Iced Americano', time: '10:15 AM', calories: 15, protein: 0, carbs: 3, fat: 0, type: 'snack' },
    { id: 3, name: 'Grilled Salmon Bowl', time: '01:00 PM', calories: 540, protein: 42, carbs: 35, fat: 22, type: 'lunch' },
];

const MOCK_RECIPES = [
    { id: 101, title: 'Avocado Toast Deluxe', cals: 340, time: '10m', image: '🥑', tag: 'Breakfast' },
    { id: 102, title: 'Quinoa Power Salad', cals: 420, time: '20m', image: '🥗', tag: 'Vegan' },
    { id: 103, title: 'Berry Smoothie Bowl', cals: 280, time: '5m', image: '🫐', tag: 'Snack' },
    { id: 104, title: 'Grilled Chicken Pesto', cals: 550, time: '35m', image: '🍗', tag: 'High Protein' },
    { id: 105, title: 'Zucchini Noodles', cals: 180, time: '15m', image: '🥒', tag: 'Keto' },
    { id: 106, title: 'Mango Chia Pudding', cals: 220, time: '1h', image: '🥭', tag: 'Dessert' },
];

const SCAN_RESULTS = {
    name: 'Caesar Salad',
    confidence: 0.98,
    calories: 350,
    macros: { p: 12, c: 18, f: 26 },
    ingredients: ['Romaine Lettuce', 'Grilled Chicken', 'Parmesan', 'Croutons', 'Caesar Dressing']
};

/**
 * -----------------------------------------------------------------------
 * UTILITIES
 * -----------------------------------------------------------------------
 */

const calculateProgress = (current, target) => Math.min(100, (current / target) * 100);

// Smooth cubic bezier for charts
const getBezierPath = (points, height, width) => {
    if (points.length === 0) return "";

    const scaleX = (index) => (index / (points.length - 1)) * width;
    const scaleY = (value) => height - (value * height);

    let path = `M ${scaleX(0)} ${scaleY(points[0])}`;

    for (let i = 0; i < points.length - 1; i++) {
        const x0 = scaleX(i);
        const y0 = scaleY(points[i]);
        const x1 = scaleX(i + 1);
        const y1 = scaleY(points[i + 1]);

        // Control points
        const cp1x = x0 + (x1 - x0) * 0.5;
        const cp1y = y0;
        const cp2x = x1 - (x1 - x0) * 0.5;
        const cp2y = y1;

        path += ` C ${cp1x} ${cp1y}, ${cp2x} ${cp2y}, ${x1} ${y1}`;
    }

    return path;
};

/**
 * -----------------------------------------------------------------------
 * UI PRIMITIVES (Glassmorphism System)
 * -----------------------------------------------------------------------
 */

const GlassCard = ({ children, className = "", onClick, active = false, delay = 0 }) => (
    <div
        onClick={onClick}
        style={{ animationDelay: `${delay}ms` }}
        className={`
      relative overflow-hidden backdrop-blur-xl transition-all duration-300 ease-out
      rounded-[2rem] border
      ${active ? 'bg-white/20 border-white/50 shadow-blue-500/20' : 'bg-white/10 border-white/20 hover:bg-white/15'}
      shadow-xl hover:shadow-2xl hover:scale-[1.02]
      dark:bg-slate-900/60 dark:border-white/10 dark:hover:bg-slate-800/60
      animate-fade-in-up
      ${className}
    `}
    >
        {/* Shine effect */}
        <div className="absolute inset-0 bg-gradient-to-br from-white/10 to-transparent opacity-0 hover:opacity-100 transition-opacity duration-500 pointer-events-none" />
        {children}
    </div>
);

const GlassButton = ({ children, variant = "primary", className = "", onClick, icon: Icon }) => {
    const variants = {
        primary: "bg-gradient-to-r from-slate-800 to-slate-900 text-white dark:from-white dark:to-slate-200 dark:text-slate-900 shadow-lg shadow-slate-900/20",
        secondary: "bg-white/20 text-slate-800 dark:text-white hover:bg-white/30 border border-white/20",
        accent: "bg-gradient-to-r from-orange-400 to-pink-500 text-white shadow-orange-500/30",
        ghost: "bg-transparent text-slate-600 dark:text-slate-400 hover:bg-black/5 dark:hover:bg-white/5"
    };

    return (
        <button
            onClick={onClick}
            className={`
        px-6 py-3 rounded-2xl font-medium text-sm sm:text-base
        transform transition-all duration-200 active:scale-95
        flex items-center justify-center gap-2 backdrop-blur-md
        ${variants[variant]}
        ${className}
      `}
        >
            {Icon && <Icon size={18} />}
            {children}
        </button>
    );
};

const AnimatedNumber = ({ value, duration = 1000 }) => {
    const [displayValue, setDisplayValue] = useState(0);

    useEffect(() => {
        let startTime;
        const startValue = displayValue;
        const change = value - startValue;

        const animate = (currentTime) => {
            if (!startTime) startTime = currentTime;
            const progress = Math.min((currentTime - startTime) / duration, 1);

            // Easing function (easeOutQuart)
            const ease = 1 - Math.pow(1 - progress, 4);

            setDisplayValue(Math.floor(startValue + change * ease));

            if (progress < 1) {
                requestAnimationFrame(animate);
            }
        };

        requestAnimationFrame(animate);
    }, [value]);

    return <span>{displayValue.toLocaleString()}</span>;
};

/**
 * -----------------------------------------------------------------------
 * COMPLEX VISUAL COMPONENTS
 * -----------------------------------------------------------------------
 */

const AppleRing = ({ size = 200, stroke = 15, progress = 75, color = "#FFB88C", icon: Icon, label, subLabel }) => {
    const center = size / 2;
    const radius = center - stroke;
    const circumference = 2 * Math.PI * radius;
    const offset = circumference - (progress / 100) * circumference;

    return (
        <div className="relative flex flex-col items-center justify-center">
            <div className="relative" style={{ width: size, height: size }}>
                {/* Background Circle */}
                <svg className="transform -rotate-90 w-full h-full drop-shadow-2xl">
                    <circle
                        cx={center}
                        cy={center}
                        r={radius}
                        fill="none"
                        stroke="currentColor"
                        strokeWidth={stroke}
                        className="text-black/5 dark:text-white/5"
                    />
                    {/* Progress Circle */}
                    <circle
                        cx={center}
                        cy={center}
                        r={radius}
                        fill="none"
                        stroke={color}
                        strokeWidth={stroke}
                        strokeDasharray={circumference}
                        strokeDashoffset={offset}
                        strokeLinecap="round"
                        className="transition-all duration-1000 ease-out"
                    />
                </svg>

                {/* Inner Content */}
                <div className="absolute inset-0 flex flex-col items-center justify-center">
                    {Icon && <Icon size={24} className="mb-1 opacity-80" style={{ color }} />}
                    <div className="text-3xl font-bold tracking-tighter dark:text-white">
                        {typeof label === 'number' ? <AnimatedNumber value={label} /> : label}
                    </div>
                    <div className="text-xs font-medium uppercase tracking-widest opacity-50 dark:text-white">
                        {subLabel}
                    </div>
                </div>
            </div>
        </div>
    );
};

const MeshBackground = ({ darkMode }) => (
    <div className="fixed inset-0 -z-10 overflow-hidden transition-colors duration-700">
        <div className={`absolute inset-0 ${darkMode ? 'bg-slate-950' : 'bg-slate-50'}`} />

        {/* Animated Blobs */}
        <div className={`
      absolute top-[-10%] left-[-10%] w-[50%] h-[50%] rounded-full blur-[80px] opacity-60 animate-blob
      ${darkMode ? 'bg-purple-900/40' : 'bg-orange-200'}
    `} />
        <div className={`
      absolute top-[20%] right-[-10%] w-[60%] h-[60%] rounded-full blur-[100px] opacity-60 animate-blob animation-delay-2000
      ${darkMode ? 'bg-blue-900/40' : 'bg-sky-200'}
    `} />
        <div className={`
      absolute bottom-[-20%] left-[20%] w-[50%] h-[50%] rounded-full blur-[80px] opacity-60 animate-blob animation-delay-4000
      ${darkMode ? 'bg-pink-900/40' : 'bg-pink-200'}
    `} />

        {/* Noise Texture Overlay */}
        <div className="absolute inset-0 opacity-[0.03] pointer-events-none mix-blend-overlay"
            style={{ backgroundImage: `url("data:image/svg+xml,%3Csvg viewBox='0 0 200 200' xmlns='http://www.w3.org/2000/svg'%3E%3Cfilter id='noiseFilter'%3E%3CfeTurbulence type='fractalNoise' baseFrequency='0.65' numOctaves='3' stitchTiles='stitch'/%3E%3C/filter%3E%3Crect width='100%25' height='100%25' filter='url(%23noiseFilter)'/%3E%3C/svg%3E")` }}
        />
    </div>
);

/**
 * -----------------------------------------------------------------------
 * SCREENS
 * -----------------------------------------------------------------------
 */

// 1. HOME DASHBOARD
const Dashboard = ({ onCameraOpen, history, calories, macros, dailyGoal }) => {
    return (
        <div className="space-y-6 pb-24 pt-4 px-4">
            {/* Header */}
            <div className="flex justify-between items-center px-2">
                <div>
                    <h2 className="text-sm font-medium opacity-60 uppercase tracking-widest">Today</h2>
                    <h1 className="text-2xl font-bold dark:text-white">Dashboard</h1>
                </div>
                <div className="w-10 h-10 rounded-full bg-gradient-to-tr from-orange-400 to-pink-500 p-0.5">
                    <img
                        src="https://api.dicebear.com/7.x/avataaars/svg?seed=Felix"
                        alt="User"
                        className="w-full h-full rounded-full bg-white border-2 border-white dark:border-slate-800"
                    />
                </div>
            </div>

            {/* Main Ring Card */}
            <GlassCard className="p-8 flex flex-col items-center relative overflow-visible">
                <div className="absolute top-4 right-4">
                    <div className="flex items-center gap-1 text-xs font-medium bg-green-500/10 text-green-600 px-2 py-1 rounded-full border border-green-500/20">
                        <TrendingUp size={12} />
                        <span>On Track</span>
                    </div>
                </div>

                <AppleRing
                    size={240}
                    stroke={24}
                    progress={calculateProgress(calories.consumed, dailyGoal)}
                    label={dailyGoal - calories.consumed}
                    subLabel="Remaining"
                    color="#FFB88C"
                    icon={Flame}
                />

                {/* Macros Breakdown */}
                <div className="grid grid-cols-3 gap-4 w-full mt-8">
                    {[
                        { label: 'Protein', val: macros.protein, max: 150, color: 'bg-blue-400' },
                        { label: 'Carbs', val: macros.carbs, max: 200, color: 'bg-green-400' },
                        { label: 'Fat', val: macros.fat, max: 70, color: 'bg-orange-400' }
                    ].map((m, i) => (
                        <div key={i} className="flex flex-col gap-2">
                            <div className="flex justify-between text-xs opacity-70">
                                <span>{m.label}</span>
                                <span>{m.val}g</span>
                            </div>
                            <div className="h-2 w-full bg-black/5 dark:bg-white/10 rounded-full overflow-hidden">
                                <div
                                    className={`h-full ${m.color} rounded-full transition-all duration-1000`}
                                    style={{ width: `${(m.val / m.max) * 100}%` }}
                                />
                            </div>
                        </div>
                    ))}
                </div>
            </GlassCard>

            {/* Timeline */}
            <div className="space-y-4">
                <div className="flex justify-between items-center px-2">
                    <h3 className="font-semibold dark:text-white">Recent Logs</h3>
                    <button className="text-xs text-blue-500 font-medium">View All</button>
                </div>

                <div className="relative pl-4 space-y-4">
                    {/* Vertical Line */}
                    <div className="absolute left-4 top-2 bottom-2 w-0.5 bg-gradient-to-b from-blue-500/50 to-transparent" />

                    {history.map((item, idx) => (
                        <GlassCard key={idx} delay={idx * 100} className="ml-4 p-4 flex items-center gap-4 cursor-pointer hover:translate-x-1">
                            <div className="w-10 h-10 rounded-2xl bg-gradient-to-br from-blue-100 to-indigo-100 dark:from-blue-900 dark:to-indigo-900 flex items-center justify-center text-xl shadow-inner">
                                {item.type === 'breakfast' ? '🍳' : item.type === 'lunch' ? '🥗' : item.type === 'snack' ? '🍎' : '🍽️'}
                            </div>
                            <div className="flex-1">
                                <h4 className="font-medium text-sm dark:text-white">{item.name}</h4>
                                <p className="text-xs opacity-50">{item.time} • {item.type}</p>
                            </div>
                            <div className="flex flex-col items-end">
                                <span className="font-bold text-sm dark:text-white">{item.calories}</span>
                                <span className="text-[10px] opacity-50">kcal</span>
                            </div>
                        </GlassCard>
                    ))}
                </div>
            </div>
        </div>
    );
};

// 2. AI CAMERA SCANNER
const CameraScanner = ({ onClose, onScanComplete }) => {
    const [scanning, setScanning] = useState(true);
    const [found, setFound] = useState(false);

    useEffect(() => {
        // Simulate finding food
        const timer = setTimeout(() => {
            setFound(true);
        }, 2500);
        return () => clearTimeout(timer);
    }, []);

    return (
        <div className="fixed inset-0 z-50 bg-black flex flex-col">
            {/* Mock Camera Viewfinder */}
            <div className="relative flex-1 bg-slate-900 overflow-hidden">
                {/* Placeholder for Video Feed */}
                <div className="absolute inset-0 bg-[url('https://images.unsplash.com/photo-1550547660-d9450f859349?w=800&q=80')] bg-cover bg-center opacity-80" />

                <div className="absolute top-0 left-0 right-0 p-6 flex justify-between items-start z-10">
                    <button onClick={onClose} className="p-3 rounded-full bg-black/40 backdrop-blur-md text-white hover:bg-black/60 transition-colors">
                        <X size={24} />
                    </button>
                    <div className="px-4 py-1 rounded-full bg-black/40 backdrop-blur-md text-white text-xs font-medium border border-white/10">
                        AI Mode Active
                    </div>
                    <button className="p-3 rounded-full bg-black/40 backdrop-blur-md text-white hover:bg-black/60">
                        <Zap size={24} />
                    </button>
                </div>

                {/* Scanning Overlay */}
                <div className="absolute inset-0 flex items-center justify-center pointer-events-none">
                    <div className={`
            w-64 h-64 border-2 rounded-[2rem] relative transition-all duration-500
            ${found ? 'border-green-400 scale-100' : 'border-white/50 scale-105 animate-pulse'}
          `}>
                        {/* Corners */}
                        <div className="absolute top-0 left-0 w-6 h-6 border-t-4 border-l-4 border-white -mt-1 -ml-1 rounded-tl-xl" />
                        <div className="absolute top-0 right-0 w-6 h-6 border-t-4 border-r-4 border-white -mt-1 -mr-1 rounded-tr-xl" />
                        <div className="absolute bottom-0 left-0 w-6 h-6 border-b-4 border-l-4 border-white -mb-1 -ml-1 rounded-bl-xl" />
                        <div className="absolute bottom-0 right-0 w-6 h-6 border-b-4 border-r-4 border-white -mb-1 -mr-1 rounded-br-xl" />

                        {/* Scanning Line */}
                        {!found && (
                            <div className="absolute top-0 left-0 right-0 h-1 bg-blue-400 shadow-[0_0_15px_rgba(96,165,250,0.8)] animate-scan" />
                        )}

                        {/* Found Tag */}
                        {found && (
                            <div className="absolute -top-16 left-1/2 transform -translate-x-1/2 animate-pop-in">
                                <GlassCard className="px-4 py-2 flex items-center gap-2 !bg-white/90 dark:!bg-slate-900/90 !rounded-xl !border-green-400/50">
                                    <div className="w-2 h-2 rounded-full bg-green-500 animate-pulse" />
                                    <div>
                                        <p className="text-xs font-bold text-slate-900 dark:text-white whitespace-nowrap">Caesar Salad</p>
                                        <p className="text-[10px] text-slate-500 font-medium">~350 kcal</p>
                                    </div>
                                </GlassCard>
                            </div>
                        )}
                    </div>
                </div>

                {/* AR Bubbles (Background details) */}
                {found && (
                    <>
                        <div className="absolute top-1/3 left-1/4 animate-fade-in-up delay-100">
                            <div className="px-3 py-1 rounded-lg bg-black/60 backdrop-blur-md text-white text-[10px] border border-white/10">Lettuce</div>
                        </div>
                        <div className="absolute bottom-1/3 right-1/4 animate-fade-in-up delay-200">
                            <div className="px-3 py-1 rounded-lg bg-black/60 backdrop-blur-md text-white text-[10px] border border-white/10">Chicken</div>
                        </div>
                    </>
                )}
            </div>

            {/* Controls */}
            <div className="h-48 bg-black flex flex-col items-center justify-center relative">
                <div className="absolute -top-6 left-1/2 transform -translate-x-1/2">
                    <p className="text-white/80 text-sm font-medium drop-shadow-md">
                        {found ? 'Tap shutter to analyze' : 'Point camera at food'}
                    </p>
                </div>

                <div className="flex items-center gap-12">
                    <button className="w-12 h-12 rounded-full bg-slate-800 border border-white/10 flex items-center justify-center text-white/50 hover:bg-slate-700">
                        <ImageIcon size={20} />
                    </button>

                    <button
                        onClick={onScanComplete}
                        className={`
              w-20 h-20 rounded-full border-4 flex items-center justify-center transition-all duration-300
              ${found ? 'border-green-400 bg-white/10 scale-110' : 'border-white bg-transparent hover:bg-white/5'}
            `}
                    >
                        <div className={`w-16 h-16 rounded-full bg-white transition-all duration-200 ${found ? 'scale-90' : 'scale-100'}`} />
                    </button>

                    <button className="w-12 h-12 rounded-full bg-slate-800 border border-white/10 flex items-center justify-center text-white/50 hover:bg-slate-700">
                        <Settings size={20} />
                    </button>
                </div>
            </div>
        </div>
    );
};

const ImageIcon = ({ size }) => (
    <svg width={size} height={size} viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
        <rect x="3" y="3" width="18" height="18" rx="2" ry="2" />
        <circle cx="8.5" cy="8.5" r="1.5" />
        <polyline points="21 15 16 10 5 21" />
    </svg>
);


// 3. FOOD DETAIL & EDIT
const FoodDetail = ({ onBack, onAdd }) => {
    const [portion, setPortion] = useState(1);
    const baseCals = SCAN_RESULTS.calories;

    return (
        <div className="fixed inset-0 z-40 bg-slate-50 dark:bg-slate-950 flex flex-col animate-slide-up">
            {/* Hero Image */}
            <div className="relative h-[40vh] w-full">
                <img
                    src="https://images.unsplash.com/photo-1550547660-d9450f859349?w=800&q=80"
                    alt="Caesar Salad"
                    className="w-full h-full object-cover"
                />
                <div className="absolute inset-0 bg-gradient-to-b from-black/30 via-transparent to-slate-50 dark:to-slate-950" />

                <button onClick={onBack} className="absolute top-6 left-6 p-3 rounded-full bg-black/20 backdrop-blur-lg text-white hover:bg-black/30 transition-all">
                    <ChevronLeft size={24} />
                </button>
            </div>

            {/* Content */}
            <div className="flex-1 -mt-12 px-6 pb-6 overflow-y-auto relative z-10">
                <GlassCard className="p-6 mb-6 !bg-white/80 dark:!bg-slate-900/80 backdrop-blur-2xl">
                    <div className="flex justify-between items-start mb-2">
                        <div>
                            <h2 className="text-2xl font-bold dark:text-white">{SCAN_RESULTS.name}</h2>
                            <p className="text-slate-500 dark:text-slate-400">Lunch • High Protein</p>
                        </div>
                        <div className="flex flex-col items-end">
                            <span className="text-3xl font-bold bg-clip-text text-transparent bg-gradient-to-r from-orange-400 to-pink-500">
                                {Math.round(baseCals * portion)}
                            </span>
                            <span className="text-xs font-bold uppercase text-slate-400 tracking-wider">kcal</span>
                        </div>
                    </div>

                    {/* Macros Grid */}
                    <div className="grid grid-cols-3 gap-4 mt-6">
                        {[
                            { label: 'Protein', val: SCAN_RESULTS.macros.p, color: 'text-blue-500', bg: 'bg-blue-500/10' },
                            { label: 'Carbs', val: SCAN_RESULTS.macros.c, color: 'text-green-500', bg: 'bg-green-500/10' },
                            { label: 'Fat', val: SCAN_RESULTS.macros.f, color: 'text-orange-500', bg: 'bg-orange-500/10' }
                        ].map((m, i) => (
                            <div key={i} className={`rounded-2xl p-3 flex flex-col items-center justify-center gap-1 ${m.bg}`}>
                                <span className={`font-bold text-lg ${m.color}`}>{Math.round(m.val * portion)}g</span>
                                <span className="text-[10px] uppercase opacity-60 font-medium">{m.label}</span>
                            </div>
                        ))}
                    </div>
                </GlassCard>

                {/* Portion Slider */}
                <div className="mb-8 px-2">
                    <div className="flex justify-between items-center mb-4">
                        <span className="font-semibold dark:text-white">Portion Size</span>
                        <span className="text-blue-500 font-bold">{portion.toFixed(1)}x serving</span>
                    </div>

                    <div className="relative h-12 flex items-center">
                        <div className="absolute w-full h-2 bg-slate-200 dark:bg-slate-800 rounded-full overflow-hidden">
                            <div className="h-full bg-gradient-to-r from-blue-400 to-indigo-500 w-full origin-left" style={{ transform: `scaleX(${portion / 2})` }} />
                        </div>
                        <input
                            type="range"
                            min="0.5"
                            max="2.0"
                            step="0.1"
                            value={portion}
                            onChange={(e) => setPortion(parseFloat(e.target.value))}
                            className="absolute w-full h-full opacity-0 cursor-pointer z-20"
                        />
                        <div
                            className="absolute w-8 h-8 bg-white shadow-lg rounded-full border-2 border-blue-500 pointer-events-none transition-all duration-75 flex items-center justify-center z-10"
                            style={{ left: `calc(${(portion - 0.5) / 1.5 * 100}% - 16px)` }}
                        >
                            <div className="w-2 h-2 bg-blue-500 rounded-full" />
                        </div>
                    </div>
                </div>

                {/* Ingredients */}
                <div className="mb-24">
                    <h3 className="font-semibold mb-3 dark:text-white">Detected Ingredients</h3>
                    <div className="flex flex-wrap gap-2">
                        {SCAN_RESULTS.ingredients.map((ing, i) => (
                            <span key={i} className="px-3 py-1.5 rounded-lg bg-white dark:bg-slate-800 border border-slate-200 dark:border-slate-700 text-sm text-slate-600 dark:text-slate-300">
                                {ing}
                            </span>
                        ))}
                    </div>
                </div>
            </div>

            {/* Floating Action Button */}
            <div className="absolute bottom-0 left-0 right-0 p-6 bg-gradient-to-t from-slate-50 via-slate-50 to-transparent dark:from-slate-950 dark:via-slate-950 z-20">
                <GlassButton variant="primary" className="w-full py-4 text-lg" onClick={() => onAdd(Math.round(baseCals * portion))}>
                    Add to Log
                </GlassButton>
            </div>
        </div>
    );
};

// 4. STATISTICS
const Statistics = ({ history }) => {
    // Mock data for graph
    const weeklyData = [1800, 2100, 1950, 2400, 1600, 2000, 1850];
    const maxVal = Math.max(...weeklyData) * 1.2;
    const path = getBezierPath(weeklyData.map(v => v / maxVal), 150, 300);

    return (
        <div className="space-y-6 pb-24 pt-4 px-4">
            <div className="flex justify-between items-center px-2">
                <h1 className="text-2xl font-bold dark:text-white">Statistics</h1>
                <select className="bg-transparent text-sm font-medium outline-none dark:text-white">
                    <option>Last 7 Days</option>
                    <option>Last 30 Days</option>
                </select>
            </div>

            {/* Calorie Trend Chart */}
            <GlassCard className="p-6">
                <h3 className="text-sm font-medium opacity-60 mb-6">Calorie Intake Trend</h3>
                <div className="h-[200px] w-full flex items-end justify-between relative">
                    {/* Grid Lines */}
                    <div className="absolute inset-0 flex flex-col justify-between pointer-events-none">
                        {[1, 0.75, 0.5, 0.25, 0].map((v, i) => (
                            <div key={i} className="w-full h-px bg-slate-200 dark:bg-white/5 border-dashed" />
                        ))}
                    </div>

                    <svg className="absolute inset-0 w-full h-full overflow-visible" preserveAspectRatio="none" viewBox="0 0 300 150">
                        <defs>
                            <linearGradient id="gradientArea" x1="0" y1="0" x2="0" y2="1">
                                <stop offset="0%" stopColor="#87CEEB" stopOpacity="0.4" />
                                <stop offset="100%" stopColor="#87CEEB" stopOpacity="0" />
                            </linearGradient>
                        </defs>
                        {/* Area */}
                        <path d={`${path} L 300 150 L 0 150 Z`} fill="url(#gradientArea)" />
                        {/* Line */}
                        <path d={path} fill="none" stroke="#87CEEB" strokeWidth="4" strokeLinecap="round" className="drop-shadow-lg" />
                    </svg>

                    {/* X Axis Labels */}
                    <div className="absolute -bottom-6 left-0 right-0 flex justify-between text-xs opacity-50 font-mono">
                        {['Mon', 'Tue', 'Wed', 'Thu', 'Fri', 'Sat', 'Sun'].map((d, i) => (
                            <span key={i}>{d}</span>
                        ))}
                    </div>
                </div>
            </GlassCard>

            {/* Weight Tracker */}
            <div className="grid grid-cols-2 gap-4">
                <GlassCard className="p-4">
                    <div className="flex items-center gap-2 mb-2 text-pink-500">
                        <Target size={18} />
                        <span className="font-bold text-sm">Weight Goal</span>
                    </div>
                    <div className="text-2xl font-bold dark:text-white">65.0 <span className="text-sm font-normal opacity-50">kg</span></div>
                    <div className="mt-2 h-1.5 w-full bg-slate-200 dark:bg-slate-700 rounded-full">
                        <div className="h-full w-[70%] bg-pink-500 rounded-full" />
                    </div>
                    <p className="text-[10px] mt-2 opacity-60">3.5kg to go</p>
                </GlassCard>

                <GlassCard className="p-4">
                    <div className="flex items-center gap-2 mb-2 text-indigo-500">
                        <Droplet size={18} />
                        <span className="font-bold text-sm">Water</span>
                    </div>
                    <div className="text-2xl font-bold dark:text-white">1,250 <span className="text-sm font-normal opacity-50">ml</span></div>
                    <div className="flex gap-1 mt-3">
                        {[1, 2, 3, 4, 5].map(i => (
                            <div key={i} className={`h-6 flex-1 rounded-sm ${i <= 3 ? 'bg-indigo-400' : 'bg-slate-200 dark:bg-slate-700'}`} />
                        ))}
                    </div>
                </GlassCard>
            </div>

            {/* Achievement */}
            <GlassCard className="p-4 flex items-center gap-4 bg-gradient-to-r from-yellow-500/10 to-orange-500/10 border-orange-200/20">
                <div className="w-12 h-12 rounded-full bg-gradient-to-br from-yellow-300 to-orange-400 flex items-center justify-center text-white shadow-lg shadow-orange-500/30">
                    <Award size={24} />
                </div>
                <div>
                    <h4 className="font-bold text-slate-800 dark:text-white">7 Day Streak!</h4>
                    <p className="text-xs text-slate-500">You're on fire! Keep it up.</p>
                </div>
            </GlassCard>
        </div>
    );
};

// 5. EXPLORE
const Explore = () => {
    return (
        <div className="space-y-6 pb-24 pt-4 px-4">
            {/* Search Header */}
            <div className="sticky top-0 z-30 pt-2 pb-4 -mx-4 px-4 bg-slate-50/80 dark:bg-slate-950/80 backdrop-blur-md">
                <h1 className="text-2xl font-bold dark:text-white mb-4">Explore</h1>
                <div className="relative">
                    <Search className="absolute left-3 top-1/2 transform -translate-y-1/2 text-slate-400" size={18} />
                    <input
                        type="text"
                        placeholder="Search recipes, ingredients..."
                        className="w-full pl-10 pr-4 py-3 rounded-2xl bg-white dark:bg-slate-900 border border-slate-200 dark:border-slate-800 focus:outline-none focus:ring-2 focus:ring-blue-500/50 transition-all shadow-sm"
                    />
                </div>
            </div>

            {/* Categories */}
            <div className="flex gap-3 overflow-x-auto pb-2 scrollbar-hide">
                {['All', 'Breakfast', 'Vegan', 'Keto', 'Snacks', 'High Protein'].map((cat, i) => (
                    <button
                        key={i}
                        className={`
              whitespace-nowrap px-4 py-2 rounded-xl text-sm font-medium transition-all
              ${i === 0
                                ? 'bg-slate-800 text-white dark:bg-white dark:text-slate-900 shadow-md'
                                : 'bg-white dark:bg-slate-900 text-slate-600 dark:text-slate-300 border border-slate-200 dark:border-slate-800'}
            `}
                    >
                        {cat}
                    </button>
                ))}
            </div>

            {/* Masonry Grid */}
            <div className="columns-2 gap-4 space-y-4">
                {MOCK_RECIPES.map((recipe) => (
                    <div key={recipe.id} className="break-inside-avoid mb-4">
                        <GlassCard className="p-0 overflow-hidden group cursor-pointer">
                            <div className="h-32 bg-slate-200 dark:bg-slate-800 flex items-center justify-center text-4xl relative overflow-hidden">
                                <span className="transform group-hover:scale-125 transition-transform duration-300">{recipe.image}</span>
                                <div className="absolute inset-0 bg-black/10 group-hover:bg-transparent transition-colors" />
                                <div className="absolute top-2 right-2 px-2 py-1 bg-black/50 backdrop-blur-md rounded-lg text-[10px] text-white font-medium">
                                    {recipe.time}
                                </div>
                            </div>
                            <div className="p-3">
                                <div className="text-[10px] font-bold text-blue-500 uppercase tracking-wider mb-1">{recipe.tag}</div>
                                <h3 className="font-bold text-sm leading-tight mb-2 dark:text-white">{recipe.title}</h3>
                                <div className="flex justify-between items-center text-xs opacity-60">
                                    <span>{recipe.cals} kcal</span>
                                    <div className="w-5 h-5 rounded-full bg-slate-100 dark:bg-slate-700 flex items-center justify-center text-slate-400 group-hover:bg-blue-500 group-hover:text-white transition-colors">
                                        <Plus size={12} />
                                    </div>
                                </div>
                            </div>
                        </GlassCard>
                    </div>
                ))}
            </div>
        </div>
    );
};

// 6. PROFILE
const Profile = ({ darkMode, setDarkMode }) => {
    return (
        <div className="pb-24 pt-4 px-4">
            <div className="flex justify-between items-center mb-8 px-2">
                <h1 className="text-2xl font-bold dark:text-white">Profile</h1>
                <button className="p-2 rounded-full hover:bg-slate-100 dark:hover:bg-slate-800 transition-colors">
                    <Settings size={20} className="text-slate-600 dark:text-slate-400" />
                </button>
            </div>

            <div className="flex flex-col items-center mb-8">
                <div className="relative w-24 h-24 mb-4">
                    <div className="absolute inset-0 rounded-full border-4 border-slate-100 dark:border-slate-800" />
                    <svg className="absolute inset-0 w-full h-full -rotate-90">
                        <circle cx="48" cy="48" r="46" fill="none" stroke="#60A5FA" strokeWidth="4" strokeDasharray="289" strokeDashoffset="50" strokeLinecap="round" />
                    </svg>
                    <img
                        src="https://api.dicebear.com/7.x/avataaars/svg?seed=Felix"
                        alt="User"
                        className="w-full h-full rounded-full p-1.5"
                    />
                    <div className="absolute bottom-0 right-0 w-8 h-8 bg-white dark:bg-slate-900 rounded-full shadow-lg flex items-center justify-center border border-slate-100 dark:border-slate-800">
                        <span className="text-xs">✏️</span>
                    </div>
                </div>
                <h2 className="text-xl font-bold dark:text-white">Alex Morgan</h2>
                <p className="text-sm text-slate-500">Premium Member</p>
            </div>

            <div className="space-y-4">
                {/* Settings Groups */}
                <GlassCard className="divide-y divide-slate-100 dark:divide-slate-800">
                    <div className="p-4 flex items-center justify-between">
                        <div className="flex items-center gap-3">
                            <div className="p-2 rounded-xl bg-orange-100 text-orange-600 dark:bg-orange-900/30 dark:text-orange-400"><Target size={18} /></div>
                            <span className="font-medium dark:text-white">Goals</span>
                        </div>
                        <ChevronRight size={16} className="opacity-30" />
                    </div>
                    <div className="p-4 flex items-center justify-between">
                        <div className="flex items-center gap-3">
                            <div className="p-2 rounded-xl bg-green-100 text-green-600 dark:bg-green-900/30 dark:text-green-400"><Smartphone size={18} /></div>
                            <span className="font-medium dark:text-white">Devices & Apps</span>
                        </div>
                        <span className="text-xs text-green-500 font-medium">2 Connected</span>
                    </div>
                </GlassCard>

                <h3 className="text-xs font-bold uppercase text-slate-400 tracking-wider ml-2 mt-6 mb-2">Preferences</h3>
                <GlassCard className="divide-y divide-slate-100 dark:divide-slate-800">
                    <div className="p-4 flex items-center justify-between">
                        <div className="flex items-center gap-3">
                            <div className="p-2 rounded-xl bg-purple-100 text-purple-600 dark:bg-purple-900/30 dark:text-purple-400"><User size={18} /></div>
                            <span className="font-medium dark:text-white">Dietary Needs</span>
                        </div>
                        <ChevronRight size={16} className="opacity-30" />
                    </div>

                    <div className="p-4 flex items-center justify-between cursor-pointer" onClick={() => setDarkMode(!darkMode)}>
                        <div className="flex items-center gap-3">
                            <div className="p-2 rounded-xl bg-slate-100 text-slate-600 dark:bg-slate-800 dark:text-slate-400"><Zap size={18} /></div>
                            <span className="font-medium dark:text-white">Dark Mode</span>
                        </div>
                        <div className={`w-11 h-6 rounded-full transition-colors relative ${darkMode ? 'bg-blue-500' : 'bg-slate-200'}`}>
                            <div className={`absolute top-1 w-4 h-4 bg-white rounded-full transition-all shadow-sm ${darkMode ? 'left-6' : 'left-1'}`} />
                        </div>
                    </div>
                </GlassCard>

                <GlassButton variant="ghost" className="w-full mt-8 text-red-500 hover:bg-red-50 dark:hover:bg-red-900/10 hover:text-red-600">
                    Sign Out
                </GlassButton>
                <p className="text-center text-[10px] text-slate-400 mt-4">Version 1.0.0 (Build 204)</p>
            </div>
        </div>
    );
};

// NAV BAR
const Navigation = ({ activeTab, onTabChange, onScan }) => {
    const tabs = [
        { id: 'home', icon: Home, label: 'Home' },
        { id: 'stats', icon: PieChart, label: 'Stats' },
        { id: 'scan', icon: ScanLine, label: 'Scan', primary: true },
        { id: 'explore', icon: Compass, label: 'Explore' },
        { id: 'profile', icon: User, label: 'Profile' },
    ];

    return (
        <div className="fixed bottom-0 left-0 right-0 z-30 pb-safe">
            <div className="absolute inset-0 bg-white/80 dark:bg-slate-900/80 backdrop-blur-xl border-t border-white/20 dark:border-white/5 shadow-[0_-10px_40px_rgba(0,0,0,0.1)]" />

            <div className="relative flex justify-between items-center px-6 py-2 h-20">
                {tabs.map((tab) => {
                    const isActive = activeTab === tab.id;

                    if (tab.primary) {
                        return (
                            <div key={tab.id} className="relative -top-6 group">
                                <button
                                    onClick={onScan}
                                    className="w-16 h-16 rounded-full bg-slate-900 dark:bg-white text-white dark:text-slate-900 flex items-center justify-center shadow-lg shadow-slate-900/30 dark:shadow-white/20 transform transition-transform group-active:scale-95 group-hover:scale-105"
                                >
                                    <ScanLine size={24} />
                                </button>
                            </div>
                        );
                    }

                    return (
                        <button
                            key={tab.id}
                            onClick={() => onTabChange(tab.id)}
                            className={`flex flex-col items-center gap-1 transition-all duration-300 ${isActive ? 'text-slate-900 dark:text-white' : 'text-slate-400 hover:text-slate-600 dark:hover:text-slate-300'}`}
                        >
                            <div className={`p-1.5 rounded-xl transition-all ${isActive ? 'bg-slate-100 dark:bg-slate-800' : 'bg-transparent'}`}>
                                <tab.icon size={20} strokeWidth={isActive ? 2.5 : 2} />
                            </div>
                            <span className={`text-[10px] font-medium transition-all ${isActive ? 'opacity-100 scale-100' : 'opacity-0 scale-0 hidden'}`}>
                                {tab.label}
                            </span>
                        </button>
                    );
                })}
            </div>
        </div>
    );
};

/**
 * -----------------------------------------------------------------------
 * MAIN APP COMPONENT
 * -----------------------------------------------------------------------
 */

export default function App() {
    const [screen, setScreen] = useState('home');
    const [darkMode, setDarkMode] = useState(false);
    const [showCamera, setShowCamera] = useState(false);
    const [showFoodDetail, setShowFoodDetail] = useState(false);

    // App State
    const [calories, setCalories] = useState({ consumed: 840, target: 2000 });
    const [macros, setMacros] = useState({ protein: 45, carbs: 120, fat: 35 });
    const [history, setHistory] = useState(MOCK_HISTORY);

    // Loading state simulation
    const [loading, setLoading] = useState(true);

    useEffect(() => {
        // Simulate initial loading for smooth entry
        const timer = setTimeout(() => setLoading(false), 1500);
        return () => clearTimeout(timer);
    }, []);

    useEffect(() => {
        // Add dark mode class to html element
        if (darkMode) {
            document.documentElement.classList.add('dark');
        } else {
            document.documentElement.classList.remove('dark');
        }
    }, [darkMode]);

    const handleScanComplete = () => {
        setShowCamera(false);
        setShowFoodDetail(true);
    };

    const handleAddFood = (cal) => {
        // Add animation delay then switch
        setCalories(prev => ({ ...prev, consumed: prev.consumed + cal }));
        setHistory(prev => [
            {
                id: Date.now(),
                name: SCAN_RESULTS.name,
                time: new Date().toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' }),
                calories: cal,
                type: 'lunch',
                protein: SCAN_RESULTS.macros.p,
                carbs: SCAN_RESULTS.macros.c,
                fat: SCAN_RESULTS.macros.f
            },
            ...prev
        ]);
        setShowFoodDetail(false);
        setScreen('home');
    };

    if (loading) {
        return (
            <div className={`flex items-center justify-center h-screen w-screen ${darkMode ? 'bg-slate-950' : 'bg-slate-50'}`}>
                <div className="flex flex-col items-center animate-pulse">
                    <div className="w-20 h-20 rounded-[2rem] bg-gradient-to-br from-orange-400 to-pink-500 shadow-2xl flex items-center justify-center mb-6">
                        <Flame size={40} className="text-white" />
                    </div>
                    <h1 className="text-2xl font-bold tracking-tight text-slate-800 dark:text-white">LuminaCal</h1>
                </div>
            </div>
        );
    }

    return (
        <div className={`min-h-screen w-full relative overflow-hidden font-sans selection:bg-pink-500/30 ${darkMode ? 'dark text-white' : 'text-slate-900'}`}>
            <style>{`
        @keyframes blob {
          0% { transform: translate(0px, 0px) scale(1); }
          33% { transform: translate(30px, -50px) scale(1.1); }
          66% { transform: translate(-20px, 20px) scale(0.9); }
          100% { transform: translate(0px, 0px) scale(1); }
        }
        .animate-blob {
          animation: blob 7s infinite;
        }
        .animation-delay-2000 { animation-delay: 2s; }
        .animation-delay-4000 { animation-delay: 4s; }
        .pb-safe { padding-bottom: env(safe-area-inset-bottom, 20px); }
        
        @keyframes scan {
          0% { top: 0%; opacity: 0; }
          10% { opacity: 1; }
          90% { opacity: 1; }
          100% { top: 100%; opacity: 0; }
        }
        .animate-scan { animation: scan 2s linear infinite; }
        
        @keyframes pop-in {
          0% { transform: translate(-50%, 20px) scale(0.8); opacity: 0; }
          100% { transform: translate(-50%, 0) scale(1); opacity: 1; }
        }
        .animate-pop-in { animation: pop-in 0.4s cubic-bezier(0.175, 0.885, 0.32, 1.275) forwards; }
        
        @keyframes slide-up {
          0% { transform: translateY(100%); }
          100% { transform: translateY(0); }
        }
        .animate-slide-up { animation: slide-up 0.4s cubic-bezier(0.16, 1, 0.3, 1) forwards; }

        @keyframes fade-in-up {
           0% { opacity: 0; transform: translateY(10px); }
           100% { opacity: 1; transform: translateY(0); }
        }
        .animate-fade-in-up { animation: fade-in-up 0.5s ease-out forwards; }
        
        .scrollbar-hide::-webkit-scrollbar { display: none; }
      `}</style>

            {/* Dynamic Background */}
            <MeshBackground darkMode={darkMode} />

            {/* Main Content Area */}
            <main className="relative z-10 max-w-md mx-auto h-full min-h-screen">
                {screen === 'home' && (
                    <Dashboard
                        onCameraOpen={() => setShowCamera(true)}
                        history={history}
                        calories={calories}
                        macros={macros}
                        dailyGoal={calories.target}
                    />
                )}
                {screen === 'stats' && <Statistics history={history} />}
                {screen === 'explore' && <Explore />}
                {screen === 'profile' && <Profile darkMode={darkMode} setDarkMode={setDarkMode} />}
            </main>

            {/* Modals & Overlays */}
            {showCamera && (
                <CameraScanner
                    onClose={() => setShowCamera(false)}
                    onScanComplete={handleScanComplete}
                />
            )}

            {showFoodDetail && (
                <FoodDetail
                    onBack={() => { setShowFoodDetail(false); setShowCamera(true); }}
                    onAdd={handleAddFood}
                />
            )}

            {/* Bottom Navigation */}
            {!showCamera && !showFoodDetail && (
                <Navigation
                    activeTab={screen}
                    onTabChange={setScreen}
                    onScan={() => setShowCamera(true)}
                />
            )}
        </div>
    );
}