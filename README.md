
# Shrine User Education Animation - (Material Design 2)

A simple prototype for [Shrine](https://m2.material.io/design/material-studies/shrine.html#motion) User Education animation.

![Shrine User Education Animation.](assets/shrine_animation.mp4)

## Content

The app containt 2 same animations:

1- A Box with background that can be anything (exp: product image)

2- A render of the item that has been redirectred to an Android Picture, then create a Bitmap out of it (onItemClick) and use it in Image and make it the moving object. This needs implementation of [Ui.Graphics v1.6.0-alpha01](https://developer.android.com/jetpack/androidx/releases/compose-ui#1.6.0-alpha01)


## Result
The Bezier Curve used.

![The Bezier Curve used.](assets/bezier_curve_used.jpg)

*Notice item index*

![Example 1.](assets/shrine_animation.mp4)

![Example 2 with redirect rendering.](assets/shrine_animation.mp4)

## What I learned

- Bezier Curve
- Redirecting rendering to an Android Picture 


## Learning resource

- [Easing in to Easing Curves in Jetpack Compose](https://medium.com/androiddevelopers/easing-in-to-easing-curves-in-jetpack-compose-d72893eeeb4d)
- [The Beauty of Bézier Curves - Youtube](https://www.youtube.com/watch?v=aVwxzDHniEw)
- [Bezier Curve Playground- Desmos](https://www.desmos.com/calculator/d1ofwre0fr)
- [Redirecting Rendering](https://github.com/android/snippets/blob/main/compose/snippets/src/main/java/com/example/compose/snippets/graphics/AdvancedGraphicsSnippets.kt#L92)


