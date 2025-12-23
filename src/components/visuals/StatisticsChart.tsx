/**
 * StatisticsChart - Area chart using Skia paths
 * Replicates the bezier curve chart from the web version.
 */

import React from 'react';
import { StyleSheet, View, Text } from 'react-native';
import {
    Canvas,
    Path,
    Skia,
    LinearGradient,
    vec,
    Line,
} from '@shopify/react-native-skia';
import { getBezierPath } from '../../utils';

interface StatisticsChartProps {
    data: number[];
    labels?: string[];
    width?: number;
    height?: number;
    lineColor?: string;
    gradientColors?: [string, string];
    darkMode?: boolean;
}

export const StatisticsChart: React.FC<StatisticsChartProps> = ({
    data,
    labels = ['Mon', 'Tue', 'Wed', 'Thu', 'Fri', 'Sat', 'Sun'],
    width = 300,
    height = 150,
    lineColor = '#87CEEB',
    gradientColors = ['rgba(135, 206, 235, 0.4)', 'rgba(135, 206, 235, 0)'],
    darkMode = false,
}) => {
    const maxVal = Math.max(...data) * 1.2;
    const normalizedData = data.map((v) => v / maxVal);

    // Generate SVG path string
    const pathString = getBezierPath(normalizedData, height, width);

    // Create Skia paths
    const linePath = React.useMemo(() => {
        if (!pathString) return null;
        return Skia.Path.MakeFromSVGString(pathString);
    }, [pathString]);

    const areaPath = React.useMemo(() => {
        if (!pathString) return null;
        const areaString = `${pathString} L ${width} ${height} L 0 ${height} Z`;
        return Skia.Path.MakeFromSVGString(areaString);
    }, [pathString, width, height]);

    const gridColor = darkMode ? 'rgba(255, 255, 255, 0.05)' : 'rgba(0, 0, 0, 0.05)';
    const textColor = darkMode ? 'rgba(255, 255, 255, 0.5)' : 'rgba(0, 0, 0, 0.5)';

    // Grid line positions
    const gridLines = [0, 0.25, 0.5, 0.75, 1];

    return (
        <View style={[styles.container, { width, height: height + 30 }]}>
            <View style={[styles.chartArea, { width, height }]}>
                <Canvas style={StyleSheet.absoluteFill}>
                    {/* Grid Lines */}
                    {gridLines.map((ratio, i) => (
                        <Line
                            key={i}
                            p1={vec(0, height * ratio)}
                            p2={vec(width, height * ratio)}
                            color={gridColor}
                            strokeWidth={1}
                            style="stroke"
                        />
                    ))}

                    {/* Gradient Area Fill */}
                    {areaPath && (
                        <Path path={areaPath} style="fill">
                            <LinearGradient
                                start={vec(0, 0)}
                                end={vec(0, height)}
                                colors={gradientColors}
                            />
                        </Path>
                    )}

                    {/* Line */}
                    {linePath && (
                        <Path
                            path={linePath}
                            style="stroke"
                            strokeWidth={4}
                            strokeCap="round"
                            strokeJoin="round"
                            color={lineColor}
                        />
                    )}
                </Canvas>
            </View>

            {/* X-Axis Labels */}
            <View style={styles.labels}>
                {labels.map((label, i) => (
                    <Text key={i} style={[styles.labelText, { color: textColor }]}>
                        {label}
                    </Text>
                ))}
            </View>
        </View>
    );
};

const styles = StyleSheet.create({
    container: {
        position: 'relative',
    },
    chartArea: {
        position: 'relative',
    },
    labels: {
        flexDirection: 'row',
        justifyContent: 'space-between',
        paddingTop: 8,
    },
    labelText: {
        fontSize: 10,
        fontFamily: 'monospace',
    },
});

export default StatisticsChart;
