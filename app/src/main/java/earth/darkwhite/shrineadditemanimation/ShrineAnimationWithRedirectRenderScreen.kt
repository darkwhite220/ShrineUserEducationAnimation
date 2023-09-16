package earth.darkwhite.shrineadditemanimation

import android.graphics.Bitmap
import android.graphics.Picture
import android.os.Build
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.EaseInOutSine
import androidx.compose.animation.core.EaseInSine
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animate
import androidx.compose.animation.core.keyframes
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Canvas
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.drawscope.draw
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInRoot
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.round
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


/** Simple Bezier Curve Animation with Redirect Rendering implementation */
@Composable
fun ShrineAnimationWithRedirectRenderScreen() {
  val lazyListState = rememberLazyListState()
  val density = LocalDensity.current
  var initialOffset by remember { mutableStateOf(Offset(0f, 0f)) }
  var initAnimation by remember { mutableStateOf(false) }
  
  ShrineAnimationWithRedirectRender(
    modifier = Modifier,
    lazyListState = lazyListState,
    initialOffset = initialOffset,
    density = density,
    initAnimation = initAnimation,
    onItemClick = { index, itemYSize, largeDpPx, lazyOffset ->
      val offsetInLazy = index - lazyListState.firstVisibleItemIndex
      val x = lazyOffset.x - largeDpPx
      val y = lazyOffset.y - largeDpPx - lazyListState.firstVisibleItemScrollOffset + (itemYSize * offsetInLazy)
      initialOffset = Offset(x, y)
      initAnimation = true
    },
    resetAnimation = {
      initAnimation = false
    }
  )
}

@Composable
fun ShrineAnimationWithRedirectRender(
  modifier: Modifier = Modifier,
  lazyListState: LazyListState,
  density: Density,
  initialOffset: Offset,
  initAnimation: Boolean,
  onItemClick: (Int, Float, Float, Offset) -> Unit,
  resetAnimation: () -> Unit
) {
  val list = mutableListOf<Color>(Color.LightGray, Color.Red, Color.Blue, Color.Green)
  repeat(3) { list.addAll(list) }
  val mediumDp by remember { mutableStateOf(8) }
  val largeDp by remember { mutableStateOf(16) }
  val largeDpPx = with(density) { largeDp.dp.toPx() }
  val mediumDpPx = with(density) { mediumDp.dp.toPx() }
  val itemSize = 150
  
  val animationDuration by remember { mutableStateOf(800) }
  val scaleDownTo by remember { mutableStateOf(.4f) }
  val scaleDownDp by remember { mutableStateOf(scaleDownTo * itemSize) }
  val scaleDownPx by remember { mutableStateOf(with(density) { (itemSize - scaleDownDp).dp.toPx() }) }
  val itemHeight = with(density) { itemSize.dp.toPx() }
  
  var lazyOffset by remember { mutableStateOf(Offset(0f, 0f)) }
  var targetOffset by remember { mutableStateOf(Offset(0f, 0f)) }
  
  var shadow by remember { mutableStateOf(0f) }
  var corner by remember { mutableStateOf(0f) }
  var scale by remember { mutableStateOf(1f) }
  var xOffset by remember { mutableStateOf(initialOffset.x) }
  var yOffset by remember { mutableStateOf(initialOffset.y) }
  
  // Method 1
  val p0 = Offset(initialOffset.x, initialOffset.y)
  val p1 = Offset(targetOffset.x - (scaleDownPx / 2), initialOffset.y + (scaleDownPx / 2))
  val p2 = Offset(targetOffset.x - (scaleDownPx / 2), initialOffset.y + (scaleDownPx / 2))
  val p3 = Offset(targetOffset.x - (scaleDownPx / 2), targetOffset.y - (scaleDownPx / 2))
  
  val pointA = lerp(xOffset, yOffset, p0, p1)
  val pointB = lerp(xOffset, yOffset, p1, p2)
  val pointC = lerp(xOffset, yOffset, p2, p3)
  val pointD = lerp(xOffset, yOffset, pointA, pointB)
  val pointE = lerp(xOffset, yOffset, pointB, pointC)
  val endPosition = lerp(xOffset, yOffset, pointD, pointE)
  
  // Method 2
//  val x = initialOffset.x * (-xOffset.pow(3) + 3 * xOffset.pow(2) - 3 * xOffset + 1) +
//    (targetOffset.x - (scaleDownPx / 2)) * (3 * xOffset.pow(3) - 6 * xOffset.pow(2) + 3 * xOffset) +
//    (targetOffset.x - (scaleDownPx / 2)) * (-3 * xOffset.pow(3) + 3 * xOffset.pow(2)) +
//    (targetOffset.x - (scaleDownPx / 2)) * (xOffset.pow(3))
//  val y = initialOffset.y * (-yOffset.pow(3) + 3 * yOffset.pow(2) - 3 * yOffset + 1) +
//    (targetOffset.y - (scaleDownPx / 2)) * (3 * yOffset.pow(3) - 6 * yOffset.pow(2) + 3 * yOffset) +
//    (targetOffset.y - (scaleDownPx / 2)) * (-3 * yOffset.pow(3) + 3 * yOffset.pow(2)) +
//    (targetOffset.y - (scaleDownPx / 2)) * (yOffset.pow(3))
//  val endPosition = Offset(x,y)
  
  val coroutineScope = rememberCoroutineScope()
  var bitmap by remember { mutableStateOf<Bitmap?>(null) }
  
  LaunchedEffect(key1 = initAnimation) {
    if (initAnimation) {
      delay(animationDuration.toLong())
      resetAnimation()
    }
  }
  Box(modifier = Modifier.fillMaxSize()) {
    LazyColumn(
      state = lazyListState,
      modifier = modifier
        .padding(largeDp.dp)
        .onGloballyPositioned { layoutCoordinator ->
          lazyOffset = Offset(layoutCoordinator.positionInRoot().x, layoutCoordinator.positionInRoot().y)
        },
      verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
      itemsIndexed(items = list) { index, it ->
        val picture = remember { Picture() }
        Box(
          modifier = Modifier
            .width(itemSize.dp)
            .height(itemSize.dp)
            .drawWithCache {
              // Example that shows how to redirect rendering to an Android Picture and then
              // draw the picture into the original destination
              val width = this.size.width.toInt()
              val height = this.size.height.toInt()
              onDrawWithContent {
                val pictureCanvas =
                  Canvas(
                    picture.beginRecording(
                      width,
                      height
                    )
                  )
      
                draw(this, this.layoutDirection, pictureCanvas, this.size) {
                  this@onDrawWithContent.drawContent()
                }
                picture.endRecording()
      
                drawIntoCanvas { canvas -> canvas.nativeCanvas.drawPicture(picture) }
              }
            }
            .background(it)
            .noRippleClickable {
              coroutineScope.launch(Dispatchers.IO) {
                bitmap = createBitmapFromPicture(picture)
              }
              val itemYSize = itemHeight + mediumDpPx
              onItemClick(
                index,
                itemYSize,
                largeDpPx,
                lazyOffset
              )
            },
          contentAlignment = Alignment.Center
        ) {
          Text(text = "$index", style = MaterialTheme.typography.headlineLarge)
        }
      }
    }
    Box(
      modifier = Modifier
        .width(scaleDownDp.plus(32).dp)
        .height(scaleDownDp.plus(32).dp)
        .align(Alignment.BottomEnd)
        .onGloballyPositioned {
          targetOffset = Offset(it.positionInRoot().x, it.positionInRoot().y)
          println("EndPosition $targetOffset")
        }
        .background(Color.DarkGray)
    ) {
      Box(
        modifier = Modifier
          .padding(largeDp.dp)
          .size(itemSize.dp)
          .clip(RoundedCornerShape(largeDp.dp))
          .border(1.dp, Color.Black, shape = RoundedCornerShape(largeDp.dp))
      )
      if (initAnimation.not()) {
        bitmap?.let {
          Image(
            bitmap = it.asImageBitmap(),
            modifier = Modifier
              .padding(largeDp.dp)
              .clip(RoundedCornerShape(largeDp.dp))
              .size(scaleDownDp.dp),
            contentDescription = null
          )
        }
      }
    }
    AnimatedVisibility(
      visible = initAnimation,
      enter = fadeIn(
        initialAlpha = 0f,
        animationSpec = tween(durationMillis = 0)
      ),
      exit = fadeOut(
        targetAlpha = 1f
      ),
      label = "initAnimation"
    ) {
      bitmap?.let {
        Image(
          bitmap = it.asImageBitmap(),
          modifier = Modifier
            .padding(largeDp.dp)
            .size(itemSize.dp)
            .offset { endPosition.round() }
            .scale(scale)
            .graphicsLayer {
              shadowElevation = shadow
              shape = RoundedCornerShape(corner.dp)
            }
            .clip(RoundedCornerShape(corner.dp)),
          contentDescription = null
        )
      }
    }
  }
  
  LaunchedEffect(initAnimation) {
    launch {
      animate(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = //tween(animationDuration, easing = LinearEasing)
        keyframes {
          durationMillis = animationDuration
          0f at animationDuration / 3
          1f at animationDuration - (animationDuration / 5) with LinearEasing
        }
      ) { value, /* velocity */ _ ->
        xOffset = value
      }
    }
    launch {
      animate(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = //tween(animationDuration, easing = EaseInSine)
        keyframes {
          durationMillis = animationDuration
          0f at animationDuration / 3
          1f at animationDuration - (animationDuration / 10) with EaseInSine
        }
      ) { value, /* velocity */ _ ->
        yOffset = value
      }
    }
    launch {
      animate(
        initialValue = 1f,
        targetValue = 0f,
        animationSpec = tween(animationDuration, easing = EaseInOutSine)
      ) { value, /* velocity */ _ ->
        shadow = value * largeDpPx
      }
    }
    launch {
      animate(
        initialValue = 1f,
        targetValue = scaleDownTo,
        animationSpec = //tween(animationDuration)
        keyframes {
          durationMillis = animationDuration
          1f at animationDuration / 3
          scaleDownTo at animationDuration / 2
        }
      ) { value, /* velocity */ _ ->
        scale = value
      }
    }
    launch {
      animate(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = keyframes {
          durationMillis = animationDuration
          0f at animationDuration / 3
          1f at animationDuration / 1
        }
      ) { value, /* velocity */ _ ->
        corner = value * largeDpPx
      }
    }
  }
}

private fun Modifier.noRippleClickable(onClick: () -> Unit): Modifier = composed {
  clickable(
    indication = null,
    interactionSource = remember { MutableInteractionSource() }
  ) {
    onClick()
  }
}

private fun lerp(
  horizontalProgress: Float,
  verticalProgress: Float,
  pointA: Offset,
  pointB: Offset
): Offset {
  val x = ((1 - horizontalProgress) * pointA.x) + horizontalProgress * pointB.x
  val y = ((1 - verticalProgress) * pointA.y) + verticalProgress * pointB.y
  
  return Offset(x, y)
}

private fun createBitmapFromPicture(picture: Picture): Bitmap {
  val bitmap = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
    Bitmap.createBitmap(picture)
  } else {
    val bitmap = Bitmap.createBitmap(
      picture.width,
      picture.height,
      Bitmap.Config.ARGB_8888
    )
    val canvas = android.graphics.Canvas(bitmap)
    canvas.drawColor(android.graphics.Color.WHITE)
    canvas.drawPicture(picture)
    bitmap
  }
  return bitmap
}