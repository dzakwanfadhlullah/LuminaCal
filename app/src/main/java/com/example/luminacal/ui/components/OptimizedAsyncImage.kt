package com.example.luminacal.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BrokenImage
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImagePainter
import coil.compose.SubcomposeAsyncImage
import coil.compose.SubcomposeAsyncImageContent
import coil.request.ImageRequest
import com.example.luminacal.util.ImageLoaderFactory

/**
 * Optimized AsyncImage with shimmer loading placeholder and error fallback
 */
@Composable
fun OptimizedAsyncImage(
    imageUrl: String?,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    contentScale: ContentScale = ContentScale.Crop,
    cornerRadius: Dp = 12.dp,
    errorIcon: ImageVector = Icons.Default.Restaurant,
    showShimmerOnLoading: Boolean = true
) {
    val context = LocalContext.current
    
    SubcomposeAsyncImage(
        model = ImageRequest.Builder(context)
            .data(imageUrl)
            .crossfade(true)
            .build(),
        imageLoader = ImageLoaderFactory.getInstance(context),
        contentDescription = contentDescription,
        modifier = modifier.clip(RoundedCornerShape(cornerRadius)),
        contentScale = contentScale
    ) {
        when (painter.state) {
            is AsyncImagePainter.State.Loading -> {
                if (showShimmerOnLoading) {
                    ShimmerEffect(
                        modifier = Modifier.fillMaxSize(),
                        cornerRadius = cornerRadius
                    )
                }
            }
            is AsyncImagePainter.State.Error -> {
                ErrorPlaceholder(
                    icon = errorIcon,
                    modifier = Modifier.fillMaxSize()
                )
            }
            else -> {
                SubcomposeAsyncImageContent()
            }
        }
    }
}

/**
 * Error placeholder with icon
 */
@Composable
private fun ErrorPlaceholder(
    icon: ImageVector,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = "Image load failed",
            tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f),
            modifier = Modifier.fillMaxSize(0.4f)
        )
    }
}

/**
 * Food-specific image with appropriate error fallback
 */
@Composable
fun FoodAsyncImage(
    imageUrl: String?,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    contentScale: ContentScale = ContentScale.Crop,
    cornerRadius: Dp = 12.dp
) {
    OptimizedAsyncImage(
        imageUrl = imageUrl,
        contentDescription = contentDescription,
        modifier = modifier,
        contentScale = contentScale,
        cornerRadius = cornerRadius,
        errorIcon = Icons.Default.Restaurant
    )
}

/**
 * Recipe-specific image
 */
@Composable
fun RecipeAsyncImage(
    imageUrl: String?,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    contentScale: ContentScale = ContentScale.Crop
) {
    OptimizedAsyncImage(
        imageUrl = imageUrl,
        contentDescription = contentDescription,
        modifier = modifier,
        contentScale = contentScale,
        cornerRadius = 0.dp,
        errorIcon = Icons.Default.Restaurant
    )
}
