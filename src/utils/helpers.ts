/**
 * Utility Functions for LuminaCal
 * Mathematical and helper functions.
 */

/**
 * Calculate progress percentage.
 */
export const calculateProgress = (current: number, target: number): number => {
    return Math.min(100, (current / target) * 100);
};

/**
 * Generate a smooth cubic bezier path for charts.
 * @param points - Array of normalized values (0-1).
 * @param height - Chart height.
 * @param width - Chart width.
 * @returns SVG path string.
 */
export const getBezierPath = (points: number[], height: number, width: number): string => {
    if (points.length === 0) return '';

    const scaleX = (index: number) => (index / (points.length - 1)) * width;
    const scaleY = (value: number) => height - value * height;

    let path = `M ${scaleX(0)} ${scaleY(points[0])}`;

    for (let i = 0; i < points.length - 1; i++) {
        const x0 = scaleX(i);
        const y0 = scaleY(points[i]);
        const x1 = scaleX(i + 1);
        const y1 = scaleY(points[i + 1]);

        // Control points for smooth curve
        const cp1x = x0 + (x1 - x0) * 0.5;
        const cp1y = y0;
        const cp2x = x1 - (x1 - x0) * 0.5;
        const cp2y = y1;

        path += ` C ${cp1x} ${cp1y}, ${cp2x} ${cp2y}, ${x1} ${y1}`;
    }

    return path;
};

/**
 * Format time to 12-hour format.
 */
export const formatTime = (date: Date): string => {
    return date.toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' });
};

/**
 * Clamp a value between min and max.
 */
export const clamp = (value: number, min: number, max: number): number => {
    return Math.min(Math.max(value, min), max);
};
